package model;

public class NhanVien {
    private int id;
    private String tenNhanVien;
    private String soDienThoai;
    private String chucVu; // BEP, TRONG_MAY, BAO_VE, KY_THUAT, LAO_CONG
    private String trangThai; // DANG_LAM, NGHI

    public NhanVien() {}

    public NhanVien(int id, String tenNhanVien, String soDienThoai, String chucVu, String trangThai) {
        this.id = id;
        this.tenNhanVien = tenNhanVien;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}