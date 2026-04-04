package controller;

import dao.SuKienDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SuKien;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/sukien"})
public class SuKienController extends HttpServlet {
    private final SuKienDAO suKienDAO = new SuKienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("list".equalsIgnoreCase(action)) {
            danhSachSuKien(request, response);
        } else if ("detail".equalsIgnoreCase(action)) {
            chiTietSuKien(request, response);
        } else {
            danhSachSuKien(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        if ("create".equalsIgnoreCase(action)) {
            taoSuKien(request, response);
        } else if ("update".equalsIgnoreCase(action)) {
            capNhatSuKien(request, response);
        } else if ("delete".equalsIgnoreCase(action)) {
            xoaSuKien(request, response);
        }

        response.sendRedirect(request.getContextPath() + "/sukien?action=list");
    }

    private void danhSachSuKien(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<SuKien> danhSach = suKienDAO.getAll();
        request.setAttribute("danhSachSuKien", danhSach);
        request.getRequestDispatcher("sukien-list.jsp").forward(request, response);
    }

    private void chiTietSuKien(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"), -1);
        
        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/sukien");
            return;
        }

        Optional<SuKien> suKienOpt = suKienDAO.findById(id);
        request.setAttribute("suKien", suKienOpt.orElse(null));
        request.getRequestDispatcher("sukien-detail.jsp").forward(request, response);
    }

    private void taoSuKien(HttpServletRequest request, HttpServletResponse response) {
        try {
            String tenSuKien = request.getParameter("tenSuKien");
            String moTa = request.getParameter("moTa");
            int phanTramGiamGia = parseInt(request.getParameter("phanTramGiamGia"), 0);
            String loaiSuKien = request.getParameter("loaiSuKien"); // THEO_GIO, THEO_NGAY, ...
            String gioApDung = request.getParameter("gioApDung");
            String ngayApDung = request.getParameter("ngayApDung");
            String ngayBatDauStr = request.getParameter("ngayBatDau");
            String ngayKetThucStr = request.getParameter("ngayKetThuc");

            if (tenSuKien == null || tenSuKien.trim().isEmpty() || phanTramGiamGia <= 0) {
                setFlash(request, "Invalid event data.");
                return;
            }

            Timestamp ngayBatDau = Timestamp.valueOf(ngayBatDauStr + " 00:00:00");
            Timestamp ngayKetThuc = Timestamp.valueOf(ngayKetThucStr + " 23:59:59");

            if (suKienDAO.create(tenSuKien, moTa, phanTramGiamGia, loaiSuKien, gioApDung, ngayApDung, 
                                ngayBatDau, ngayKetThuc)) {
                setFlash(request, "Event created successfully.");
            } else {
                setFlash(request, "Cannot create event.");
            }
        } catch (Exception e) {
            setFlash(request, "Error: " + e.getMessage());
        }
    }

    private void capNhatSuKien(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = parseInt(request.getParameter("id"), -1);
            String tenSuKien = request.getParameter("tenSuKien");
            String moTa = request.getParameter("moTa");
            int phanTramGiamGia = parseInt(request.getParameter("phanTramGiamGia"), 0);
            String loaiSuKien = request.getParameter("loaiSuKien");
            String gioApDung = request.getParameter("gioApDung");
            String ngayApDung = request.getParameter("ngayApDung");
            String ngayBatDauStr = request.getParameter("ngayBatDau");
            String ngayKetThucStr = request.getParameter("ngayKetThuc");

            if (id <= 0 || tenSuKien == null || tenSuKien.trim().isEmpty()) {
                setFlash(request, "Invalid event data.");
                return;
            }

            Timestamp ngayBatDau = Timestamp.valueOf(ngayBatDauStr + " 00:00:00");
            Timestamp ngayKetThuc = Timestamp.valueOf(ngayKetThucStr + " 23:59:59");

            if (suKienDAO.update(id, tenSuKien, moTa, phanTramGiamGia, loaiSuKien, gioApDung, 
                                ngayApDung, ngayBatDau, ngayKetThuc)) {
                setFlash(request, "Event updated successfully.");
            } else {
                setFlash(request, "Cannot update event.");
            }
        } catch (Exception e) {
            setFlash(request, "Error: " + e.getMessage());
        }
    }

    private void xoaSuKien(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = parseInt(request.getParameter("id"), -1);

            if (id <= 0) {
                setFlash(request, "Invalid event id.");
                return;
            }

            if (suKienDAO.deactivate(id)) {
                setFlash(request, "Event deleted successfully.");
            } else {
                setFlash(request, "Cannot delete event.");
            }
        } catch (Exception e) {
            setFlash(request, "Error: " + e.getMessage());
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

