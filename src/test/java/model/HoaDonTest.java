package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

public class HoaDonTest {
    
    @Test
    public void testHoaDonCreation() {
        Timestamp ngayTao = Timestamp.valueOf("2026-04-04 10:00:00");
        HoaDon hd = new HoaDon(1, 5, ngayTao, 45000, 30000, 5000, 70000, "DA_THANH_TOAN");
        
        assertEquals(1, hd.getId());
        assertEquals(5, hd.getLuotChoiId());
        assertEquals(45000, hd.getTienChoi());
        assertEquals(30000, hd.getTienDichVu());
        assertEquals(5000, hd.getTienKhuyenMai());
        assertEquals(70000, hd.getTongTien());
        assertEquals("DA_THANH_TOAN", hd.getTrangThai());
    }
    
    @Test
    public void testHoaDonSettersGetters() {
        HoaDon hd = new HoaDon();
        hd.setId(2);
        hd.setLuotChoiId(10);
        hd.setTienChoi(60000);
        hd.setTienDichVu(40000);
        hd.setTienKhuyenMai(0);
        hd.setTongTien(100000);
        hd.setTrangThai("CHUA_THANH_TOAN");
        
        assertEquals(2, hd.getId());
        assertEquals(10, hd.getLuotChoiId());
        assertEquals(60000, hd.getTienChoi());
        assertEquals(40000, hd.getTienDichVu());
        assertEquals(0, hd.getTienKhuyenMai());
        assertEquals(100000, hd.getTongTien());
        assertEquals("CHUA_THANH_TOAN", hd.getTrangThai());
    }
    
    @Test
    public void testHoaDonNegativeValues() {
        HoaDon hd = new HoaDon();
        // Should allow zero and positive values
        hd.setTienChoi(0);
        hd.setTienDichVu(0);
        assertEquals(0, hd.getTienChoi());
        assertEquals(0, hd.getTienDichVu());
    }
}

