package controller;

import dao.LuotChoiDAO;
import dao.MayPSDAO;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.NhanVien;
import model.MayPS;
import utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@WebServlet(urlPatterns = {"/home"})
public class HomeController extends HttpServlet {
    private static final double DEFAULT_DON_GIA_GIO = 30000;
    private static final String STATUS_AVAILABLE_NEW = "BINH_THUONG";
    private static final String STATUS_PLAYING_NEW = "DANG_CHOI";

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
        NhanVien currentUser = getCurrentUser(request);

        if (currentUser == null) {
            setFlash(request, "Please login first.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (mayId <= 0) {
            setFlash(request, "Machine id is invalid.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        if ("open".equalsIgnoreCase(action)) {
            moMay(request, mayId, currentUser);
        } else if ("close".equalsIgnoreCase(action)) {
            dongMay(request, mayId);
        } else {
            setFlash(request, "Action is invalid.");
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void moMay(HttpServletRequest request, int mayId, NhanVien currentUser) {
        Optional<MayPS> mayOpt = mayPSDAO.findById(mayId);
        if (mayOpt.isEmpty()) {
            setFlash(request, "Machine not found.");
            return;
        }

        MayPS may = mayOpt.get();
        if (!isAvailableStatus(may.getTinhTrang())) {
            setFlash(request, "Machine " + may.getTenMay() + " is not available.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            boolean taoLuot = luotChoiDAO.batDauLuotChoi(conn, mayId, currentUser.getId(), DEFAULT_DON_GIA_GIO);
            boolean updateMay = taoLuot && mayPSDAO.updateTinhTrang(conn, mayId, STATUS_PLAYING_NEW);

            if (taoLuot && updateMay) {
                conn.commit();
                setFlash(request, "Opened " + may.getTenMay() + " successfully.");
            } else {
                rollbackQuietly(conn);
                setFlash(request, "Cannot open machine now.");
            }
        } catch (Exception e) {
            rollbackQuietly(conn);
            // best-effort rollback happens only when the connection is still open inside the try block
            setFlash(request, "Cannot open machine now: " + e.getMessage());
        } finally {
            closeQuietly(conn);
        }
    }

    private void dongMay(HttpServletRequest request, int mayId) {
        Optional<MayPS> mayOpt = mayPSDAO.findById(mayId);
        if (mayOpt.isEmpty()) {
            setFlash(request, "Machine not found.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Optional<Double> tongTienOpt = luotChoiDAO.ketThucLuotChoi(conn, mayId);
            if (tongTienOpt.isEmpty()) {
                rollbackQuietly(conn);
                setFlash(request, "No active play session found.");
                return;
            }

            boolean updateMay = mayPSDAO.updateTinhTrang(conn, mayId, STATUS_AVAILABLE_NEW);
            if (!updateMay) {
                rollbackQuietly(conn);
                setFlash(request, "Cannot close machine now.");
                return;
            }

            conn.commit();
            String tienText = String.format(Locale.US, "%.0f", tongTienOpt.get());
            setFlash(request, "Closed " + mayOpt.get().getTenMay() + ". Session total: " + tienText + " VND.");
        } catch (Exception e) {
            rollbackQuietly(conn);
            setFlash(request, "Cannot close machine now: " + e.getMessage());
        } finally {
            closeQuietly(conn);
        }
    }

    private static boolean isAvailableStatus(String tinhTrang) {
        return STATUS_AVAILABLE_NEW.equals(tinhTrang) || "TRONG".equals(tinhTrang);
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

    private static NhanVien getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute("currentUser");
        if (user instanceof NhanVien) {
            return (NhanVien) user;
        }
        return null;
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (Exception ignored) {
            // ignore rollback errors
        }
    }

    private static void closeQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (Exception ignored) {
            // ignore close errors
        }
    }
}