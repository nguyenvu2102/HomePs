package controller;

import com.google.gson.Gson;
import dao.SuKienDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.SuKien;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/events"})
public class ApiEventController extends HttpServlet {
    private final Gson gson = new Gson();
    private final SuKienDAO suKienDAO = new SuKienDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Map<String, Object>> events = new ArrayList<>();
            for (SuKien suKien : suKienDAO.getAll()) {
                events.add(toMap(suKien));
            }

            response.getWriter().write(gson.toJson(Map.of(
                    "success", true,
                    "events", events
            )));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Không tải được sự kiện: " + e.getMessage()
            ));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = text(request.getParameter("action")).toLowerCase();
        try {
            if ("delete".equals(action)) {
                int id = parseInt(request.getParameter("id"), -1);
                if (id <= 0) {
                    write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Thiếu mã sự kiện"));
                    return;
                }
                boolean deleted = suKienDAO.deactivate(id);
                write(response, deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND, Map.of(
                        "success", deleted,
                        "message", deleted ? "Đã tắt sự kiện" : "Không tìm thấy sự kiện"
                ));
                return;
            }

            int id = parseInt(request.getParameter("id"), -1);
            String tenSuKien = text(request.getParameter("tenSuKien"));
            String moTa = text(request.getParameter("moTa"));
            int phanTramGiamGia = parseInt(request.getParameter("phanTramGiamGia"), 0);
            String loaiSuKien = text(request.getParameter("loaiSuKien"));
            String gioApDung = text(request.getParameter("gioApDung"));
            String ngayApDung = text(request.getParameter("ngayApDung"));
            Timestamp ngayBatDau = parseTimestamp(request.getParameter("ngayBatDau"), false);
            Timestamp ngayKetThuc = parseTimestamp(request.getParameter("ngayKetThuc"), true);

            if (tenSuKien.isEmpty() || phanTramGiamGia <= 0 || phanTramGiamGia > 100 || ngayBatDau == null || ngayKetThuc == null) {
                write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of(
                        "success", false,
                        "message", "Vui lòng nhập tên, % giảm và thời gian áp dụng hợp lệ"
                ));
                return;
            }
            if (loaiSuKien.isEmpty()) {
                loaiSuKien = "THEO_KHOANG_THOI_GIAN";
            }

            boolean ok;
            if ("update".equals(action)) {
                if (id <= 0) {
                    write(response, HttpServletResponse.SC_BAD_REQUEST, Map.of("success", false, "message", "Thiếu mã sự kiện"));
                    return;
                }
                ok = suKienDAO.update(id, tenSuKien, moTa, phanTramGiamGia, loaiSuKien, gioApDung, ngayApDung, ngayBatDau, ngayKetThuc);
            } else {
                ok = suKienDAO.create(tenSuKien, moTa, phanTramGiamGia, loaiSuKien, gioApDung, ngayApDung, ngayBatDau, ngayKetThuc);
            }

            write(response, ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", ok,
                    "message", ok ? "Đã lưu sự kiện" : "Không thể lưu sự kiện"
            ));
        } catch (Exception e) {
            write(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "success", false,
                    "message", "Lỗi sự kiện: " + e.getMessage()
            ));
        }
    }

    private static Map<String, Object> toMap(SuKien suKien) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", suKien.getId());
        item.put("tenSuKien", suKien.getTenSuKien());
        item.put("moTa", suKien.getMoTa());
        item.put("phanTramGiamGia", suKien.getPhanTramGiamGia());
        item.put("loaiSuKien", suKien.getLoaiSuKien());
        item.put("gioApDung", suKien.getGioApDung());
        item.put("ngayApDung", suKien.getNgayApDung());
        item.put("ngayBatDau", toMillis(suKien.getNgayBatDau()));
        item.put("ngayKetThuc", toMillis(suKien.getNgayKetThuc()));
        item.put("trangThai", suKien.isTrangThai());
        return item;
    }

    private static Timestamp parseTimestamp(String raw, boolean endOfDay) {
        String value = text(raw);
        if (value.isEmpty()) {
            return null;
        }
        try {
            if (value.contains("T")) {
                return Timestamp.valueOf(LocalDateTime.parse(value));
            }
            LocalDate date = LocalDate.parse(value);
            LocalTime time = endOfDay ? LocalTime.of(23, 59, 59) : LocalTime.MIN;
            return Timestamp.valueOf(LocalDateTime.of(date, time));
        } catch (Exception e) {
            return null;
        }
    }

    private static Long toMillis(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.getTime();
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private void write(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.getWriter().write(gson.toJson(payload));
    }
}
