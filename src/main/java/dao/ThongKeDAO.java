package dao;

import model.ThongKe;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {
    public List<ThongKe> getThongKeTheoNgay(Date date) {
        List<ThongKe> list = new ArrayList<>();
        String sql = "SELECT COALESCE(m.id, 0) as mayid, COALESCE(m.tenmay, 'Tổng') as tenmay, " +
                "COUNT(DISTINCT lc.id) as soluotchoi, COALESCE(SUM(lc.tongtiengio), 0) as doanhthuchoi, " +
                "COALESCE(SUM(ctd.thanhTien), 0) as doanhthudicvu, COALESCE(SUM(hd.tienkhuyenmai), 0) as tienkhuyenmai " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN mayps m ON lc.mayid = m.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE DATE(hd.ngaytao) = ? " +
                "GROUP BY m.id, m.tenmay " +
                "UNION ALL " +
                "SELECT 0, 'Tổng Cộng', COUNT(DISTINCT lc.id), COALESCE(SUM(lc.tongtiengio), 0), " +
                "COALESCE(SUM(ctd.thanhTien), 0), COALESCE(SUM(hd.tienkhuyenmai), 0) " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE DATE(hd.ngaytao) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mayId = rs.getInt("mayid");
                    String tenMay = rs.getString("tenmay");
                    int soLuotChoi = rs.getInt("soluotchoi");
                    double doanhThuChoi = rs.getDouble("doanhthuchoi");
                    double doanhThuDichVu = rs.getDouble("doanhthudicvu");
                    double tienKhuyenMai = rs.getDouble("tienkhuyenmai");
                    double tongDoanhThu = doanhThuChoi + doanhThuDichVu - tienKhuyenMai;

                    ThongKe tk = new ThongKe(0, date, mayId, tenMay, soLuotChoi, doanhThuChoi,
                            doanhThuDichVu, tienKhuyenMai, tongDoanhThu, "NGAY");
                    list.add(tk);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load daily statistics", e);
        }

        return list;
    }

    public List<ThongKe> getThongKeTheoTuan(Date startDate, Date endDate) {
        List<ThongKe> list = new ArrayList<>();
        String sql = "SELECT COALESCE(m.id, 0) as mayid, COALESCE(m.tenmay, 'Tổng') as tenmay, " +
                "COUNT(DISTINCT lc.id) as soluotchoi, COALESCE(SUM(lc.tongtiengio), 0) as doanhthuchoi, " +
                "COALESCE(SUM(ctd.thanhTien), 0) as doanhthudicvu, COALESCE(SUM(hd.tienkhuyenmai), 0) as tienkhuyenmai " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN mayps m ON lc.mayid = m.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE DATE(hd.ngaytao) BETWEEN ? AND ? " +
                "GROUP BY m.id, m.tenmay " +
                "UNION ALL " +
                "SELECT 0, 'Tổng Cộng', COUNT(DISTINCT lc.id), COALESCE(SUM(lc.tongtiengio), 0), " +
                "COALESCE(SUM(ctd.thanhTien), 0), COALESCE(SUM(hd.tienkhuyenmai), 0) " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE DATE(hd.ngaytao) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mayId = rs.getInt("mayid");
                    String tenMay = rs.getString("tenmay");
                    int soLuotChoi = rs.getInt("soluotchoi");
                    double doanhThuChoi = rs.getDouble("doanhthuchoi");
                    double doanhThuDichVu = rs.getDouble("doanhthudicvu");
                    double tienKhuyenMai = rs.getDouble("tienkhuyenmai");
                    double tongDoanhThu = doanhThuChoi + doanhThuDichVu - tienKhuyenMai;

                    ThongKe tk = new ThongKe(0, startDate, mayId, tenMay, soLuotChoi, doanhThuChoi,
                            doanhThuDichVu, tienKhuyenMai, tongDoanhThu, "TUAN");
                    list.add(tk);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load weekly statistics", e);
        }

        return list;
    }

    public List<ThongKe> getThongKeTheoThang(int thang, int nam) {
        List<ThongKe> list = new ArrayList<>();
        String sql = "SELECT COALESCE(m.id, 0) as mayid, COALESCE(m.tenmay, 'Tổng') as tenmay, " +
                "COUNT(DISTINCT lc.id) as soluotchoi, COALESCE(SUM(lc.tongtiengio), 0) as doanhthuchoi, " +
                "COALESCE(SUM(ctd.thanhTien), 0) as doanhthudicvu, COALESCE(SUM(hd.tienkhuyenmai), 0) as tienkhuyenmai " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN mayps m ON lc.mayid = m.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE EXTRACT(MONTH FROM hd.ngaytao) = ? AND EXTRACT(YEAR FROM hd.ngaytao) = ? " +
                "GROUP BY m.id, m.tenmay " +
                "UNION ALL " +
                "SELECT 0, 'Tổng Cộng', COUNT(DISTINCT lc.id), COALESCE(SUM(lc.tongtiengio), 0), " +
                "COALESCE(SUM(ctd.thanhTien), 0), COALESCE(SUM(hd.tienkhuyenmai), 0) " +
                "FROM hoadon hd " +
                "LEFT JOIN luotchoi lc ON hd.luotchoiid = lc.id " +
                "LEFT JOIN chitiet_hoadon ctd ON hd.id = ctd.hoadonid " +
                "WHERE EXTRACT(MONTH FROM hd.ngaytao) = ? AND EXTRACT(YEAR FROM hd.ngaytao) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.setInt(3, thang);
            ps.setInt(4, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mayId = rs.getInt("mayid");
                    String tenMay = rs.getString("tenmay");
                    int soLuotChoi = rs.getInt("soluotchoi");
                    double doanhThuChoi = rs.getDouble("doanhthuchoi");
                    double doanhThuDichVu = rs.getDouble("doanhthudicvu");
                    double tienKhuyenMai = rs.getDouble("tienkhuyenmai");
                    double tongDoanhThu = doanhThuChoi + doanhThuDichVu - tienKhuyenMai;

                    Date dateOfMonth = Date.valueOf(nam + "-" + String.format("%02d", thang) + "-01");
                    ThongKe tk = new ThongKe(0, dateOfMonth, mayId, tenMay, soLuotChoi, doanhThuChoi,
                            doanhThuDichVu, tienKhuyenMai, tongDoanhThu, "THANG");
                    list.add(tk);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load monthly statistics", e);
        }

        return list;
    }
}

