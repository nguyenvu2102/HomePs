package controller;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.NhanVien;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/backup"})
public class ApiBackupController extends HttpServlet {
    private static final List<String> TABLES = List.of(
            "mayps",
            "nhanvien",
            "luotchoi",
            "dichvu",
            "hoadon",
            "chitiet_hoadon",
            "sukien"
    );

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {
            response.setContentType("application/json;charset=UTF-8");
            write(response, HttpServletResponse.SC_FORBIDDEN, Map.of(
                    "success", false,
                    "message", "Chi tai khoan admin duoc sao luu du lieu"
            ));
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("success", true);
            payload.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            payload.put("tables", dumpTables(conn));

            String fileTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=homeps-backup-" + fileTime + ".json");
            response.getWriter().write(gson.toJson(payload));
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Khong the sao luu du lieu: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> dumpTables(Connection conn) throws Exception {
        Map<String, Object> tables = new LinkedHashMap<>();
        for (String table : TABLES) {
            tables.put(table, dumpTable(conn, table));
        }
        return tables;
    }

    private List<Map<String, Object>> dumpTable(Connection conn, String table) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " ORDER BY id ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), normalizeValue(rs.getObject(i)));
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private static Object normalizeValue(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (value instanceof Date date) {
            return date.toLocalDate().toString();
        }
        if (value instanceof Time time) {
            return time.toLocalTime().toString();
        }
        return value;
    }

    private static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object user = session.getAttribute("currentUser");
        return user instanceof NhanVien nhanVien && nhanVien.isAdmin();
    }

    private void write(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(payload));
    }
}
