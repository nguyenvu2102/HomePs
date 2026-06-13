package controller;

import com.google.gson.Gson;
import dao.ChiTietHoaDonDAO;
import dao.DichVuDAO;
import dao.HoaDonDAO;
import dao.LuotChoiDAO;
import model.DichVu;
import model.HoaDon;
import model.LuotChoi;
import utils.DBConnection;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/api/services"})
public class ApiDichVuController extends HttpServlet {
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        List<DichVu> dichVuList = dichVuDAO.getAll();
        response.getWriter().write(gson.toJson(dichVuList));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int machineId = Integer.parseInt(request.getParameter("machineId"));
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            Optional<LuotChoi> activeSessionOpt = luotChoiDAO.findActiveByMayId(machineId);
            if (activeSessionOpt.isEmpty()) {
                write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Máy không có lượt chơi hoạt động."));
                return;
            }
            LuotChoi activeSession = activeSessionOpt.get();

            Optional<DichVu> serviceOpt = dichVuDAO.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                write(response, HttpServletResponse.SC_NOT_FOUND, Map.of("success", false, "message", "Không tìm thấy dịch vụ."));
                return;
            }
            DichVu service = serviceOpt.get();

            addServiceToInvoice(activeSession, service, quantity);

            write(response, HttpServletResponse.SC_OK, Map.of("success", true, "message", "Đã thêm dịch vụ thành công."));

        } catch (NumberFormatException e) {
            write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "ID máy, dịch vụ hoặc số lượng không hợp lệ."));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of("success", false, "message", "Lỗi server: " + e.getMessage()));
        }
    }

    private void addServiceToInvoice(LuotChoi luotChoi, DichVu dichVu, int quantity) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Find or create an invoice for the current session
                Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(conn, luotChoi.getId());
                HoaDon hoaDon;
                if (hoaDonOpt.isEmpty()) {
                    // Create a new invoice if it doesn't exist
                    hoaDonDAO.create(conn, luotChoi.getId(), 0, 0, 0, 0);
                    hoaDon = hoaDonDAO.findByLuotChoiId(conn, luotChoi.getId()).orElseThrow(() -> new SQLException("Failed to create invoice"));
                } else {
                    hoaDon = hoaDonOpt.get();
                }

                // Add the service to the invoice details
                double thanhTien = dichVu.getDonGia() * quantity;
                chiTietHoaDonDAO.create(conn, hoaDon.getId(), dichVu.getId(), dichVu.getTenDichVu(), quantity, dichVu.getDonGia(), thanhTien);

                // Update the total service cost in the main invoice table
                hoaDonDAO.updateTienDichVu(conn, hoaDon.getId());

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    private void write(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(payload));
    }
}
