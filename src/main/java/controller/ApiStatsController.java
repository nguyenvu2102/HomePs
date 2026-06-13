package controller;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/stats"})
public class ApiStatsController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            DateRange range = resolveRange(request);
            try (Connection conn = DBConnection.getConnection()) {
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("success", true);
                payload.put("period", range.period);
                payload.put("startDate", range.start.toString());
                payload.put("endDate", range.end.toString());
                payload.put("summary", loadSummary(conn, range));
                payload.put("byMachine", loadByMachine(conn, range));
                payload.put("dailyTrend", loadDailyTrend(conn, range));
                payload.put("topServices", loadTopServices(conn, range));
                response.getWriter().write(gson.toJson(payload));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of(
                    "success", false,
                    "message", "Không tải được thống kê: " + e.getMessage()
            )));
        }
    }

    private Map<String, Object> loadSummary(Connection conn, DateRange range) throws Exception {
        String sql = "SELECT COUNT(*) AS sohoadon, COUNT(DISTINCT hd.luotchoiid) AS soluotchoi, " +
                "COALESCE(SUM(hd.tienchoi), 0) AS doanhthuchoi, " +
                "COALESCE(SUM(hd.tiendichvu), 0) AS doanhthudichvu, " +
                "COALESCE(SUM(hd.tienkhuyenmai), 0) AS tienkhuyenmai, " +
                "COALESCE(SUM(hd.tongtien), 0) AS tongdoanhthu " +
                "FROM hoadon hd WHERE hd.trangthai = 'DA_THANH_TOAN' AND DATE(hd.ngaytao) BETWEEN ? AND ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindRange(ps, range);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Object> item = new LinkedHashMap<>();
                if (rs.next()) {
                    item.put("soHoaDon", rs.getInt("sohoadon"));
                    item.put("soLuotChoi", rs.getInt("soluotchoi"));
                    item.put("doanhThuChoi", rs.getDouble("doanhthuchoi"));
                    item.put("doanhThuDichVu", rs.getDouble("doanhthudichvu"));
                    item.put("tienKhuyenMai", rs.getDouble("tienkhuyenmai"));
                    item.put("tongDoanhThu", rs.getDouble("tongdoanhthu"));
                }
                return item;
            }
        }
    }

    private List<Map<String, Object>> loadByMachine(Connection conn, DateRange range) throws Exception {
        String sql = "SELECT COALESCE(m.id, 0) AS mayid, COALESCE(m.tenmay, 'Không rõ') AS tenmay, " +
                "COUNT(DISTINCT hd.luotchoiid) AS soluotchoi, " +
                "COALESCE(SUM(hd.tienchoi), 0) AS doanhthuchoi, " +
                "COALESCE(SUM(hd.tiendichvu), 0) AS doanhthudichvu, " +
                "COALESCE(SUM(hd.tienkhuyenmai), 0) AS tienkhuyenmai, " +
                "COALESCE(SUM(hd.tongtien), 0) AS tongdoanhthu " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN mayps m ON lc.mayid = m.id " +
                "WHERE hd.trangthai = 'DA_THANH_TOAN' AND DATE(hd.ngaytao) BETWEEN ? AND ? " +
                "GROUP BY m.id, m.tenmay ORDER BY tongdoanhthu DESC, tenmay ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindRange(ps, range);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("mayId", rs.getInt("mayid"));
                    item.put("tenMay", rs.getString("tenmay"));
                    item.put("soLuotChoi", rs.getInt("soluotchoi"));
                    item.put("doanhThuChoi", rs.getDouble("doanhthuchoi"));
                    item.put("doanhThuDichVu", rs.getDouble("doanhthudichvu"));
                    item.put("tienKhuyenMai", rs.getDouble("tienkhuyenmai"));
                    item.put("tongDoanhThu", rs.getDouble("tongdoanhthu"));
                    rows.add(item);
                }
                return rows;
            }
        }
    }

    private List<Map<String, Object>> loadDailyTrend(Connection conn, DateRange range) throws Exception {
        String sql = "SELECT d.day::date AS ngay, " +
                "COALESCE(SUM(hd.tienchoi), 0) AS doanhthuchoi, " +
                "COALESCE(SUM(hd.tiendichvu), 0) AS doanhthudichvu, " +
                "COALESCE(SUM(hd.tienkhuyenmai), 0) AS tienkhuyenmai, " +
                "COALESCE(SUM(hd.tongtien), 0) AS tongdoanhthu " +
                "FROM generate_series(CAST(? AS date), CAST(? AS date), interval '1 day') AS d(day) " +
                "LEFT JOIN hoadon hd ON DATE(hd.ngaytao) = d.day::date AND hd.trangthai = 'DA_THANH_TOAN' " +
                "GROUP BY d.day ORDER BY d.day";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindRange(ps, range);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("ngay", rs.getDate("ngay").toString());
                    item.put("doanhThuChoi", rs.getDouble("doanhthuchoi"));
                    item.put("doanhThuDichVu", rs.getDouble("doanhthudichvu"));
                    item.put("tienKhuyenMai", rs.getDouble("tienkhuyenmai"));
                    item.put("tongDoanhThu", rs.getDouble("tongdoanhthu"));
                    rows.add(item);
                }
                return rows;
            }
        }
    }

    private List<Map<String, Object>> loadTopServices(Connection conn, DateRange range) throws Exception {
        String sql = "SELECT ctd.tendichvu, COALESCE(SUM(ctd.soluong), 0) AS soluong, " +
                "COALESCE(SUM(ctd.thanhtien), 0) AS doanhthu " +
                "FROM chitiet_hoadon ctd " +
                "JOIN hoadon hd ON ctd.hoadonid = hd.id " +
                "WHERE hd.trangthai = 'DA_THANH_TOAN' AND DATE(hd.ngaytao) BETWEEN ? AND ? " +
                "GROUP BY ctd.tendichvu ORDER BY doanhthu DESC, soluong DESC LIMIT 8";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            bindRange(ps, range);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("tenDichVu", rs.getString("tendichvu"));
                    item.put("soLuong", rs.getInt("soluong"));
                    item.put("doanhThu", rs.getDouble("doanhthu"));
                    rows.add(item);
                }
                return rows;
            }
        }
    }

    private static DateRange resolveRange(HttpServletRequest request) {
        String period = text(request.getParameter("period")).toLowerCase();
        LocalDate today = LocalDate.now();

        if ("month".equals(period)) {
            int year = parseInt(request.getParameter("year"), today.getYear());
            int month = parseInt(request.getParameter("month"), today.getMonthValue());
            YearMonth yearMonth = YearMonth.of(year, month);
            return new DateRange("month", yearMonth.atDay(1), yearMonth.atEndOfMonth());
        }

        if ("week".equals(period)) {
            LocalDate start = parseDate(request.getParameter("startDate"), today.minusDays(6));
            LocalDate end = parseDate(request.getParameter("endDate"), today);
            return new DateRange("week", start, end);
        }

        LocalDate date = parseDate(request.getParameter("date"), today);
        return new DateRange("day", date, date);
    }

    private static void bindRange(PreparedStatement ps, DateRange range) throws Exception {
        ps.setDate(1, Date.valueOf(range.start));
        ps.setDate(2, Date.valueOf(range.end));
    }

    private static LocalDate parseDate(String value, LocalDate defaultValue) {
        try {
            return LocalDate.parse(text(value));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(text(value));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private record DateRange(String period, LocalDate start, LocalDate end) {
    }
}
