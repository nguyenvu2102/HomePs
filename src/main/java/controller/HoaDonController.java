package controller;

import dao.HoaDonDAO;
import dao.ChiTietHoaDonDAO;
import dao.DichVuDAO;
import dao.LuotChoiDAO;
import dao.SuKienDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DichVu;
import model.HoaDon;
import model.LuotChoi;
import model.SuKien;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/hoadon"})
public class HoaDonController extends HttpServlet {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
    private final SuKienDAO suKienDAO = new SuKienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("view".equalsIgnoreCase(action)) {
            viewHoaDon(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        if ("addService".equalsIgnoreCase(action)) {
            themDichVu(request, response);
        } else if ("checkout".equalsIgnoreCase(action)) {
            thanhToan(request, response);
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void viewHoaDon(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int luotChoiId = parseInt(request.getParameter("luotChoiId"), -1);
        if (luotChoiId <= 0) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(luotChoiId);
        List<DichVu> danhSachDichVu = dichVuDAO.getAll();

        request.setAttribute("luotChoiId", luotChoiId);
        request.setAttribute("hoaDon", hoaDonOpt.orElse(null));
        request.setAttribute("chiTietList", hoaDonOpt.isPresent() ? 
            chiTietDAO.getByHoaDonId(hoaDonOpt.get().getId()) : List.of());
        request.setAttribute("danhSachDichVu", danhSachDichVu);

        request.getRequestDispatcher("hoadon.jsp").forward(request, response);
    }

    private void themDichVu(HttpServletRequest request, HttpServletResponse response) {
        try {
            int luotChoiId = parseInt(request.getParameter("luotChoiId"), -1);
            int dichVuId = parseInt(request.getParameter("dichVuId"), -1);
            int soLuong = parseInt(request.getParameter("soLuong"), 1);

            if (luotChoiId <= 0 || dichVuId <= 0 || soLuong <= 0) {
                setFlash(request, "Invalid input parameters.");
                return;
            }

            Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(luotChoiId);
            Optional<DichVu> dichVuOpt = dichVuDAO.findById(dichVuId);

            if (!hoaDonOpt.isPresent() || !dichVuOpt.isPresent()) {
                setFlash(request, "Invoice or service not found.");
                return;
            }

            HoaDon hoaDon = hoaDonOpt.get();
            DichVu dichVu = dichVuOpt.get();
            double thanhTien = dichVu.getDonGia() * soLuong;

            if (chiTietDAO.create(hoaDon.getId(), dichVuId, dichVu.getTenDichVu(), soLuong, 
                                 dichVu.getDonGia(), thanhTien)) {
                // Cập nhật hoá đơn
                double tienDichVuMoi = hoaDon.getTienDichVu() + thanhTien;
                double tongTienMoi = hoaDon.getTienChoi() + tienDichVuMoi - hoaDon.getTienKhuyenMai();
                
                hoaDonDAO.updateStatus(hoaDon.getId(), "CHUA_THANH_TOAN");
                setFlash(request, "Service added successfully.");
            } else {
                setFlash(request, "Cannot add service.");
            }
        } catch (Exception e) {
            setFlash(request, "Error: " + e.getMessage());
        }
    }

    private void thanhToan(HttpServletRequest request, HttpServletResponse response) {
        try {
            int luotChoiId = parseInt(request.getParameter("luotChoiId"), -1);

            if (luotChoiId <= 0) {
                setFlash(request, "Invalid play session.");
                return;
            }

            Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(luotChoiId);
            if (!hoaDonOpt.isPresent()) {
                setFlash(request, "Invoice not found.");
                return;
            }

            HoaDon hoaDon = hoaDonOpt.get();
            
            // Áp dụng các sự kiện giảm giá
            double tienKhuyenMai = tinhKhuyenMai(luotChoiId, hoaDon.getTienChoi(), hoaDon.getTienDichVu());
            double tongTienFinal = hoaDon.getTienChoi() + hoaDon.getTienDichVu() - tienKhuyenMai;

            hoaDonDAO.updateStatus(hoaDon.getId(), "DA_THANH_TOAN");
            setFlash(request, "Payment completed. Total: " + String.format("%.0f", tongTienFinal) + " VND");
        } catch (Exception e) {
            setFlash(request, "Payment error: " + e.getMessage());
        }
    }

    private double tinhKhuyenMai(int luotChoiId, double tienChoi, double tienDichVu) {
        try {
            // Note: This is a simplified version and needs proper integration with the actual lượt chơi
            LocalDateTime thoiGianChoi = LocalDateTime.now();
            List<SuKien> suKienHoatDong = suKienDAO.getActiveByTime(thoiGianChoi);

            double tongChiPhi = tienChoi + tienDichVu;
            double khuyenMai = 0;

            for (SuKien sk : suKienHoatDong) {
                khuyenMai += tongChiPhi * (sk.getPhanTramGiamGia() / 100.0);
            }

            return Math.min(khuyenMai, tongChiPhi);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static void setFlash(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("flashMessage", message);
    }
}


