package model;

import java.sql.Timestamp;

public class HoaDon {
    private int id;
    private int luotChoiId;
    private Timestamp ngayTao;
    private double tienChoi;
    private double tienDichVu;
    private double tienKhuyenMai;
    private double tongTien;
    private String trangThai;

    public HoaDon() {
    }

    public HoaDon(int id, int luotChoiId, Timestamp ngayTao, double tienChoi, double tienDichVu, 
                  double tienKhuyenMai, double tongTien, String trangThai) {
        this.id = id;
        this.luotChoiId = luotChoiId;
        this.ngayTao = ngayTao;
        this.tienChoi = tienChoi;
        this.tienDichVu = tienDichVu;
        this.tienKhuyenMai = tienKhuyenMai;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLuotChoiId() { return luotChoiId; }
    public void setLuotChoiId(int luotChoiId) { this.luotChoiId = luotChoiId; }

    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }

    public double getTienChoi() { return tienChoi; }
    public void setTienChoi(double tienChoi) { this.tienChoi = tienChoi; }

    public double getTienDichVu() { return tienDichVu; }
    public void setTienDichVu(double tienDichVu) { this.tienDichVu = tienDichVu; }

    public double getTienKhuyenMai() { return tienKhuyenMai; }
    public void setTienKhuyenMai(double tienKhuyenMai) { this.tienKhuyenMai = tienKhuyenMai; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}

