package controller;

import dao.NhanVienDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.NhanVien;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = valueOf(request.getParameter("username")).trim();
        String password = valueOf(request.getParameter("password")).trim();

        NhanVien user = authenticate(username, password);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(toJson(Map.of(
                    "success", false,
                    "message", "Sai tai khoan hoac mat khau"
            )));
            return;
        }

        request.getSession(true).setAttribute("currentUser", user);
        response.getWriter().write(toJson(Map.of(
                "success", true,
                "message", "Dang nhap thanh cong",
                "username", username,
                "role", user.getVaiTro(),
                "displayName", user.getTenNhanVien()
        )));
    }

    private NhanVien authenticate(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            return nhanVienDAO.findById(1).orElseGet(() -> buildFallbackUser(1, "admin", "ADMIN"));
        }
        if ("staff".equals(username) && "staff".equals(password)) {
            return nhanVienDAO.findById(2).orElseGet(() -> buildFallbackUser(2, "staff", "NHAN_VIEN"));
        }
        return null;
    }

    private static NhanVien buildFallbackUser(int id, String name, String role) {
        NhanVien user = new NhanVien();
        user.setId(id);
        user.setTenNhanVien(name);
        user.setChucVu(role);
        user.setTrangThai("DANG_LAM");
        return user;
    }

    private static String valueOf(String value) {
        return value == null ? "" : value;
    }

    private static String toJson(Map<String, Object> payload) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('"').append(escape(entry.getKey())).append('"').append(':');
            Object value = entry.getValue();
            if (value == null) {
                builder.append("null");
            } else {
                builder.append('"').append(escape(String.valueOf(value))).append('"');
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

