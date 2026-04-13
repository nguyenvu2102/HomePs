package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.NhanVien;

import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        Object sessionUser = session == null ? null : session.getAttribute("currentUser");
        NhanVien currentUser = sessionUser instanceof NhanVien ? (NhanVien) sessionUser : null;
        if (currentUser == null) {
            if (session != null) {
                session.setAttribute("flashMessage", "Please login first.");
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (isAdminOnlyPath(path) && !currentUser.isAdmin()) {
            session.setAttribute("flashMessage", "You do not have permission to access this page.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }

    private static boolean isPublicPath(String path) {
        return "/login".equals(path)
                || "/login.jsp".equals(path)
                || "/logout".equals(path)
                || path.startsWith("/resources/")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".svg");
    }

    private static boolean isAdminOnlyPath(String path) {
        return "/dichvu".equals(path)
                || "/sukien".equals(path)
                || "/thongke".equals(path);
    }
}



