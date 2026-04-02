package controller;

import dao.DichVuDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DichVu;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/dichvu"})
public class DichVuController extends HttpServlet {
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String editIdParam = request.getParameter("editId");
        int editId = parseInt(editIdParam, -1);

        if (editId > 0) {
            Optional<DichVu> editItem = dichVuDAO.findById(editId);
            if (editItem.isPresent()) {
                request.setAttribute("editDichVu", editItem.get());
            }
        }

        List<DichVu> list = dichVuDAO.getAll();
        request.setAttribute("danhSachDichVu", list);

        HttpSession session = request.getSession(false);
        if (session != null) {
            String flashMessage = (String) session.getAttribute("flashMessage");
            if (flashMessage != null) {
                request.setAttribute("flashMessage", flashMessage);
                session.removeAttribute("flashMessage");
            }
        }

        request.getRequestDispatcher("dichvu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        if ("create".equalsIgnoreCase(action)) {
            taoDichVu(request);
        } else if ("update".equalsIgnoreCase(action)) {
            capNhatDichVu(request);
        } else if ("delete".equalsIgnoreCase(action)) {
            xoaDichVu(request);
        } else {
            setFlash(request, "Action is invalid.");
        }

        response.sendRedirect(request.getContextPath() + "/dichvu");
    }

    private void taoDichVu(HttpServletRequest request) {
        String tenDichVu = request.getParameter("tenDichVu");
        String loai = normalizeLoai(request.getParameter("loai"));
        double donGia = parseDouble(request.getParameter("donGia"), -1);

        if (isBlank(tenDichVu) || donGia < 0) {
            setFlash(request, "Service name/price is invalid.");
            return;
        }

        boolean ok = dichVuDAO.create(tenDichVu.trim(), donGia, loai);
        setFlash(request, ok ? "Created service successfully." : "Cannot create service.");
    }

    private void capNhatDichVu(HttpServletRequest request) {
        int id = parseInt(request.getParameter("id"), -1);
        String tenDichVu = request.getParameter("tenDichVu");
        String loai = normalizeLoai(request.getParameter("loai"));
        double donGia = parseDouble(request.getParameter("donGia"), -1);

        if (id <= 0 || isBlank(tenDichVu) || donGia < 0) {
            setFlash(request, "Update data is invalid.");
            return;
        }

        boolean ok = dichVuDAO.update(id, tenDichVu.trim(), donGia, loai);
        setFlash(request, ok ? "Updated service successfully." : "Cannot update service.");
    }

    private void xoaDichVu(HttpServletRequest request) {
        int id = parseInt(request.getParameter("id"), -1);
        if (id <= 0) {
            setFlash(request, "Service id is invalid.");
            return;
        }

        boolean ok = dichVuDAO.deleteById(id);
        setFlash(request, ok ? "Deleted service successfully." : "Cannot delete service.");
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String normalizeLoai(String loai) {
        if ("DO_AN".equalsIgnoreCase(loai) || "NUOC".equalsIgnoreCase(loai)) {
            return loai.toUpperCase();
        }
        return "KHAC";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void setFlash(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute("flashMessage", message);
    }
}

