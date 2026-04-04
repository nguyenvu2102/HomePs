package dao;

import model.ChiTietHoaDon;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {
    public boolean create(int hoaDonId, int dichVuId, String tenDichVu, int soLuong, 
                         double donGia, double thanhTien) {
        String sql = "INSERT INTO chitiet_hoadon (hoadonid, dichvuid, tendichvu, soluong, dongia, thanhTien) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.setInt(2, dichVuId);
            ps.setString(3, tenDichVu);
            ps.setInt(4, soLuong);
            ps.setDouble(5, donGia);
            ps.setDouble(6, thanhTien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create invoice detail", e);
        }
    }

    public List<ChiTietHoaDon> getByHoaDonId(int hoaDonId) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT id, hoadonid, dichvuid, tendichvu, soluong, dongia, thanhtien " +
                "FROM chitiet_hoadon WHERE hoadonid = ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load invoice details", e);
        }

        return list;
    }

    private static ChiTietHoaDon mapRow(ResultSet rs) throws Exception {
        return new ChiTietHoaDon(
                rs.getInt("id"),
                rs.getInt("hoadonid"),
                rs.getInt("dichvuid"),
                rs.getString("tendichvu"),
                rs.getInt("soluong"),
                rs.getDouble("dongia"),
                rs.getDouble("thanhtien")
        );
    }
}

