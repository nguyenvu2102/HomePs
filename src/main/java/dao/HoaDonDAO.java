package dao;

import model.HoaDon;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonDAO {
    public boolean create(int luotChoiId, double tienChoi, double tienDichVu, 
                         double tienKhuyenMai, double tongTien) {
        String sql = "INSERT INTO hoadon (luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai) " +
                "VALUES (?, NOW(), ?, ?, ?, ?, 'CHUA_THANH_TOAN')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, luotChoiId);
            ps.setDouble(2, tienChoi);
            ps.setDouble(3, tienDichVu);
            ps.setDouble(4, tienKhuyenMai);
            ps.setDouble(5, tongTien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create invoice", e);
        }
    }

    public Optional<HoaDon> findByLuotChoiId(int luotChoiId) {
        String sql = "SELECT id, luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai " +
                "FROM hoadon WHERE luotchoiid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, luotChoiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find invoice", e);
        }

        return Optional.empty();
    }

    public Optional<HoaDon> findById(int id) {
        String sql = "SELECT id, luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai " +
                "FROM hoadon WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find invoice", e);
        }

        return Optional.empty();
    }

    public List<HoaDon> getAllByDate(java.sql.Date date) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT id, luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai " +
                "FROM hoadon WHERE DATE(ngaytao) = ? ORDER BY ngaytao DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load invoices", e);
        }

        return list;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE hoadon SET trangthai = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot update invoice status", e);
        }
    }

    private static HoaDon mapRow(ResultSet rs) throws Exception {
        return new HoaDon(
                rs.getInt("id"),
                rs.getInt("luotchoiid"),
                rs.getTimestamp("ngaytao"),
                rs.getDouble("tienchoi"),
                rs.getDouble("tiendichvu"),
                rs.getDouble("tienkhuyenmai"),
                rs.getDouble("tongtien"),
                rs.getString("trangthai")
        );
    }
}

