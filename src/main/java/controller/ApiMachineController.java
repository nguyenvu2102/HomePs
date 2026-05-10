package controller;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/api/machines"})
@SuppressWarnings("unused")
public class ApiMachineController extends HttpServlet {
    private final MayPSDAO mayPSDAO = new MayPSDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
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
            
            // Fetch active session if machine is in use
            java.util.Optional<LuotChoi> activeSession = luotChoiDAO.findActiveByMayId(machine.getId());
            if (activeSession.isPresent()) {
                LuotChoi session = activeSession.get();
                long minutesElapsed = Math.max(0, java.time.Duration.between(
                    session.getThoiGianBatDau().toInstant(),
                    java.time.Instant.now()
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
                    "message", "Thiếu id máy"
            ));
            return;
        }

        Optional<MayPS> machineOpt = mayPSDAO.findById(machineId);
        if (machineOpt.isEmpty()) {
            write(response, HttpServletResponse.SC_NOT_FOUND, Map.of(
                    "success", false,
                    "message", "Không tìm thấy máy"
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
                "message", "Action không hợp lệ"
        ));
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = valueOf(request.getParameter("tenMay")).trim();
        String note = valueOf(request.getParameter("ghiChu")).trim();

        if (name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                    "success", false,
                    "message", "Tên máy không được để trống"
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
                                "message", "Đã thêm máy mới thành công",
                                "machine", newMachine
                        ));
                    } else {
                        write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                                "success", false,
                                "message", "Không thể lấy ID máy mới"
                        ));
                    }
                }
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Thêm máy thất bại: " + e.getMessage()
            ));
        }
    }

    private void handleOpen(HttpServletRequest request, HttpServletResponse response, MayPS machine) throws IOException {
        if (isMachineInUse(machine)) {
            write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                    "success", false,
                    "message", "Máy đang được mở rồi"
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
                            "message", "Không thể mở máy"
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
                    "message", "Không thể mở máy: " + e.getMessage()
            ));
            return;
        }

        int sessionId = luotChoiDAO.findActiveByMayId(machine.getId()).map(session -> session.getId()).orElse(-1);
        write(response, HttpServletResponse.SC_OK, Map.of(
                "success", true,
                "message", "Đã mở máy",
                "machine", mayPSDAO.findById(machine.getId()).orElse(machine),
                "sessionId", sessionId
        ));
    }

    private void handleClose(HttpServletResponse response, MayPS machine) throws IOException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Optional<Double> totalOpt;
            try {
                totalOpt = luotChoiDAO.ketThucLuotChoi(conn, machine.getId());
                if (totalOpt.isEmpty()) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_CONFLICT, Map.of(
                            "success", false,
                            "message", "Máy chưa có lượt chơi đang hoạt động"
                    ));
                    return;
                }

                boolean updated = mayPSDAO.updateTinhTrang(conn, machine.getId(), "BINH_THUONG");
                if (!updated) {
                    conn.rollback();
                    write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                            "success", false,
                            "message", "Không thể đóng máy"
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

            write(response, HttpServletResponse.SC_OK, Map.of(
                    "success", true,
                    "message", "Đã đóng máy",
                    "totalHoursMoney", totalOpt.get(),
                    "machine", mayPSDAO.findById(machine.getId()).orElse(machine)
            ));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Không thể đóng máy: " + e.getMessage()
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

