package model;

import java.sql.Timestamp;

public class LuotChoi {
    private int id;
    private int mayId;
    private int nhanVienId;
    private Timestamp thoiGianBatDau;
    private Timestamp thoiGianKetThuc;
    private double donGiaGio;
    private double tongTienGio;
    private String trangThai;

    public LuotChoi() {
    }

    public LuotChoi(int id, int mayId, int nhanVienId, Timestamp thoiGianBatDau, Timestamp thoiGianKetThuc,
                    double donGiaGio, double tongTienGio, String trangThai) {
        this.id = id;
        this.mayId = mayId;
        this.nhanVienId = nhanVienId;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.donGiaGio = donGiaGio;
        this.tongTienGio = tongTienGio;
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMayId() {
        return mayId;
    }

    public void setMayId(int mayId) {
        this.mayId = mayId;
    }

    public int getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(int nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public Timestamp getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(Timestamp thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public Timestamp getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(Timestamp thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public double getDonGiaGio() {
        return donGiaGio;
    }

    public void setDonGiaGio(double donGiaGio) {
        this.donGiaGio = donGiaGio;
    }

    public double getTongTienGio() {
        return tongTienGio;
    }

    public void setTongTienGio(double tongTienGio) {
        this.tongTienGio = tongTienGio;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}