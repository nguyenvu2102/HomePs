package controller;

import dao.LuotChoiDAO;
import dao.MayPSDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.MayPS;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@WebServlet(urlPatterns = {"/home"})
public class HomeController extends HttpServlet {
    private static final double DEFAULT_DON_GIA_GIO = 12000;

    private final MayPSDAO mayPSDAO = new MayPSDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy dữ liệu từ DB
        List<MayPS> listMay = mayPSDAO.getAllMay();

        // 2. Đẩy dữ liệu vào request attribute
        request.setAttribute("danhSachMay", listMay);

        HttpSession session = request.getSession(false);
        if (session != null) {
            String flashMessage = (String) session.getAttribute("flashMessage");
            if (flashMessage != null) {
                request.setAttribute("flashMessage", flashMessage);
                session.removeAttribute("flashMessage");
            }
        }

        // 3. Chuyển hướng sang trang JSP để hiển thị
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        int mayId = parseInt(request.getParameter("mayId"), -1);

        if (mayId <= 0) {
            setFlash(request, "Machine id is invalid.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        if ("open".equalsIgnoreCase(action)) {
            moMay(request, mayId);
        } else if ("close".equalsIgnoreCase(action)) {
            dongMay(request, mayId);
        } else {
            setFlash(request, "Action is invalid.");
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void moMay(HttpServletRequest request, int mayId) {
        Optional<MayPS> mayOpt = mayPSDAO.findById(mayId);
        if (!mayOpt.isPresent()) {
            setFlash(request, "Machine not found.");
            return;
        }

        MayPS may = mayOpt.get();
        if (!"BINH_THUONG".equals(may.getTinhTrang())) {
            setFlash(request, "Machine " + may.getTenMay() + " is not available.");
            return;
        }

        int nhanVienId = parseInt(request.getParameter("nhanVienId"), 1);
        boolean taoLuot = luotChoiDAO.batDauLuotChoi(mayId, nhanVienId, DEFAULT_DON_GIA_GIO);
        boolean updateMay = mayPSDAO.updateTinhTrang(mayId, "DANG_CHOI");

        if (taoLuot && updateMay) {
            setFlash(request, "Opened " + may.getTenMay() + " successfully.");
        } else {
            setFlash(request, "Cannot open machine now.");
        }
    }

    private void dongMay(HttpServletRequest request, int mayId) {
        Optional<MayPS> mayOpt = mayPSDAO.findById(mayId);
        if (!mayOpt.isPresent()) {
            setFlash(request, "Machine not found.");
            return;
        }

        Optional<Double> tongTienOpt = luotChoiDAO.ketThucLuotChoi(mayId);
        if (!tongTienOpt.isPresent()) {
            setFlash(request, "No active play session found.");
            return;
        }

        mayPSDAO.updateTinhTrang(mayId, "BINH_THUONG");
        String tienText = String.format(Locale.US, "%.0f", tongTienOpt.get());
        setFlash(request, "Closed " + mayOpt.get().getTenMay() + ". Session total: " + tienText + " VND.");
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