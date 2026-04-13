package controller;

import dao.NhanVienDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.NhanVien;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") instanceof NhanVien) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        List<NhanVien> danhSachNhanVien = nhanVienDAO.getAllActive();
        request.setAttribute("danhSachNhanVien", danhSachNhanVien);
        copyFlashMessage(request);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int nhanVienId = parseInt(request.getParameter("nhanVienId"), -1);
        if (nhanVienId <= 0) {
            setFlash(request, "Please choose an employee.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Optional<NhanVien> nhanVienOpt = nhanVienDAO.findById(nhanVienId);
        if (nhanVienOpt.isEmpty()) {
            setFlash(request, "Employee not found.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        NhanVien nhanVien = nhanVienOpt.get();
        if (!"DANG_LAM".equalsIgnoreCase(nhanVien.getTrangThai())) {
            setFlash(request, "This employee is not active.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getSession(true).setAttribute("currentUser", nhanVien);
        setFlash(request, "Welcome, " + nhanVien.getTenNhanVien() + "!");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void copyFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        String flashMessage = (String) session.getAttribute("flashMessage");
        if (flashMessage != null) {
            request.setAttribute("flashMessage", flashMessage);
            session.removeAttribute("flashMessage");
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

