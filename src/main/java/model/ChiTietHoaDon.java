package model;

public class ChiTietHoaDon {
    private int id;
    private int hoaDonId;
    private int dichVuId;
    private String tenDichVu;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(int id, int hoaDonId, int dichVuId, String tenDichVu, int soLuong, 
                        double donGia, double thanhTien) {
        this.id = id;
        this.hoaDonId = hoaDonId;
        this.dichVuId = dichVuId;
        this.tenDichVu = tenDichVu;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHoaDonId() { return hoaDonId; }
    public void setHoaDonId(int hoaDonId) { this.hoaDonId = hoaDonId; }

    public int getDichVuId() { return dichVuId; }
    public void setDichVuId(int dichVuId) { this.dichVuId = dichVuId; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }
}

