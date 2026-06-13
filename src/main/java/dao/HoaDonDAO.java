package dao;

import model.HoaDon;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HoaDonDAO {
    public boolean create(int luotChoiId, double tienChoi, double tienDichVu,
                         double tienKhuyenMai, double tongTien) {
        try (Connection conn = DBConnection.getConnection()) {
            return create(conn, luotChoiId, tienChoi, tienDichVu, tienKhuyenMai, tongTien);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create invoice", e);
        }
    }

    public boolean create(Connection conn, int luotChoiId, double tienChoi, double tienDichVu,
                         double tienKhuyenMai, double tongTien) {
        String sql = "INSERT INTO hoadon (luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai) " +
                "VALUES (?, NOW(), ?, ?, ?, ?, 'CHUA_THANH_TOAN')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = DBConnection.getConnection()) {
            return findByLuotChoiId(conn, luotChoiId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot find invoice", e);
        }
    }

    public Optional<HoaDon> findByLuotChoiId(Connection conn, int luotChoiId) {
        String sql = "SELECT id, luotchoiid, ngaytao, tienchoi, tiendichvu, tienkhuyenmai, tongtien, trangthai " +
                "FROM hoadon WHERE luotchoiid = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public boolean completePayment(int id, double tienKhuyenMai, double tongTien) {
        String sql = "UPDATE hoadon SET tienkhuyenmai = ?, tongtien = ?, trangthai = 'DA_THANH_TOAN' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tienKhuyenMai);
            ps.setDouble(2, tongTien);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot complete invoice payment", e);
        }
    }

    public void updateTienDichVu(Connection conn, int hoaDonId) throws SQLException {
        String sql = "UPDATE hoadon SET tiendichvu = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiet_hoadon WHERE hoadonid = ?) WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.setInt(2, hoaDonId);
            ps.executeUpdate();
        }
    }

    public void updateTienChoi(Connection conn, int hoaDonId, double tienChoi) throws SQLException {
        String sql = "UPDATE hoadon SET tienchoi = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tienChoi);
            ps.setInt(2, hoaDonId);
            ps.executeUpdate();
        }
    }

    public void updateTongTien(Connection conn, int hoaDonId) throws SQLException {
        String sql = "UPDATE hoadon SET tongtien = tienchoi + tiendichvu - tienkhuyenmai WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.executeUpdate();
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
