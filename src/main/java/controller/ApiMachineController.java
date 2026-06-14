package controller;

import com.google.gson.Gson;
import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.LuotChoiDAO;
import dao.MayPSDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.LuotChoi;
import model.MayPS;
import model.NhanVien;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/api/machines"})
@SuppressWarnings("unused")
public class ApiMachineController extends HttpServlet {
    private final MayPSDAO mayPSDAO = new MayPSDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<MayPS> machines = mayPSDAO.getAllMay();
        List<Map<String, Object>> result = new java.util.ArrayList<>();

        for (MayPS machine : machines) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", machine.getId());
            item.put("tenmay", machine.getTenMay());
            item.put("tinhtrang", machine.getTinhTrang());
            item.put("ghichu", machine.getGhiChu());

            Optional<LuotChoi> activeSessionOpt = luotChoiDAO.findOpenByMayId(machine.getId());
            if (activeSessionOpt.isPresent()) {
                LuotChoi session = activeSessionOpt.get();
                double estimatedCost = calculateEstimatedCost(session);
                long minutesElapsed = calculateEstimatedMinutes(session, estimatedCost);

                item.put("luotchoiId", session.getId());
                item.put("thoiGianBatDau", session.getThoiGianBatDau().getTime());
                item.put("thoiGianKetThuc", session.getThoiGianKetThuc() == null ? null : session.getThoiGianKetThuc().getTime());
                item.put("minutesElapsed", minutesElapsed);
                item.put("estimatedCost", estimatedCost);
                item.put("donGiaGio", session.getDonGiaGio());
                item.put("trangThaiLuotChoi", session.getTrangThai());

                // Lấy thông tin hóa đơn và dịch vụ đã gọi
                Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(session.getId());
                if (hoaDonOpt.isPresent()) {
                    HoaDon hoaDon = hoaDonOpt.get();
                    List<ChiTietHoaDon> details = chiTietHoaDonDAO.getByHoaDonId(hoaDon.getId());
                    item.put("dichVuDaGoi", details);
                    item.put("tienDichVu", hoaDon.getTienDichVu());
                } else {
                    item.put("dichVuDaGoi", List.of());
                    item.put("tienDichVu", 0);
                }
            } else {
                item.put("luotchoiId", null);
                item.put("thoiGianBatDau", null);
                item.put("thoiGianKetThuc", null);
                item.put("minutesElapsed", 0);
                item.put("estimatedCost", 0);
                item.put("donGiaGio", 0);
                item.put("trangThaiLuotChoi", null);
                item.put("dichVuDaGoi", List.of());
                item.put("tienDichVu", 0);
            }

            result.add(item);
        }

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = valueOf(request.getParameter("action")).trim().toLowerCase();

        if ("create".equals(action)) {
            handleCreate(request, response);
            return;
        }

        int machineId = parseInt(request.getParameter("id"), -1);
        if (machineId <= 0) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                    "success", false,
                    "message", "Thieu id may"
            ));
            return;
        }

        Optional<MayPS> machineOpt = mayPSDAO.findById(machineId);
        if (machineOpt.isEmpty()) {
            write(response, HttpServletResponse.SC_NOT_FOUND, Map.of(
                    "success", false,
                    "message", "Khong tim thay may"
            ));
            return;
        }

        if ("open".equals(action)) {
            handleOpen(request, response, machineOpt.get());
            return;
        }

        if ("close".equals(action)) {
            handleClose(response, machineOpt.get());
            return;
        }

        if ("pause".equals(action)) {
            handlePause(response, machineOpt.get());
            return;
        }

        if ("resume".equals(action)) {
            handleResume(response, machineOpt.get());
            return;
        }

        write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                "success", false,
                "message", "Action khong hop le"
        ));
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = valueOf(request.getParameter("tenMay")).trim();
        String note = valueOf(request.getParameter("ghiChu")).trim();

        if (name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                    "success", false,
                    "message", "Ten may khong duoc de trong"
            ));
            return;
        }

        try {
            String sql = "INSERT INTO mayps (tenmay, tinhtrang, ghichu) VALUES (?, 'BINH_THUONG', ?)";
            try (Connection conn = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, note.isEmpty() ? null : note);
                ps.executeUpdate();

                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        MayPS newMachine = mayPSDAO.findById(newId).orElse(new MayPS(newId, name, "BINH_THUONG", note));
                        write(response, HttpServletResponse.SC_CREATED, Map.of(
                                "success", true,
                                "message", "Da them may moi thanh cong",
                                "machine", newMachine
                        ));
                    } else {
                        write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                                "success", false,
                                "message", "Khong the lay ID may moi"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Them may that bai: " + e.getMessage()
            ));
        }
    }

    private void handleOpen(HttpServletRequest request, HttpServletResponse response, MayPS machine) throws IOException {
        if (isMachineInUse(machine)) {
            write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                    "success", false,
                    "message", "May dang duoc mo roi"
            ));
            return;
        }

        int staffId = resolveNhanVienId(request);
        double donGiaGio = parseDouble(request.getParameter("donGiaGio"), 30000d);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean started = luotChoiDAO.batDauLuotChoi(conn, machine.getId(), staffId, donGiaGio);
                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "DANG_CHOI");
                if (!started || !updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                            "success", false,
                            "message", "Khong the mo may"
                    ));
                    return;
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong the mo may: " + e.getMessage()
            ));
            return;
        }

        int sessionId = luotChoiDAO.findActiveByMayId(machine.getId()).map(session -> session.getId()).orElse(-1);
        write(response, HttpServletResponse.SC_OK, Map.of(
                "success", true,
                "message", "Da mo may",
                "machine", mayPSDAO.findById(machine.getId()).orElse(machine),
                "sessionId", sessionId
        ));
    }

    private void handleClose(HttpServletResponse response, MayPS machine) throws IOException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<LuotChoi> activeSessionOpt = luotChoiDAO.findOpenByMayId(machine.getId());
                if (activeSessionOpt.isEmpty()) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_CONFLICT, Map.of("success", false, "message", "Máy chưa có lượt chơi đang hoạt động"));
                    return;
                }
                LuotChoi activeSession = activeSessionOpt.get();

                // End the session and calculate playing time cost
                double totalHoursMoney = luotChoiDAO.ketThucLuotChoi(conn, machine.getId()).orElse(0.0);

                // Find or create an invoice
                Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(conn, activeSession.getId());
                HoaDon hoaDon;
                if (hoaDonOpt.isEmpty()) {
                    hoaDonDAO.create(conn, activeSession.getId(), totalHoursMoney, 0, 0, totalHoursMoney);
                    hoaDon = hoaDonDAO.findByLuotChoiId(conn, activeSession.getId()).orElseThrow(() -> new SQLException("Failed to create invoice"));
                } else {
                    hoaDon = hoaDonOpt.get();
                }

                // Update invoice with final costs
                hoaDonDAO.updateTienChoi(conn, hoaDon.getId(), totalHoursMoney);
                hoaDonDAO.updateTongTien(conn, hoaDon.getId());

                double tienDichVu = hoaDon.getTienDichVu();
                double tienKhuyenMai = hoaDon.getTienKhuyenMai();
                double tongTien = Math.max(0, totalHoursMoney + tienDichVu - tienKhuyenMai);
                long minutesElapsed = calculateEstimatedMinutes(activeSession, totalHoursMoney);

                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "BINH_THUONG");
                if (!updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("success", false, "message", "Không thể đóng máy"));
                    return;
                }

                conn.commit();

                Map<String, Object> payload = new java.util.HashMap<>();
                payload.put("success", true);
                payload.put("message", "Đã đóng máy");
                payload.put("totalHoursMoney", totalHoursMoney);
                payload.put("sessionId", activeSession.getId());
                payload.put("luotChoiId", activeSession.getId());
                payload.put("hoaDonId", hoaDon.getId());
                payload.put("tienChoi", totalHoursMoney);
                payload.put("tienDichVu", tienDichVu);
                payload.put("tienKhuyenMai", tienKhuyenMai);
                payload.put("tongTien", tongTien);
                payload.put("minutesElapsed", minutesElapsed);
                payload.put("donGiaGio", activeSession.getDonGiaGio());
                payload.put("machine", mayPSDAO.findById(machine.getId()).orElse(machine));
                write(response, HttpServletResponse.SC_OK, payload);

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("success", false, "message", "Không thể đóng máy: " + e.getMessage()));
        }
    }

    private void handlePause(HttpServletResponse response, MayPS machine) throws IOException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Double> pausedCost = luotChoiDAO.pauseLuotChoi(conn, machine.getId());
                if (pausedCost.isEmpty()) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                            "success", false,
                            "message", "May chua co luot choi dang chay"
                    ));
                    return;
                }

                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "TAM_DUNG");
                if (!updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                            "success", false,
                            "message", "Khong the tam dung may"
                    ));
                    return;
                }

                conn.commit();
                write(response, HttpServletResponse.SC_OK, Map.of(
                        "success", true,
                        "message", "Da tam dung luot choi",
                        "estimatedCost", pausedCost.get(),
                        "machine", mayPSDAO.findById(machine.getId()).orElse(machine)
                ));
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong the tam dung may: " + e.getMessage()
            ));
        }
    }

    private void handleResume(HttpServletResponse response, MayPS machine) throws IOException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean resumed = luotChoiDAO.resumeLuotChoi(conn, machine.getId());
                if (!resumed) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                            "success", false,
                            "message", "May khong co luot choi tam dung"
                    ));
                    return;
                }

                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "DANG_CHOI");
                if (!updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                            "success", false,
                            "message", "Khong the tiep tuc may"
                    ));
                    return;
                }

                conn.commit();
                write(response, HttpServletResponse.SC_OK, Map.of(
                        "success", true,
                        "message", "Da tiep tuc luot choi",
                        "machine", mayPSDAO.findById(machine.getId()).orElse(machine)
                ));
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong the tiep tuc may: " + e.getMessage()
            ));
        }
    }


    private static boolean isMachineInUse(MayPS machine) {
        String status = valueOf(machine.getTinhTrang()).toUpperCase();
        return "DANG_CHOI".equals(status) || "DANG_SU_DUNG".equals(status) || "TAM_DUNG".equals(status) ||
                "PAUSED".equals(status) || "IN_USE".equals(status) || "BUSY".equals(status);
    }

    private static double calculateEstimatedCost(LuotChoi session) {
        double total = Math.max(0, session.getTongTienGio());
        if ("DANG_CHOI".equalsIgnoreCase(session.getTrangThai()) && session.getThoiGianBatDau() != null) {
            long minutes = Math.max(0, java.time.Duration.between(
                    session.getThoiGianBatDau().toInstant(),
                    java.time.Instant.now()
            ).toMinutes());
            total += session.getDonGiaGio() * (minutes / 60.0);
        }
        return total;
    }

    private static long calculateEstimatedMinutes(LuotChoi session, double estimatedCost) {
        if (session.getDonGiaGio() <= 0) {
            return 0;
        }
        return Math.max(0, Math.round((estimatedCost / session.getDonGiaGio()) * 60));
    }

    private static int resolveNhanVienId(HttpServletRequest request) {
        Object user = request.getSession(true).getAttribute("currentUser");
        if (user instanceof NhanVien nhanVien) {
            return nhanVien.getId();
        }
        int nhanVienId = parseInt(request.getParameter("nhanVienId"), 1);
        return nhanVienId > 0 ? nhanVienId : 1;
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String valueOf(String value) {
        return value == null ? "" : value;
    }

    private void write(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(payload));
    }
}
