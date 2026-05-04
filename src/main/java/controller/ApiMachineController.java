package controller;

import dao.MayPSDAO;
import dao.LuotChoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MayPS;
import model.NhanVien;
import utils.DBConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;

@WebServlet(urlPatterns = {"/api/machines"})
public class ApiMachineController extends HttpServlet {
    private final MayPSDAO mayPSDAO = new MayPSDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        NhanVien currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Not authenticated\"}");
            return;
        }

        List<MayPS> machines = mayPSDAO.getAllMay();
        response.getWriter().write(gson.toJson(machines));
    }

    private static NhanVien getCurrentUser(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session == null) return null;
        Object user = session.getAttribute("currentUser");
        return user instanceof NhanVien ? (NhanVien) user : null;
    }
}

