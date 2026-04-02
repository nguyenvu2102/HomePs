package dao;

import model.MayPS;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MayPSDAO {
    // Lấy danh sách tất cả máy để hiện lên Dashboard
    public List<MayPS> getAllMay() {
        List<MayPS> list = new ArrayList<>();
        String sql = "SELECT id, tenmay, tinhtrang, ghichu FROM mayps ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MayPS(
                        rs.getInt("id"),
                        rs.getString("tenmay"),
                        rs.getString("tinhtrang"),
                        rs.getString("ghichu")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load machine list", e);
        }
        return list;
    }

    public Optional<MayPS> findById(int id) {
        String sql = "SELECT id, tenmay, tinhtrang, ghichu FROM mayps WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new MayPS(
                            rs.getInt("id"),
                            rs.getString("tenmay"),
                            rs.getString("tinhtrang"),
                            rs.getString("ghichu")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find machine id=" + id, e);
        }
        return Optional.empty();
    }

    public boolean updateTinhTrang(int id, String tinhTrang) {
        String sql = "UPDATE mayps SET tinhtrang = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tinhTrang);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot update machine status", e);
        }
    }
}