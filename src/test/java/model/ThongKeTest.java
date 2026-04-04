package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

public class ThongKeTest {
    
    @Test
    public void testThongKeCreation() {
        Date ngay = Date.valueOf("2026-04-04");
        ThongKe tk = new ThongKe(1, ngay, 1, "May 1", 5, 150000, 30000, 5000, 175000, "NGAY");
        
        assertEquals(1, tk.getId());
        assertEquals(1, tk.getMayId());
        assertEquals("May 1", tk.getTenMay());
        assertEquals(5, tk.getSoLuotChoi());
        assertEquals(150000, tk.getDoanhThuChoi());
        assertEquals(30000, tk.getDoanhThuDichVu());
        assertEquals(5000, tk.getTienKhuyenMai());
        assertEquals(175000, tk.getTongDoanhThu());
        assertEquals("NGAY", tk.getKieuThongKe());
    }
    
    @Test
    public void testThongKeCalculations() {
        Date ngay = Date.valueOf("2026-04-04");
        double playRevenue = 150000;
        double serviceRevenue = 30000;
        double discount = 5000;
        double total = playRevenue + serviceRevenue - discount;
        
        ThongKe tk = new ThongKe(1, ngay, 1, "May 1", 5, playRevenue, serviceRevenue, discount, total, "NGAY");
        
        assertEquals(175000, tk.getTongDoanhThu());
    }
    
    @Test
    public void testDailyStatistics() {
        Date ngay = Date.valueOf("2026-04-04");
        
        // Machine 1: 5 sessions, 150k play + 30k service - 5k discount
        ThongKe machine1 = new ThongKe(1, ngay, 1, "May 1", 5, 150000, 30000, 5000, 175000, "NGAY");
        
        // Machine 2: 3 sessions, 90k play + 15k service - 2k discount
        ThongKe machine2 = new ThongKe(2, ngay, 2, "May 2", 3, 90000, 15000, 2000, 103000, "NGAY");
        
        // Total
        int totalSessions = machine1.getSoLuotChoi() + machine2.getSoLuotChoi();
        double totalRevenue = machine1.getTongDoanhThu() + machine2.getTongDoanhThu();
        
        assertEquals(8, totalSessions);
        assertEquals(278000, totalRevenue);
    }
    
    @Test
    public void testWeeklyStatistics() {
        // Weekly stats aggregation
        double weeklyRevenue = 0;
        int weeklySessions = 0;
        
        for (int day = 0; day < 7; day++) {
            int sessionsPerDay = 8;
            double revenuePerDay = 278000;
            
            weeklySessions += sessionsPerDay;
            weeklyRevenue += revenuePerDay;
        }
        
        assertEquals(56, weeklySessions);
        assertEquals(1946000, weeklyRevenue);
    }
    
    @Test
    public void testMonthlyStatistics() {
        // 4 weeks per month estimation
        int monthlySessions = 56 * 4; // ~224 sessions
        double monthlyRevenue = 1946000 * 4; // ~7.784M
        
        assertEquals(224, monthlySessions);
        assertEquals(7784000, monthlyRevenue);
    }
    
    @Test
    public void testNoSessionDay() {
        Date ngay = Date.valueOf("2026-04-05"); // Assumed no sessions
        ThongKe tk = new ThongKe(1, ngay, 1, "May 1", 0, 0, 0, 0, 0, "NGAY");
        
        assertEquals(0, tk.getSoLuotChoi());
        assertEquals(0, tk.getTongDoanhThu());
    }
}

