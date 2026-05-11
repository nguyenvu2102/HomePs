package controller;

import com.google.gson.Gson;
import dao.HoaDonDAO;
import dao.LuotChoiDAO;
import dao.MayPSDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LuotChoi;
import model.MayPS;
import model.NhanVien;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/api/machines"})
@SuppressWarnings("unused")
public class ApiMachineController extends HttpServlet {
    private final MayPSDAO mayPSDAO = new MayPSDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
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

            Optional<LuotChoi> activeSession = luotChoiDAO.findActiveByMayId(machine.getId());
            if (activeSession.isPresent()) {
                LuotChoi session = activeSession.get();
                long minutesElapsed = Math.max(0, Duration.between(
                        session.getThoiGianBatDau().toInstant(),
                        Instant.now()
                ).toMinutes());

                double estimatedCost = session.getDonGiaGio() * (minutesElapsed / 60.0);

                item.put("luotchoiId", session.getId());
                item.put("thoiGianBatDau", session.getThoiGianBatDau().getTime());
                item.put("minutesElapsed", minutesElapsed);
                item.put("estimatedCost", estimatedCost);
                item.put("donGiaGio", session.getDonGiaGio());
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
        Optional<LuotChoi> activeSessionOpt = luotChoiDAO.findActiveByMayId(machine.getId());
        if (activeSessionOpt.isEmpty()) {
            write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                    "success", false,
                    "message", "Khong tim thay luot choi dang hoat dong"
            ));
            return;
        }

        LuotChoi activeSession = activeSessionOpt.get();
        long minutesPlayed = Math.max(1, Duration.between(
                activeSession.getThoiGianBatDau().toInstant(),
                Instant.now()
        ).toMinutes());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Optional<Double> totalOpt;
            try {
                totalOpt = luotChoiDAO.ketThucLuotChoi(conn, machine.getId());
                if (totalOpt.isEmpty()) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                            "success", false,
                            "message", "Khong tim thay luot choi dang hoat dong"
                    ));
                    return;
                }

                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "BINH_THUONG");
                if (!updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                            "success", false,
                            "message", "Khong the dong may"
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

            double totalHoursMoney = totalOpt.get();
            if (hoaDonDAO.findByLuotChoiId(activeSession.getId()).isEmpty()) {
                hoaDonDAO.create(activeSession.getId(), totalHoursMoney, 0d, 0d, totalHoursMoney);
            }

            write(response, HttpServletResponse.SC_OK, Map.of(
                    "success", true,
                    "message", "Da dong may",
                    "luotchoiId", activeSession.getId(),
                    "minutesPlayed", minutesPlayed,
                    "donGiaGio", activeSession.getDonGiaGio(),
                    "totalHoursMoney", totalHoursMoney,
                    "machine", mayPSDAO.findById(machine.getId()).orElse(machine)
            ));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong the dong may: " + e.getMessage()
            ));
        }
    }

    private static boolean isMachineInUse(MayPS machine) {
        String status = valueOf(machine.getTinhTrang()).toUpperCase();
        return "DANG_CHOI".equals(status) || "DANG_SU_DUNG".equals(status) || "IN_USE".equals(status) || "BUSY".equals(status);
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
