package dao;

import model.SuKien;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SuKienDAO {
    public boolean create(String tenSuKien, String moTa, int phanTramGiamGia, String loaiSuKien,
                         String gioApDung, String ngayApDung, Timestamp ngayBatDau, Timestamp ngayKetThuc) {
        String sql = "INSERT INTO sukien (tensukien, mota, phantramgiamgia, loaisukien, gioapdung, ngayapdung, ngayBatDau, ngayKetThuc, trangthai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, true)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenSuKien);
            ps.setString(2, moTa);
            ps.setInt(3, phanTramGiamGia);
            ps.setString(4, loaiSuKien);
            ps.setString(5, gioApDung);
            ps.setString(6, ngayApDung);
            ps.setTimestamp(7, ngayBatDau);
            ps.setTimestamp(8, ngayKetThuc);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create event", e);
        }
    }

    public List<SuKien> getAll() {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT id, tensukien, mota, phantramgiamgia, loaisukien, gioapdung, ngayapdung, " +
                "ngayBatDau, ngayKetThuc, trangthai FROM sukien WHERE trangthai = true ORDER BY ngayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load events", e);
        }

        return list;
    }

    public List<SuKien> getActiveByTime(LocalDateTime time) {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT id, tensukien, mota, phantramgiamgia, loaisukien, gioapdung, ngayapdung, " +
                "ngayBatDau, ngayKetThuc, trangthai FROM sukien " +
                "WHERE trangthai = true AND ? BETWEEN ngayBatDau AND ngayKetThuc";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load active events", e);
        }

        return list;
    }

    public Optional<SuKien> findById(int id) {
        String sql = "SELECT id, tensukien, mota, phantramgiamgia, loaisukien, gioapdung, ngayapdung, " +
                "ngayBatDau, ngayKetThuc, trangthai FROM sukien WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find event", e);
        }

        return Optional.empty();
    }

    public boolean update(int id, String tenSuKien, String moTa, int phanTramGiamGia, String loaiSuKien,
                         String gioApDung, String ngayApDung, Timestamp ngayBatDau, Timestamp ngayKetThuc) {
        String sql = "UPDATE sukien SET tensukien = ?, mota = ?, phantramgiamgia = ?, loaisukien = ?, " +
                "gioapdung = ?, ngayapdung = ?, ngayBatDau = ?, ngayKetThuc = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenSuKien);
            ps.setString(2, moTa);
            ps.setInt(3, phanTramGiamGia);
            ps.setString(4, loaiSuKien);
            ps.setString(5, gioApDung);
            ps.setString(6, ngayApDung);
            ps.setTimestamp(7, ngayBatDau);
            ps.setTimestamp(8, ngayKetThuc);
            ps.setInt(9, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot update event", e);
        }
    }

    public boolean deactivate(int id) {
        String sql = "UPDATE sukien SET trangthai = false WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot deactivate event", e);
        }
    }

    private static SuKien mapRow(ResultSet rs) throws Exception {
        return new SuKien(
                rs.getInt("id"),
                rs.getString("tensukien"),
                rs.getString("mota"),
                rs.getInt("phantramgiamgia"),
                rs.getString("loaisukien"),
                rs.getString("gioapdung"),
                rs.getString("ngayapdung"),
                rs.getTimestamp("ngayBatDau"),
                rs.getTimestamp("ngayKetThuc"),
                rs.getBoolean("trangthai")
        );
    }
}

