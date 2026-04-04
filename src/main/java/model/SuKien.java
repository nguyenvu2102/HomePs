package model;

import java.sql.Timestamp;

public class SuKien {
    private int id;
    private String tenSuKien;
    private String moTa;
    private int phanTramGiamGia;
    private String loaiSuKien;  // THEO_GIO, THEO_NGAY, THEO_TUAN, THEO_THANG
    private String gioApDung;   // Format: HH:mm-HH:mm (e.g., "11:00-14:00")
    private String ngayApDung;  // Format: 2025-04-04 or NULL for all days
    private Timestamp ngayBatDau;
    private Timestamp ngayKetThuc;
    private boolean trangThai;

    public SuKien() {
    }

    public SuKien(int id, String tenSuKien, String moTa, int phanTramGiamGia, String loaiSuKien,
                  String gioApDung, String ngayApDung, Timestamp ngayBatDau, Timestamp ngayKetThuc,
                  boolean trangThai) {
        this.id = id;
        this.tenSuKien = tenSuKien;
        this.moTa = moTa;
        this.phanTramGiamGia = phanTramGiamGia;
        this.loaiSuKien = loaiSuKien;
        this.gioApDung = gioApDung;
        this.ngayApDung = ngayApDung;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenSuKien() { return tenSuKien; }
    public void setTenSuKien(String tenSuKien) { this.tenSuKien = tenSuKien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public int getPhanTramGiamGia() { return phanTramGiamGia; }
    public void setPhanTramGiamGia(int phanTramGiamGia) { this.phanTramGiamGia = phanTramGiamGia; }

    public String getLoaiSuKien() { return loaiSuKien; }
    public void setLoaiSuKien(String loaiSuKien) { this.loaiSuKien = loaiSuKien; }

    public String getGioApDung() { return gioApDung; }
    public void setGioApDung(String gioApDung) { this.gioApDung = gioApDung; }

    public String getNgayApDung() { return ngayApDung; }
    public void setNgayApDung(String ngayApDung) { this.ngayApDung = ngayApDung; }

    public Timestamp getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(Timestamp ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public Timestamp getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(Timestamp ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}

