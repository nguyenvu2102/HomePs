package model;

import java.sql.Date;

public class ThongKe {
    private int id;
    private Date ngay;
    private int mayId;
    private String tenMay;
    private int soLuotChoi;
    private double doanhThuChoi;
    private double doanhThuDichVu;
    private double tienKhuyenMai;
    private double tongDoanhThu;
    private String kieuThongKe; // NGAY, TUAN, THANG

    public ThongKe() {
    }

    public ThongKe(int id, Date ngay, int mayId, String tenMay, int soLuotChoi,
                   double doanhThuChoi, double doanhThuDichVu, double tienKhuyenMai,
                   double tongDoanhThu, String kieuThongKe) {
        this.id = id;
        this.ngay = ngay;
        this.mayId = mayId;
        this.tenMay = tenMay;
        this.soLuotChoi = soLuotChoi;
        this.doanhThuChoi = doanhThuChoi;
        this.doanhThuDichVu = doanhThuDichVu;
        this.tienKhuyenMai = tienKhuyenMai;
        this.tongDoanhThu = tongDoanhThu;
        this.kieuThongKe = kieuThongKe;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getNgay() { return ngay; }
    public void setNgay(Date ngay) { this.ngay = ngay; }

    public int getMayId() { return mayId; }
    public void setMayId(int mayId) { this.mayId = mayId; }

    public String getTenMay() { return tenMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }

    public int getSoLuotChoi() { return soLuotChoi; }
    public void setSoLuotChoi(int soLuotChoi) { this.soLuotChoi = soLuotChoi; }

    public double getDoanhThuChoi() { return doanhThuChoi; }
    public void setDoanhThuChoi(double doanhThuChoi) { this.doanhThuChoi = doanhThuChoi; }

    public double getDoanhThuDichVu() { return doanhThuDichVu; }
    public void setDoanhThuDichVu(double doanhThuDichVu) { this.doanhThuDichVu = doanhThuDichVu; }

    public double getTienKhuyenMai() { return tienKhuyenMai; }
    public void setTienKhuyenMai(double tienKhuyenMai) { this.tienKhuyenMai = tienKhuyenMai; }

    public double getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(double tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public String getKieuThongKe() { return kieuThongKe; }
    public void setKieuThongKe(String kieuThongKe) { this.kieuThongKe = kieuThongKe; }
}

