package dao;

import model.DichVu;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DichVuDAO {
    public List<DichVu> getAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT id, tendichvu, dongia, loai FROM dichvu ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load service list", e);
        }

        return list;
    }

    public Optional<DichVu> findById(int id) {
        String sql = "SELECT id, tendichvu, dongia, loai FROM dichvu WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find service id=" + id, e);
        }

        return Optional.empty();
    }

    public boolean create(String tenDichVu, double donGia, String loai) {
        String sql = "INSERT INTO dichvu (tendichvu, dongia, loai) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDichVu);
            ps.setDouble(2, donGia);
            ps.setString(3, loai);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create service", e);
        }
    }

    public boolean update(int id, String tenDichVu, double donGia, String loai) {
        String sql = "UPDATE dichvu SET tendichvu = ?, dongia = ?, loai = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDichVu);
            ps.setDouble(2, donGia);
            ps.setString(3, loai);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot update service", e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM dichvu WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete service", e);
        }
    }

    private static DichVu mapRow(ResultSet rs) throws Exception {
        return new DichVu(
                rs.getInt("id"),
                rs.getString("tendichvu"),
                rs.getDouble("dongia"),
                rs.getString("loai")
        );
    }
}

