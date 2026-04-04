<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Thong ke</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="m-0">Thong ke doanh thu</h2>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/home">Back to dashboard</a>
    </div>

    <div class="card mb-3">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/thongke" method="get" class="row g-2 align-items-end">
                <div class="col-md-3">
                    <label class="form-label">Loai thong ke</label>
                    <select class="form-select" name="loai" id="loaiSelect">
                        <option value="ngay" ${loai == 'ngay' ? 'selected' : ''}>Theo ngay</option>
                        <option value="tuan" ${loai == 'tuan' ? 'selected' : ''}>Theo tuan</option>
                        <option value="thang" ${loai == 'thang' ? 'selected' : ''}>Theo thang</option>
                    </select>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Ngay</label>
                    <input class="form-control" type="date" name="ngay">
                </div>

                <div class="col-md-2">
                    <label class="form-label">Tuan</label>
                    <input class="form-control" type="number" min="1" max="53" name="tuan" placeholder="14">
                </div>

                <div class="col-md-2">
                    <label class="form-label">Thang</label>
                    <input class="form-control" type="number" min="1" max="12" name="thang" placeholder="4">
                </div>

                <div class="col-md-2">
                    <label class="form-label">Nam</label>
                    <input class="form-control" type="number" min="2000" name="nam" placeholder="2026">
                </div>

                <div class="col-md-2">
                    <button class="btn btn-primary w-100" type="submit">Xem thong ke</button>
                </div>
            </form>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-bordered table-hover">
            <thead class="table-light">
            <tr>
                <th>May</th>
                <th>So luot choi</th>
                <th>Doanh thu choi</th>
                <th>Doanh thu dich vu</th>
                <th>Tien khuyen mai</th>
                <th>Tong doanh thu</th>
                <th>Kieu</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${danhSach}" var="tk">
                <tr>
                    <td>${tk.tenMay}</td>
                    <td>${tk.soLuotChoi}</td>
                    <td>${tk.doanhThuChoi}</td>
                    <td>${tk.doanhThuDichVu}</td>
                    <td>${tk.tienKhuyenMai}</td>
                    <td><strong>${tk.tongDoanhThu}</strong></td>
                    <td>${tk.kieuThongKe}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty danhSach}">
                <tr><td colspan="7" class="text-center text-muted">Khong co du lieu.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>

