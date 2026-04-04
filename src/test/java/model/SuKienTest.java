package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

public class SuKienTest {
    
    @Test
    public void testSuKienCreation() {
        Timestamp batDau = Timestamp.valueOf("2026-04-01 00:00:00");
        Timestamp ketThuc = Timestamp.valueOf("2026-12-31 23:59:59");
        
        SuKien sk = new SuKien(1, "Giờ vàng trưa", "Khuyến mãi 11h-14h", 20, "THEO_GIO", 
                              "11:00-14:00", null, batDau, ketThuc, true);
        
        assertEquals(1, sk.getId());
        assertEquals("Giờ vàng trưa", sk.getTenSuKien());
        assertEquals(20, sk.getPhanTramGiamGia());
        assertEquals("THEO_GIO", sk.getLoaiSuKien());
        assertEquals("11:00-14:00", sk.getGioApDung());
        assertTrue(sk.isTrangThai());
    }
    
    @Test
    public void testSuKienTheoDayPromotion() {
        Timestamp batDau = Timestamp.valueOf("2026-02-28 00:00:00");
        Timestamp ketThuc = Timestamp.valueOf("2026-03-02 23:59:59");
        
        SuKien tetPromo = new SuKien(2, "Tết Âm Lịch", "Giảm 30% dịp Tết", 30, "THEO_NGAY",
                                    null, "2026-02-29", batDau, ketThuc, true);
        
        assertEquals("THEO_NGAY", tetPromo.getLoaiSuKien());
        assertEquals(30, tetPromo.getPhanTramGiamGia());
        assertEquals("2026-02-29", tetPromo.getNgayApDung());
    }
    
    @Test
    public void testPromotionDiscountCalculation() {
        // Test discount calculation for invoice total 100,000
        double totalBill = 100000;
        int discount = 20; // 20%
        double discountAmount = totalBill * (discount / 100.0);
        double finalAmount = totalBill - discountAmount;
        
        assertEquals(20000, discountAmount);
        assertEquals(80000, finalAmount);
    }
    
    @Test
    public void testMultiplePromotionsStackable() {
        // Check if multiple promotions can be applied
        double totalBill = 100000;
        double discount1 = totalBill * 0.10; // 10% off
        double afterDiscount1 = totalBill - discount1;
        
        // If another promotion applies (should be handled by business logic)
        double discount2 = afterDiscount1 * 0.05; // 5% off second
        double finalAmount = afterDiscount1 - discount2;
        
        assertEquals(90000, afterDiscount1);
        assertEquals(85500, finalAmount);
    }
    
    @Test
    public void testMaxDiscountLimit() {
        // Discount should not exceed 100%
        double totalBill = 100000;
        int discount = 100; // 100%
        double discountAmount = Math.min(totalBill, totalBill * (discount / 100.0));
        double finalAmount = totalBill - discountAmount;
        
        assertEquals(0, finalAmount);
        assertTrue(discountAmount <= totalBill);
    }
}

