package controller;

import com.google.gson.Gson;
import dao.ChiTietHoaDonDAO;
import dao.DichVuDAO;
import dao.HoaDonDAO;
import dao.LuotChoiDAO;
import dao.SuKienDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DichVu;
import model.HoaDon;
import model.LuotChoi;
import model.SuKien;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/hoadon"})
public class HoaDonController extends HttpServlet {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final LuotChoiDAO luotChoiDAO = new LuotChoiDAO();
    private final SuKienDAO suKienDAO = new SuKienDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("addService".equalsIgnoreCase(action)) {
            themDichVu(request, response);
            return;
        }

        if ("checkout".equalsIgnoreCase(action)) {
            thanhToan(request, response);
            return;
        }

        write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                "success", false,
                "message", "Action khong hop le"
        ));
    }

    private void themDichVu(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int luotChoiId = parseInt(request.getParameter("luotChoiId"), -1);
            int dichVuId = parseInt(request.getParameter("dichVuId"), -1);
            int soLuong = parseInt(request.getParameter("soLuong"), 1);

            if (luotChoiId <= 0 || dichVuId <= 0 || soLuong <= 0) {
                write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                        "success", false,
                        "message", "Invalid input parameters."
                ));
                return;
            }

            Optional<HoaDon> hoaDonOpt = hoaDonDAO.findByLuotChoiId(luotChoiId);
            Optional<DichVu> dichVuOpt = dichVuDAO.findById(dichVuId);

            if (hoaDonOpt.isEmpty() || dichVuOpt.isEmpty()) {
                write(response, HttpServletResponse.SC_NOT_FOUND, Map.of(
                        "success", false,
                        "message", "Invoice or service not found."
                ));
                return;
            }

            HoaDon hoaDon = hoaDonOpt.get();
            DichVu dichVu = dichVuOpt.get();
            double thanhTien = dichVu.getDonGia() * soLuong;

            if (chiTietDAO.create(hoaDon.getId(), dichVuId, dichVu.getTenDichVu(), soLuong, dichVu.getDonGia(), thanhTien)) {
                write(response, HttpServletResponse.SC_OK, Map.of(
                        "success", true,
                        "message", "Service added successfully."
                ));
            } else {
                write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                        "success", false,
                        "message", "Cannot add service."
                ));
            }
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            ));
        }
    }

    private void thanhToan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int luotChoiId = parseInt(request.getParameter("luotChoiId"), -1);
            if (luotChoiId <= 0) {
                write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                        "success", false,
                        "message", "Invalid play session."
                ));
                return;
            }

            HoaDon hoaDon = resolveInvoice(luotChoiId);
            if (hoaDon == null) {
                write(response, HttpServletResponse.SC_NOT_FOUND, Map.of(
                        "success", false,
                        "message", "Invoice not found."
                ));
                return;
            }

            double tienKhuyenMai = tinhKhuyenMai(luotChoiId, hoaDon.getTienChoi(), hoaDon.getTienDichVu());
            double tongTienFinal = hoaDon.getTienChoi() + hoaDon.getTienDichVu() - tienKhuyenMai;

            hoaDonDAO.updateStatus(hoaDon.getId(), "DA_THANH_TOAN");
            write(response, HttpServletResponse.SC_OK, Map.of(
                    "success", true,
                    "message", "Payment completed.",
                    "luotchoiId", luotChoiId,
                    "tienChoi", hoaDon.getTienChoi(),
                    "tienDichVu", hoaDon.getTienDichVu(),
                    "tienKhuyenMai", tienKhuyenMai,
                    "tongTien", tongTienFinal
            ));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Payment error: " + e.getMessage()
            ));
        }
    }

    private HoaDon resolveInvoice(int luotChoiId) {
        Optional<HoaDon> existing = hoaDonDAO.findByLuotChoiId(luotChoiId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Optional<LuotChoi> luotChoiOpt = luotChoiDAO.findById(luotChoiId);
        if (luotChoiOpt.isEmpty()) {
            return null;
        }

        LuotChoi luotChoi = luotChoiOpt.get();
        double tienChoi = luotChoi.getTongTienGio();
        hoaDonDAO.create(luotChoiId, tienChoi, 0d, 0d, tienChoi);
        return hoaDonDAO.findByLuotChoiId(luotChoiId).orElse(null);
    }

    private double tinhKhuyenMai(int luotChoiId, double tienChoi, double tienDichVu) {
        try {
            LocalDateTime thoiGianChoi = LocalDateTime.now();
            List<SuKien> suKienHoatDong = suKienDAO.getActiveByTime(thoiGianChoi);

            double tongChiPhi = tienChoi + tienDichVu;
            double khuyenMai = 0;

            for (SuKien sk : suKienHoatDong) {
                khuyenMai += tongChiPhi * (sk.getPhanTramGiamGia() / 100.0);
            }

            return Math.min(khuyenMai, tongChiPhi);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void write(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(payload));
    }
}
