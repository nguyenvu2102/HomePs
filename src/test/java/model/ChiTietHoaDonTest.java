package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChiTietHoaDonTest {
    
    @Test
    public void testChiTietHoaDonCreation() {
        ChiTietHoaDon ct = new ChiTietHoaDon(1, 5, 10, "Coca", 2, 12000, 24000);
        
        assertEquals(1, ct.getId());
        assertEquals(5, ct.getHoaDonId());
        assertEquals(10, ct.getDichVuId());
        assertEquals("Coca", ct.getTenDichVu());
        assertEquals(2, ct.getSoLuong());
        assertEquals(12000, ct.getDonGia());
        assertEquals(24000, ct.getThanhTien());
    }
    
    @Test
    public void testChiTietHoaDonCalculation() {
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setSoLuong(3);
        ct.setDonGia(15000);
        double expected = 3 * 15000;
        
        assertEquals(expected, ct.getDonGia() * ct.getSoLuong());
    }
    
    @Test
    public void testChiTietHoaDonMultipleItems() {
        // Simulate bill with multiple items
        ChiTietHoaDon[] items = {
            new ChiTietHoaDon(1, 5, 1, "Mỳ tôm", 1, 15000, 15000),
            new ChiTietHoaDon(2, 5, 2, "Coca", 2, 12000, 24000),
            new ChiTietHoaDon(3, 5, 3, "Trà đá", 3, 5000, 15000)
        };
        
        double totalService = 0;
        for (ChiTietHoaDon item : items) {
            totalService += item.getThanhTien();
        }
        
        assertEquals(54000, totalService); // 15000 + 24000 + 15000
    }
}

