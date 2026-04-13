package dao;

import model.NhanVien;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienDAO {
    public List<NhanVien> getAllActive() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT id, tennhanvien, sodienthoai, chucvu, trangthai FROM nhanvien WHERE trangthai = 'DANG_LAM' ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load employee list", e);
        }
        return list;
    }

    public Optional<NhanVien> findById(int id) {
        String sql = "SELECT id, tennhanvien, sodienthoai, chucvu, trangthai FROM nhanvien WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot find employee id=" + id, e);
        }
        return Optional.empty();
    }

    private static NhanVien mapRow(ResultSet rs) throws Exception {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setId(rs.getInt("id"));
        nhanVien.setTenNhanVien(rs.getString("tennhanvien"));
        nhanVien.setSoDienThoai(rs.getString("sodienthoai"));
        nhanVien.setChucVu(NhanVien.normalizeVaiTro(rs.getString("chucvu")));
        nhanVien.setTrangThai(rs.getString("trangthai"));
        return nhanVien;
    }
}

