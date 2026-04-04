package controller;

import dao.ThongKeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ThongKe;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@WebServlet(urlPatterns = {"/thongke"})
public class ThongKeController extends HttpServlet {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String loai = request.getParameter("loai");

        if ("tuan".equalsIgnoreCase(loai)) {
            thongKeTuan(request, response);
        } else if ("thang".equalsIgnoreCase(loai)) {
            thongKeThang(request, response);
        } else {
            thongKeNgay(request, response);
        }
    }

    private void thongKeNgay(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ngayStr = request.getParameter("ngay");
        Date ngay = ngayStr != null && !ngayStr.isEmpty() 
            ? Date.valueOf(ngayStr) 
            : Date.valueOf(LocalDate.now());

        List<ThongKe> danhSach = thongKeDAO.getThongKeTheoNgay(ngay);
        request.setAttribute("loai", "ngay");
        request.setAttribute("ngay", ngay);
        request.setAttribute("danhSach", danhSach);
        request.getRequestDispatcher("thongke.jsp").forward(request, response);
    }

    private void thongKeTuan(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tuanStr = request.getParameter("tuan");
        String namStr = request.getParameter("nam");

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        if (tuanStr != null && !tuanStr.isEmpty() && namStr != null && !namStr.isEmpty()) {
            int tuan = Integer.parseInt(tuanStr);
            int nam = Integer.parseInt(namStr);
            // Tính toán ngày đầu tuần dựa trên số tuần
            LocalDate firstDayOfYear = LocalDate.of(nam, 1, 1);
            startOfWeek = firstDayOfYear.plusWeeks(tuan - 1).minusDays(firstDayOfYear.getDayOfWeek().getValue() - 1);
            endOfWeek = startOfWeek.plusDays(6);
        }

        List<ThongKe> danhSach = thongKeDAO.getThongKeTheoTuan(Date.valueOf(startOfWeek), Date.valueOf(endOfWeek));
        request.setAttribute("loai", "tuan");
        request.setAttribute("startOfWeek", startOfWeek);
        request.setAttribute("endOfWeek", endOfWeek);
        request.setAttribute("danhSach", danhSach);
        request.getRequestDispatcher("thongke.jsp").forward(request, response);
    }

    private void thongKeThang(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String thangStr = request.getParameter("thang");
        String namStr = request.getParameter("nam");

        int thang = thangStr != null && !thangStr.isEmpty() ? Integer.parseInt(thangStr) : LocalDate.now().getMonthValue();
        int nam = namStr != null && !namStr.isEmpty() ? Integer.parseInt(namStr) : LocalDate.now().getYear();

        List<ThongKe> danhSach = thongKeDAO.getThongKeTheoThang(thang, nam);
        request.setAttribute("loai", "thang");
        request.setAttribute("thang", thang);
        request.setAttribute("nam", nam);
        request.setAttribute("danhSach", danhSach);
        request.getRequestDispatcher("thongke.jsp").forward(request, response);
    }
}

