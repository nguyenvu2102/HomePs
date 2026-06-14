package dao;

import model.LuotChoi;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class LuotChoiDAO {
    public Optional<LuotChoi> findActiveByMayId(int mayId) {
        String sql = "SELECT id, mayid, nhanvienid, thoigianbatdau, thoigianketthuc, dongiagio, tongtiengio, trangthai " +
                "FROM luotchoi WHERE mayid = ? AND trangthai = 'DANG_CHOI' ORDER BY thoigianbatdau DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mayId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load active play session", e);
        }

        return Optional.empty();
    }

    public Optional<LuotChoi> findOpenByMayId(int mayId) {
        try (Connection conn = DBConnection.getConnection()) {
            return findOpenByMayId(conn, mayId);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load open play session", e);
        }
    }

    public Optional<LuotChoi> findById(int id) {
        String sql = "SELECT id, mayid, nhanvienid, thoigianbatdau, thoigianketthuc, dongiagio, tongtiengio, trangthai " +
                "FROM luotchoi WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load play session", e);
        }

        return Optional.empty();
    }

    public boolean batDauLuotChoi(int mayId, int nhanVienId, double donGiaGio) {
        try (Connection conn = DBConnection.getConnection()) {
            return batDauLuotChoi(conn, mayId, nhanVienId, donGiaGio);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot start play session", e);
        }
    }

    public boolean batDauLuotChoi(Connection conn, int mayId, int nhanVienId, double donGiaGio) {
        String sql = "INSERT INTO luotchoi (mayid, nhanvienid, thoigianbatdau, dongiagio, tongtiengio, trangthai) " +
                "VALUES (?, ?, NOW(), ?, 0, 'DANG_CHOI')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mayId);
            ps.setInt(2, nhanVienId);
            ps.setDouble(3, donGiaGio);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot start play session", e);
        }
    }

    public Optional<Double> ketThucLuotChoi(int mayId) {
        try (Connection conn = DBConnection.getConnection()) {
            return ketThucLuotChoi(conn, mayId);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot stop play session", e);
        }
    }

    public Optional<Double> ketThucLuotChoi(Connection conn, int mayId) {
        Optional<LuotChoi> openOpt = findOpenByMayId(conn, mayId);
        if (!openOpt.isPresent()) {
            return Optional.empty();
        }

        LuotChoi active = openOpt.get();
        double tongTien = active.getTongTienGio();
        if ("DANG_CHOI".equalsIgnoreCase(active.getTrangThai())) {
            tongTien += tinhTien(active.getThoiGianBatDau(), active.getDonGiaGio());
        }

        String sql = "UPDATE luotchoi SET thoigianketthuc = NOW(), tongtiengio = ?, trangthai = 'DA_KET_THUC' WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tongTien);
            ps.setInt(2, active.getId());
            ps.executeUpdate();
            return Optional.of(tongTien);
        } catch (Exception e) {
            throw new RuntimeException("Cannot stop play session", e);
        }
    }

    public Optional<Double> pauseLuotChoi(Connection conn, int mayId) {
        Optional<LuotChoi> activeOpt = findActiveByMayId(conn, mayId);
        if (!activeOpt.isPresent()) {
            return Optional.empty();
        }

        LuotChoi active = activeOpt.get();
        double tongTien = active.getTongTienGio() + tinhTien(active.getThoiGianBatDau(), active.getDonGiaGio());
        String sql = "UPDATE luotchoi SET thoigianketthuc = NOW(), tongtiengio = ?, trangthai = 'TAM_DUNG' WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tongTien);
            ps.setInt(2, active.getId());
            ps.executeUpdate();
            return Optional.of(tongTien);
        } catch (Exception e) {
            throw new RuntimeException("Cannot pause play session", e);
        }
    }

    public boolean resumeLuotChoi(Connection conn, int mayId) {
        String sql = "UPDATE luotchoi SET thoigianbatdau = NOW(), thoigianketthuc = NULL, trangthai = 'DANG_CHOI' " +
                "WHERE id = (SELECT id FROM luotchoi WHERE mayid = ? AND trangthai = 'TAM_DUNG' ORDER BY thoigianketthuc DESC NULLS LAST, id DESC LIMIT 1)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mayId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Cannot resume play session", e);
        }
    }

    private static LuotChoi mapRow(ResultSet rs) throws Exception {
        return new LuotChoi(
                rs.getInt("id"),
                rs.getInt("mayid"),
                rs.getInt("nhanvienid"),
                rs.getTimestamp("thoigianbatdau"),
                rs.getTimestamp("thoigianketthuc"),
                rs.getDouble("dongiagio"),
                rs.getDouble("tongtiengio"),
                rs.getString("trangthai")
        );
    }

    private Optional<LuotChoi> findActiveByMayId(Connection conn, int mayId) {
        String sql = "SELECT id, mayid, nhanvienid, thoigianbatdau, thoigianketthuc, dongiagio, tongtiengio, trangthai " +
                "FROM luotchoi WHERE mayid = ? AND trangthai = 'DANG_CHOI' ORDER BY thoigianbatdau DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mayId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load active play session", e);
        }

        return Optional.empty();
    }

    private Optional<LuotChoi> findOpenByMayId(Connection conn, int mayId) {
        String sql = "SELECT id, mayid, nhanvienid, thoigianbatdau, thoigianketthuc, dongiagio, tongtiengio, trangthai " +
                "FROM luotchoi WHERE mayid = ? AND trangthai IN ('DANG_CHOI', 'TAM_DUNG') ORDER BY id DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mayId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load open play session", e);
        }

        return Optional.empty();
    }

    private static double tinhTien(Timestamp batDau, double donGiaGio) {
        long minutes = Math.max(1, Duration.between(batDau.toInstant(), Instant.now()).toMinutes());
        return donGiaGio * (minutes / 60.0);
    }
}
