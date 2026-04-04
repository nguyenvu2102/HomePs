<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Cap nhat su kien</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="m-0">Cap nhat su kien</h2>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/sukien?action=list">Back to list</a>
    </div>

    <c:if test="${empty suKien}">
        <div class="alert alert-warning">Khong tim thay su kien.</div>
    </c:if>

    <c:if test="${not empty suKien}">
        <div class="card">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/sukien" method="post" class="row g-2">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${suKien.id}">

                    <div class="col-md-4">
                        <label class="form-label">Ten su kien</label>
                        <input class="form-control" name="tenSuKien" value="${suKien.tenSuKien}" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">% giam</label>
                        <input class="form-control" type="number" min="1" max="100" name="phanTramGiamGia" value="${suKien.phanTramGiamGia}" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Loai</label>
                        <select class="form-select" name="loaiSuKien">
                            <option value="THEO_GIO" ${suKien.loaiSuKien == 'THEO_GIO' ? 'selected' : ''}>THEO_GIO</option>
                            <option value="THEO_NGAY" ${suKien.loaiSuKien == 'THEO_NGAY' ? 'selected' : ''}>THEO_NGAY</option>
                            <option value="THEO_TUAN" ${suKien.loaiSuKien == 'THEO_TUAN' ? 'selected' : ''}>THEO_TUAN</option>
                            <option value="THEO_THANG" ${suKien.loaiSuKien == 'THEO_THANG' ? 'selected' : ''}>THEO_THANG</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Gio ap dung</label>
                        <input class="form-control" name="gioApDung" value="${suKien.gioApDung}">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Ngay ap dung</label>
                        <input class="form-control" name="ngayApDung" value="${suKien.ngayApDung}">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Ngay bat dau (yyyy-MM-dd)</label>
                        <input class="form-control" type="text" name="ngayBatDau" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Ngay ket thuc (yyyy-MM-dd)</label>
                        <input class="form-control" type="text" name="ngayKetThuc" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Mo ta</label>
                        <input class="form-control" name="moTa" value="${suKien.moTa}">
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button class="btn btn-primary w-100" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </c:if>
</div>
</body>
</html>

