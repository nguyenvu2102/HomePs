<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Su kien</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="m-0">Quan ly su kien</h2>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/home">Back to dashboard</a>
    </div>

    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="alert alert-info" role="alert">${sessionScope.flashMessage}</div>
        <c:remove var="flashMessage" scope="session"/>
    </c:if>

    <div class="card mb-4">
        <div class="card-body">
            <h5 class="card-title">Tao su kien moi</h5>
            <form action="${pageContext.request.contextPath}/sukien" method="post" class="row g-2">
                <input type="hidden" name="action" value="create">

                <div class="col-md-4">
                    <label class="form-label">Ten su kien</label>
                    <input class="form-control" name="tenSuKien" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">% giam</label>
                    <input class="form-control" type="number" min="1" max="100" name="phanTramGiamGia" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Loai</label>
                    <select class="form-select" name="loaiSuKien">
                        <option value="THEO_GIO">THEO_GIO</option>
                        <option value="THEO_NGAY">THEO_NGAY</option>
                        <option value="THEO_TUAN">THEO_TUAN</option>
                        <option value="THEO_THANG">THEO_THANG</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Gio ap dung</label>
                    <input class="form-control" name="gioApDung" placeholder="11:00-14:00">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Ngay ap dung</label>
                    <input class="form-control" name="ngayApDung" placeholder="2026-04-04">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Ngay bat dau</label>
                    <input class="form-control" type="date" name="ngayBatDau" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Ngay ket thuc</label>
                    <input class="form-control" type="date" name="ngayKetThuc" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Mo ta</label>
                    <input class="form-control" name="moTa">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button class="btn btn-primary w-100" type="submit">Tao su kien</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th style="width: 70px">ID</th>
            <th>Ten su kien</th>
            <th style="width: 100px">% giam</th>
            <th style="width: 140px">Loai</th>
            <th style="width: 220px">Thoi gian</th>
            <th style="width: 200px">Thao tac</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${danhSachSuKien}" var="sk">
            <tr>
                <td>${sk.id}</td>
                <td>${sk.tenSuKien}</td>
                <td>${sk.phanTramGiamGia}</td>
                <td>${sk.loaiSuKien}</td>
                <td>${sk.ngayBatDau} - ${sk.ngayKetThuc}</td>
                <td>
                    <a class="btn btn-sm btn-warning" href="${pageContext.request.contextPath}/sukien?action=detail&id=${sk.id}">Edit</a>
                    <form action="${pageContext.request.contextPath}/sukien" method="post" style="display:inline-block" onsubmit="return confirm('Delete this event?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${sk.id}">
                        <button class="btn btn-sm btn-danger" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty danhSachSuKien}">
            <tr><td colspan="6" class="text-center text-muted">Chua co su kien.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>

