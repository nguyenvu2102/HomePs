package model;

import java.sql.Timestamp;

public class LuotChoiTest {
    public static void main(String[] args) {
        Timestamp batDau = Timestamp.valueOf("2026-04-02 08:00:00");
        Timestamp ketThuc = Timestamp.valueOf("2026-04-02 09:30:00");

        LuotChoi luotChoi = new LuotChoi(1, 2, 3, batDau, ketThuc, 12000, 18000, "DA_KET_THUC");

        if (luotChoi.getId() != 1 || luotChoi.getMayId() != 2 || luotChoi.getNhanVienId() != 3) {
            throw new IllegalStateException("Basic LuotChoi values are invalid");
        }

        if (!"DA_KET_THUC".equals(luotChoi.getTrangThai())) {
            throw new IllegalStateException("TrangThai is invalid");
        }

        System.out.println("LuotChoi smoke check passed");
    }
}
