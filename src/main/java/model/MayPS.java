package model;

public class MayPS {
    private int id;
    private String tenMay;
    private String tinhTrang;
    private String ghiChu;

    public MayPS() {
    }

    public MayPS(int id, String tenMay, String tinhTrang, String ghiChu) {
        this.id = id;
        this.tenMay = tenMay;
        this.tinhTrang = tinhTrang;
        this.ghiChu = ghiChu;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenMay() { return tenMay; }
    public void setTenMay(String tenMay) { this.tenMay = tenMay; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}