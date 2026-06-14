package controller;

import com.google.gson.Gson;
import dao.DichVuDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.NhanVien;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/admin"})
public class ApiAdminController extends HttpServlet {
    private final Gson gson = new Gson();
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {
            write(response, HttpServletResponse.SC_FORBIDDEN, Map.of(
                    "success", false,
                    "message", "Chi tai khoan admin duoc mo quan tri he thong"
            ));
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("success", true);
            payload.put("summary", loadSummary(conn));
            payload.put("machines", loadMachines(conn));
            payload.put("employees", loadEmployees(conn));
            payload.put("services", dichVuDAO.getAll());
            payload.put("system", loadSystem(conn));
            response.getWriter().write(gson.toJson(payload));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong tai duoc quan tri: " + e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {
            write(response, HttpServletResponse.SC_FORBIDDEN, Map.of(
                    "success", false,
                    "message", "Chi tai khoan admin duoc thao tac quan tri"
            ));
            return;
        }

        String action = valueOf(request.getParameter("action")).trim();
        try {
            switch (action) {
                case "createEmployee" -> handleCreateEmployee(request, response);
                case "updateEmployee" -> handleUpdateEmployee(request, response);
                case "createMachine" -> handleCreateMachine(request, response);
                case "updateMachine" -> handleUpdateMachine(request, response);
                case "createService" -> handleCreateService(request, response);
                case "updateService" -> handleUpdateService(request, response);
                default -> write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                        "success", false,
                        "message", "Action khong hop le"
                ));
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Thao tac quan tri that bai: " + e.getMessage()
            ));
        }
    }

    private void handleCreateEmployee(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = valueOf(request.getParameter("tenNhanVien")).trim();
        String phone = valueOf(request.getParameter("soDienThoai")).trim();
        String role = normalizeRole(request.getParameter("chucVu"));
        String status = normalizeEmployeeStatus(request.getParameter("trangThai"));

        if (name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Ten nhan vien khong duoc de trong"));
            return;
        }

        String sql = "INSERT INTO nhanvien (tennhanvien, sodienthoai, chucvu, trangthai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            syncSerialSequence(conn, "nhanvien", "id");
            ps.setString(1, name);
            setNullableString(ps, 2, phone);
            ps.setString(3, role);
            ps.setString(4, status);
            ps.executeUpdate();
        }
        write(response, HttpServletResponse.SC_CREATED, Map.of("success", true, "message", "Da them nhan vien"));
    }

    private void handleUpdateEmployee(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        String name = valueOf(request.getParameter("tenNhanVien")).trim();
        String phone = valueOf(request.getParameter("soDienThoai")).trim();
        String role = normalizeRole(request.getParameter("chucVu"));
        String status = normalizeEmployeeStatus(request.getParameter("trangThai"));

        if (id <= 0 || name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Du lieu nhan vien khong hop le"));
            return;
        }

        String sql = "UPDATE nhanvien SET tennhanvien = ?, sodienthoai = ?, chucvu = ?, trangthai = ? WHERE id = ?";
        int updated;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            setNullableString(ps, 2, phone);
            ps.setString(3, role);
            ps.setString(4, status);
            ps.setInt(5, id);
            updated = ps.executeUpdate();
        }
        write(response, updated > 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND,
                Map.of("success", updated > 0, "message", updated > 0 ? "Da cap nhat nhan vien" : "Khong tim thay nhan vien"));
    }

    private void handleCreateMachine(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = valueOf(request.getParameter("tenMay")).trim();
        String status = normalizeMachineStatus(request.getParameter("tinhTrang"));
        String note = valueOf(request.getParameter("ghiChu")).trim();

        if (name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Ten may khong duoc de trong"));
            return;
        }
        if (isOpenMachineStatus(status)) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Mo may tu man hinh Quan ly may"));
            return;
        }

        String sql = "INSERT INTO mayps (tenmay, tinhtrang, ghichu) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, status);
            setNullableString(ps, 3, note);
            ps.executeUpdate();
        }
        write(response, HttpServletResponse.SC_CREATED, Map.of("success", true, "message", "Da them may"));
    }

    private void handleUpdateMachine(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        String name = valueOf(request.getParameter("tenMay")).trim();
        String status = normalizeMachineStatus(request.getParameter("tinhTrang"));
        String note = valueOf(request.getParameter("ghiChu")).trim();

        if (id <= 0 || name.isEmpty()) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Du lieu may khong hop le"));
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String currentStatus = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT tinhtrang FROM mayps WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentStatus = valueOf(rs.getString("tinhtrang")).toUpperCase();
                    }
                }
            }
            if (currentStatus == null) {
                write(response, HttpServletResponse.SC_NOT_FOUND, Map.of("success", false, "message", "Khong tim thay may"));
                return;
            }
            if (isOpenMachineStatus(currentStatus) && !currentStatus.equals(status)) {
                write(response, HttpServletResponse.SC_CONFLICT, Map.of("success", false, "message", "May dang choi, hay dong may truoc khi doi trang thai"));
                return;
            }
            if (!isOpenMachineStatus(currentStatus) && isOpenMachineStatus(status)) {
                write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Mo may tu man hinh Quan ly may"));
                return;
            }

            String sql = "UPDATE mayps SET tenmay = ?, tinhtrang = ?, ghichu = ? WHERE id = ?";
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, status);
                setNullableString(ps, 3, note);
                ps.setInt(4, id);
                updated = ps.executeUpdate();
            }
            write(response, updated > 0 ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND,
                    Map.of("success", updated > 0, "message", updated > 0 ? "Da cap nhat may" : "Khong tim thay may"));
        }
    }

    private void handleCreateService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = valueOf(request.getParameter("tenDichVu")).trim();
        double price = parseDouble(request.getParameter("donGia"), -1);
        String type = normalizeServiceType(request.getParameter("loai"));

        if (name.isEmpty() || price < 0) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Du lieu dich vu khong hop le"));
            return;
        }

        boolean created = dichVuDAO.create(name, price, type);
        write(response, created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Map.of("success", created, "message", created ? "Da them dich vu" : "Khong the them dich vu"));
    }

    private void handleUpdateService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = parseInt(request.getParameter("id"), 0);
        String name = valueOf(request.getParameter("tenDichVu")).trim();
        double price = parseDouble(request.getParameter("donGia"), -1);
        String type = normalizeServiceType(request.getParameter("loai"));

        if (id <= 0 || name.isEmpty() || price < 0) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Du lieu dich vu khong hop le"));
            return;
        }

        boolean updated = dichVuDAO.update(id, name, price, type);
        write(response, updated ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND,
                Map.of("success", updated, "message", updated ? "Da cap nhat dich vu" : "Khong tim thay dich vu"));
    }

    private Map<String, Object> loadSummary(Connection conn) throws Exception {
        Map<String, Object> summary = new LinkedHashMap<>();
        String sql =
                "SELECT " +
                        "(SELECT COUNT(*) FROM mayps) AS total_machines, " +
                        "(SELECT COUNT(*) FROM mayps WHERE tinhtrang IN ('DANG_CHOI','DANG_SU_DUNG','TAM_DUNG')) AS active_machines, " +
                        "(SELECT COUNT(*) FROM mayps WHERE tinhtrang IN ('BINH_THUONG','TRONG')) AS vacant_machines, " +
                        "(SELECT COUNT(*) FROM mayps WHERE tinhtrang IN ('BAO_TRI','HONG')) AS maintenance_machines, " +
                        "(SELECT COUNT(*) FROM nhanvien WHERE trangthai = 'DANG_LAM') AS active_employees, " +
                        "(SELECT COUNT(*) FROM dichvu) AS services, " +
                        "(SELECT COUNT(*) FROM sukien WHERE trangthai = true AND NOW() BETWEEN ngaybatdau AND ngayketthuc) AS active_events, " +
                        "(SELECT COUNT(*) FROM hoadon WHERE trangthai = 'CHUA_THANH_TOAN') AS unpaid_invoices, " +
                        "(SELECT COALESCE(SUM(tongtien), 0) FROM hoadon WHERE trangthai = 'DA_THANH_TOAN' AND DATE(ngaytao) = CURRENT_DATE) AS today_revenue";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                summary.put("totalMachines", rs.getInt("total_machines"));
                summary.put("activeMachines", rs.getInt("active_machines"));
                summary.put("vacantMachines", rs.getInt("vacant_machines"));
                summary.put("maintenanceMachines", rs.getInt("maintenance_machines"));
                summary.put("activeEmployees", rs.getInt("active_employees"));
                summary.put("services", rs.getInt("services"));
                summary.put("activeEvents", rs.getInt("active_events"));
                summary.put("unpaidInvoices", rs.getInt("unpaid_invoices"));
                summary.put("todayRevenue", rs.getDouble("today_revenue"));
            }
        }
        return summary;
    }

    private List<Map<String, Object>> loadMachines(Connection conn) throws Exception {
        String sql = "SELECT m.id, m.tenmay, m.tinhtrang, m.ghichu, " +
                "(SELECT lc.id FROM luotchoi lc WHERE lc.mayid = m.id AND lc.trangthai IN ('DANG_CHOI','TAM_DUNG') ORDER BY lc.id DESC LIMIT 1) AS active_session_id " +
                "FROM mayps m ORDER BY m.id ASC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("tenMay", rs.getString("tenmay"));
                item.put("tinhTrang", rs.getString("tinhtrang"));
                item.put("ghiChu", rs.getString("ghichu"));
                item.put("activeSessionId", getNullableInt(rs, "active_session_id"));
                rows.add(item);
            }
        }
        return rows;
    }

    private List<Map<String, Object>> loadEmployees(Connection conn) throws Exception {
        String sql = "SELECT id, tennhanvien, sodienthoai, chucvu, trangthai FROM nhanvien ORDER BY id ASC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("tenNhanVien", rs.getString("tennhanvien"));
                item.put("soDienThoai", rs.getString("sodienthoai"));
                item.put("chucVu", NhanVien.normalizeVaiTro(rs.getString("chucvu")));
                item.put("rawChucVu", rs.getString("chucvu"));
                item.put("trangThai", rs.getString("trangthai"));
                rows.add(item);
            }
        }
        return rows;
    }

    private Map<String, Object> loadSystem(Connection conn) throws Exception {
        Map<String, Object> system = new LinkedHashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT NOW() AS database_time")) {
            if (rs.next()) {
                Timestamp databaseTime = rs.getTimestamp("database_time");
                system.put("databaseTime", databaseTime == null ? null : databaseTime.getTime());
            }
        }
        system.put("version", "HomePS v2.0");
        return system;
    }

    private static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("currentUser");
        return user instanceof NhanVien nhanVien && nhanVien.isAdmin();
    }

    private static String normalizeRole(String value) {
        String role = valueOf(value).trim().toUpperCase();
        return "ADMIN".equals(role) ? "ADMIN" : "NHAN_VIEN";
    }

    private static String normalizeEmployeeStatus(String value) {
        String status = valueOf(value).trim().toUpperCase();
        return "NGHI_VIEC".equals(status) ? "NGHI_VIEC" : "DANG_LAM";
    }

    private static String normalizeMachineStatus(String value) {
        String status = valueOf(value).trim().toUpperCase();
        return switch (status) {
            case "DANG_CHOI", "TAM_DUNG", "BAO_TRI", "HONG" -> status;
            default -> "BINH_THUONG";
        };
    }

    private static boolean isOpenMachineStatus(String status) {
        return "DANG_CHOI".equals(status) || "TAM_DUNG".equals(status);
    }

    private static String normalizeServiceType(String value) {
        String type = valueOf(value).trim().toUpperCase();
        return switch (type) {
            case "DO_AN", "NUOC", "SNACK" -> type;
            default -> "KHAC";
        };
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws Exception {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static void setNullableString(PreparedStatement ps, int index, String value) throws Exception {
        if (value == null || value.isBlank()) {
            ps.setString(index, null);
        } else {
            ps.setString(index, value);
        }
    }

    private static void syncSerialSequence(Connection conn, String tableName, String idColumn) throws Exception {
        String sql = "SELECT setval(pg_get_serial_sequence('" + tableName + "', '" + idColumn + "'), " +
                "COALESCE((SELECT MAX(" + idColumn + ") FROM " + tableName + "), 1), true)";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
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
