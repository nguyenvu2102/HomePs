package model;

public class DichVu {
    private int id;
    private String tenDichVu;
    private double donGia;
    private String loai;

    public DichVu() {
    }

    public DichVu(int id, String tenDichVu, double donGia, String loai) {
        this.id = id;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.loai = loai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }
}