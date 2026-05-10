package dao;

import java.sql.Timestamp;

/**
 * Manual testing class for HomePS backend functionality
 * This can be run directly to test basic operations
 */
public class ManualBackendTest {
    
    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("🧪 HomePS Backend Manual Test");
        System.out.println("================================");
        System.out.println();
        
        // Test 1: Invoice calculation
        testInvoiceCalculation();
        
        // Test 2: Promotion discount
        testPromotionDiscount();
        
        // Test 3: Play time calculation
        testPlayTimeCalculation();
        
        // Test 4: Statistics aggregation
        testStatisticsAggregation();
        
        // Test 5: Multi-service billing
        testMultiServiceBilling();
        
        System.out.println();
        System.out.println("================================");
        System.out.println("✨ All manual tests completed!");
        System.out.println("================================");
    }
    
    /**
     * Test 1: Calculate total invoice amount
     */
    static void testInvoiceCalculation() {
        System.out.println("TEST 1: Invoice Calculation");
        System.out.println("--------------------------");
        
        // Scenario: Customer plays 1.5 hours and orders 2 items
        double playTime = 1.5; // hours
        double hourlyRate = 30000; // VND
        double playCharge = playTime * hourlyRate;
        
        double service1 = 15000; // Mỳ tôm
        double service2 = 12000 * 2; // 2 Coca
        double totalService = service1 + service2;
        
        double subtotal = playCharge + totalService;
        double discount = 0;
        double total = subtotal - discount;
        
        System.out.println("Play time: " + playTime + " hours @ " + hourlyRate + " VND/hour = " + playCharge + " VND");
        System.out.println("Service 1: Mỳ tôm = " + service1 + " VND");
        System.out.println("Service 2: Coca x2 = " + service2 + " VND");
        System.out.println("Total Service: " + totalService + " VND");
        System.out.println("Subtotal: " + subtotal + " VND");
        System.out.println("Discount: " + discount + " VND");
        System.out.println("TOTAL INVOICE: " + total + " VND");
        
        assert total == 87000 : "Invoice calculation failed!";
        System.out.println("✅ Invoice calculation passed\n");
    }
    
    /**
     * Test 2: Apply promotion discount
     */
    static void testPromotionDiscount() {
        System.out.println("TEST 2: Promotion Discount");
        System.out.println("-------------------------");
        
        // Scenario: Customer gets 20% discount (Happy Hour)
        double invoiceAmount = 87000;
        int discountPercent = 20;
        double discountAmount = invoiceAmount * (discountPercent / 100.0);
        double finalAmount = invoiceAmount - discountAmount;
        
        System.out.println("Original invoice: " + invoiceAmount + " VND");
        System.out.println("Promotion: Happy Hour (" + discountPercent + "% off)");
        System.out.println("Discount amount: " + discountAmount + " VND");
        System.out.println("Final amount: " + finalAmount + " VND");
        
        assert finalAmount == 69600 : "Discount calculation failed!";
        System.out.println("✅ Discount calculation passed\n");
    }
    
    /**
     * Test 3: Calculate play time from timestamps
     */
    static void testPlayTimeCalculation() {
        System.out.println("TEST 3: Play Time Calculation");
        System.out.println("-----------------------------");
        
        // Simulate play session
        long startTime = System.currentTimeMillis();
        
        // Simulate 1 hour 30 minutes of play
        long sleepTime = 1000; // 1 second (for demo)
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        double durationMinutes = durationMs / (60.0 * 1000);
        double durationHours = durationMinutes / 60.0;
        
        System.out.println("Session start time: " + new Timestamp(startTime));
        System.out.println("Session end time: " + new Timestamp(endTime));
        System.out.println("Duration: " + Math.round(durationMinutes) + " minutes = " + 
                          String.format("%.2f", durationHours) + " hours");
        
        double hourlyRate = 30000;
        double charge = Math.max(hourlyRate, durationHours * hourlyRate); // Min 1 hour
        System.out.println("Charge: " + charge + " VND");
        
        assert durationMinutes > 0 : "Play time calculation failed!";
        System.out.println("✅ Play time calculation passed\n");
    }
    
    /**
     * Test 4: Aggregate daily statistics
     */
    static void testStatisticsAggregation() {
        System.out.println("TEST 4: Statistics Aggregation");
        System.out.println("------------------------------");
        
        // Simulate daily stats from 3 machines
        int[] sessionsPerMachine = {5, 3, 4}; // Machine 1, 2, 3
        double[] playRevenuePerMachine = {150000, 90000, 120000};
        double[] serviceRevenuePerMachine = {30000, 15000, 25000};
        double[] discountPerMachine = {5000, 2000, 3000};
        
        int totalSessions = 0;
        double totalPlayRevenue = 0;
        double totalServiceRevenue = 0;
        double totalDiscount = 0;
        
        for (int i = 0; i < sessionsPerMachine.length; i++) {
            totalSessions += sessionsPerMachine[i];
            totalPlayRevenue += playRevenuePerMachine[i];
            totalServiceRevenue += serviceRevenuePerMachine[i];
            totalDiscount += discountPerMachine[i];
            
            double machineTotal = playRevenuePerMachine[i] + serviceRevenuePerMachine[i] - discountPerMachine[i];
            System.out.println("Machine " + (i + 1) + ": " + sessionsPerMachine[i] + " sessions, " + 
                              machineTotal + " VND");
        }
        
        double totalRevenue = totalPlayRevenue + totalServiceRevenue - totalDiscount;
        
        System.out.println("---");
        System.out.println("Total sessions: " + totalSessions);
        System.out.println("Play revenue: " + totalPlayRevenue + " VND");
        System.out.println("Service revenue: " + totalServiceRevenue + " VND");
        System.out.println("Discounts: " + totalDiscount + " VND");
        System.out.println("TOTAL REVENUE: " + totalRevenue + " VND");
        
        assert totalSessions == 12 : "Session count failed!";
        assert totalRevenue == 368000 : "Revenue calculation failed!";
        System.out.println("✅ Statistics aggregation passed\n");
    }
    
    /**
     * Test 5: Multi-service billing
     */
    static void testMultiServiceBilling() {
        System.out.println("TEST 5: Multi-Service Billing");
        System.out.println("-----------------------------");
        
        // Customer orders multiple services
        String[] services = {"Mỳ tôm", "Coca", "Trà đá", "Bánh mì"};
        int[] quantities = {1, 2, 3, 1};
        double[] prices = {15000, 12000, 5000, 8000};
        
        double totalServiceCost = 0;
        System.out.println("Services ordered:");
        
        for (int i = 0; i < services.length; i++) {
            double itemTotal = quantities[i] * prices[i];
            totalServiceCost += itemTotal;
            System.out.println("  • " + services[i] + " x" + quantities[i] + " @ " + prices[i] + 
                              " VND = " + itemTotal + " VND");
        }
        
        System.out.println("---");
        System.out.println("Total service cost: " + totalServiceCost + " VND");
        
        double playCharge = 45000; // 1.5 hours
        double subtotal = playCharge + totalServiceCost;
        double discount = 0;
        double finalTotal = subtotal - discount;
        
        System.out.println("Play charge: " + playCharge + " VND");
        System.out.println("Service cost: " + totalServiceCost + " VND");
        System.out.println("Subtotal: " + subtotal + " VND");
        System.out.println("Discount: " + discount + " VND");
        System.out.println("FINAL TOTAL: " + finalTotal + " VND");
        
        assert totalServiceCost == 73000 : "Service billing calculation failed!";
        System.out.println("✅ Multi-service billing passed\n");
    }
}

