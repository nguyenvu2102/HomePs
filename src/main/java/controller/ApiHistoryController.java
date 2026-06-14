package controller;

import com.google.gson.Gson;
import dao.ChiTietHoaDonDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ChiTietHoaDon;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/history"})
public class ApiHistoryController extends HttpServlet {
    private final Gson gson = new Gson();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String dateParam = valueOf(request.getParameter("date")).trim();
        String statusParam = valueOf(request.getParameter("status")).trim().toUpperCase();
        int machineId = parseInt(request.getParameter("machineId"), 0);

        StringBuilder sql = new StringBuilder(
                "SELECT lc.id, lc.mayid, lc.nhanvienid, lc.thoigianbatdau, lc.thoigianketthuc, " +
                        "lc.dongiagio, lc.tongtiengio, lc.trangthai AS luot_trangthai, " +
                        "m.tenmay, nv.tennhanvien, " +
                        "hd.id AS hoadonid, hd.ngaytao, hd.tienchoi, hd.tiendichvu, hd.tienkhuyenmai, " +
                        "hd.tongtien, hd.trangthai AS hoadon_trangthai " +
                        "FROM luotchoi lc " +
                        "LEFT JOIN mayps m ON lc.mayid = m.id " +
                        "LEFT JOIN nhanvien nv ON lc.nhanvienid = nv.id " +
                        "LEFT JOIN hoadon hd ON hd.luotchoiid = lc.id " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (!dateParam.isEmpty()) {
            try {
                params.add(Date.valueOf(dateParam));
                sql.append("AND DATE(lc.thoigianbatdau) = ? ");
            } catch (IllegalArgumentException e) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Ngay loc khong hop le");
                return;
            }
        }
        if (machineId > 0) {
            sql.append("AND lc.mayid = ? ");
            params.add(machineId);
        }
        if (!statusParam.isEmpty() && !"ALL".equals(statusParam)) {
            if ("DANG_CHOI".equals(statusParam) || "TAM_DUNG".equals(statusParam) || "DA_KET_THUC".equals(statusParam)) {
                sql.append("AND lc.trangthai = ? ");
            } else {
                sql.append("AND hd.trangthai = ? ");
            }
            params.add(statusParam);
        }
        sql.append("ORDER BY lc.thoigianbatdau DESC, lc.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Date date) {
                    ps.setDate(i + 1, date);
                } else {
                    ps.setObject(i + 1, param);
                }
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> summary = new LinkedHashMap<>();
            int activeSessions = 0;
            int completedSessions = 0;
            int paidSessions = 0;
            long totalMinutes = 0;
            double totalRevenue = 0;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp startedAt = rs.getTimestamp("thoigianbatdau");
                    Timestamp endedAt = rs.getTimestamp("thoigianketthuc");
                    long durationMinutes = calculateMinutes(startedAt, endedAt);
                    Integer invoiceId = getNullableInt(rs, "hoadonid");
                    String sessionStatus = valueOf(rs.getString("luot_trangthai"));
                    String invoiceStatus = valueOf(rs.getString("hoadon_trangthai"));
                    double rowRevenue = invoiceId == null ? rs.getDouble("tongtiengio") : rs.getDouble("tongtien");

                    if ("DANG_CHOI".equalsIgnoreCase(sessionStatus) || "TAM_DUNG".equalsIgnoreCase(sessionStatus)) {
                        activeSessions++;
                    } else {
                        completedSessions++;
                    }
                    if ("DA_THANH_TOAN".equalsIgnoreCase(invoiceStatus)) {
                        paidSessions++;
                    }
                    totalMinutes += durationMinutes;
                    totalRevenue += rowRevenue;

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("luotChoiId", rs.getInt("id"));
                    item.put("mayId", rs.getInt("mayid"));
                    item.put("tenMay", rs.getString("tenmay"));
                    item.put("nhanVienId", rs.getInt("nhanvienid"));
                    item.put("tenNhanVien", rs.getString("tennhanvien"));
                    item.put("thoiGianBatDau", toMillis(startedAt));
                    item.put("thoiGianKetThuc", toMillis(endedAt));
                    item.put("durationMinutes", durationMinutes);
                    item.put("donGiaGio", rs.getDouble("dongiagio"));
                    item.put("tongTienGio", rs.getDouble("tongtiengio"));
                    item.put("trangThaiLuotChoi", sessionStatus);
                    item.put("hoaDonId", invoiceId);
                    item.put("ngayTaoHoaDon", toMillis(rs.getTimestamp("ngaytao")));
                    item.put("tienChoi", rs.getDouble("tienchoi"));
                    item.put("tienDichVu", rs.getDouble("tiendichvu"));
                    item.put("tienKhuyenMai", rs.getDouble("tienkhuyenmai"));
                    item.put("tongTien", rowRevenue);
                    item.put("trangThaiHoaDon", invoiceStatus.isEmpty() ? "CHUA_CO_HOA_DON" : invoiceStatus);
                    if (invoiceId != null) {
                        List<ChiTietHoaDon> details = chiTietHoaDonDAO.getByHoaDonId(invoiceId);
                        item.put("chiTiet", details);
                    } else {
                        item.put("chiTiet", List.of());
                    }
                    rows.add(item);
                }
            }

            summary.put("totalSessions", rows.size());
            summary.put("activeSessions", activeSessions);
            summary.put("completedSessions", completedSessions);
            summary.put("paidSessions", paidSessions);
            summary.put("totalMinutes", totalMinutes);
            summary.put("totalRevenue", totalRevenue);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("success", true);
            payload.put("summary", summary);
            payload.put("history", rows);
            response.getWriter().write(gson.toJson(payload));
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Khong tai duoc lich su: " + e.getMessage());
        }
    }

    private static long calculateMinutes(Timestamp startedAt, Timestamp endedAt) {
        if (startedAt == null) {
            return 0;
        }
        Instant end = endedAt == null ? Instant.now() : endedAt.toInstant();
        return Math.max(0, Duration.between(startedAt.toInstant(), end).toMinutes());
    }

    private static Long toMillis(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.getTime();
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws Exception {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String valueOf(String value) {
        return value == null ? "" : value;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(Map.of(
                "success", false,
                "message", message
        )));
    }
}
