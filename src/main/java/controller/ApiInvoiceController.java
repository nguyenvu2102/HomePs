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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/invoices"})
public class ApiInvoiceController extends HttpServlet {
    private final Gson gson = new Gson();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String dateParam = valueOf(request.getParameter("date")).trim();
        String statusParam = valueOf(request.getParameter("status")).trim().toUpperCase();

        StringBuilder sql = new StringBuilder(
                "SELECT hd.id, hd.luotchoiid, hd.ngaytao, hd.tienchoi, hd.tiendichvu, " +
                        "hd.tienkhuyenmai, hd.tongtien, hd.trangthai, " +
                        "lc.mayid, lc.thoigianbatdau, lc.thoigianketthuc, lc.dongiagio, " +
                        "m.tenmay " +
                        "FROM hoadon hd " +
                        "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                        "LEFT JOIN mayps m ON lc.mayid = m.id WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (!dateParam.isEmpty()) {
            sql.append("AND DATE(hd.ngaytao) = ? ");
            params.add(Date.valueOf(dateParam));
        }
        if (!statusParam.isEmpty() && !"ALL".equals(statusParam)) {
            sql.append("AND hd.trangthai = ? ");
            params.add(statusParam);
        }
        sql.append("ORDER BY hd.ngaytao DESC, hd.id DESC");

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

            List<Map<String, Object>> invoices = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int invoiceId = rs.getInt("id");
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", invoiceId);
                    item.put("luotChoiId", rs.getInt("luotchoiid"));
                    item.put("luotchoiId", rs.getInt("luotchoiid"));
                    item.put("ngayTao", toMillis(rs.getTimestamp("ngaytao")));
                    item.put("tienChoi", rs.getDouble("tienchoi"));
                    item.put("tienDichVu", rs.getDouble("tiendichvu"));
                    item.put("tienKhuyenMai", rs.getDouble("tienkhuyenmai"));
                    item.put("tongTien", rs.getDouble("tongtien"));
                    item.put("trangThai", rs.getString("trangthai"));
                    item.put("mayId", rs.getInt("mayid"));
                    item.put("tenMay", rs.getString("tenmay"));
                    item.put("thoiGianBatDau", toMillis(rs.getTimestamp("thoigianbatdau")));
                    item.put("thoiGianKetThuc", toMillis(rs.getTimestamp("thoigianketthuc")));
                    item.put("donGiaGio", rs.getDouble("dongiagio"));

                    List<ChiTietHoaDon> details = chiTietHoaDonDAO.getByHoaDonId(invoiceId);
                    item.put("chiTiet", details);
                    invoices.add(item);
                }
            }

            response.getWriter().write(gson.toJson(Map.of(
                    "success", true,
                    "invoices", invoices
            )));
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không tải được hóa đơn: " + e.getMessage());
        }
    }

    private static Long toMillis(java.sql.Timestamp timestamp) {
        return timestamp == null ? null : timestamp.getTime();
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
