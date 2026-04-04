<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Hoa don</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="m-0">Hoa don luot choi #${luotChoiId}</h2>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/home">Back to dashboard</a>
    </div>

    <c:if test="${not empty sessionScope.flashMessage}">
        <div class="alert alert-info" role="alert">${sessionScope.flashMessage}</div>
        <c:remove var="flashMessage" scope="session"/>
    </c:if>

    <div class="row g-3 mb-3">
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Thong tin hoa don</h5>
                    <c:choose>
                        <c:when test="${not empty hoaDon}">
                            <p class="mb-1"><strong>ID:</strong> ${hoaDon.id}</p>
                            <p class="mb-1"><strong>Tien choi:</strong> ${hoaDon.tienChoi}</p>
                            <p class="mb-1"><strong>Tien dich vu:</strong> ${hoaDon.tienDichVu}</p>
                            <p class="mb-1"><strong>Tien khuyen mai:</strong> ${hoaDon.tienKhuyenMai}</p>
                            <p class="mb-1"><strong>Tong tien:</strong> ${hoaDon.tongTien}</p>
                            <p class="mb-0"><strong>Trang thai:</strong> ${hoaDon.trangThai}</p>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Chua co hoa don cho luot choi nay.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Them dich vu vao hoa don</h5>
                    <form action="${pageContext.request.contextPath}/hoadon" method="post" class="row g-2">
                        <input type="hidden" name="action" value="addService">
                        <input type="hidden" name="luotChoiId" value="${luotChoiId}">

                        <div class="col-12">
                            <label class="form-label">Dich vu</label>
                            <select class="form-select" name="dichVuId" required>
                                <c:forEach items="${danhSachDichVu}" var="dv">
                                    <option value="${dv.id}">${dv.tenDichVu} - ${dv.donGia}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-12">
                            <label class="form-label">So luong</label>
                            <input class="form-control" type="number" min="1" value="1" name="soLuong" required>
                        </div>

                        <div class="col-12 d-flex gap-2">
                            <button class="btn btn-primary" type="submit">Them dich vu</button>
                        </div>
                    </form>
                    <form action="${pageContext.request.contextPath}/hoadon" method="post" class="mt-2">
                        <input type="hidden" name="action" value="checkout">
                        <input type="hidden" name="luotChoiId" value="${luotChoiId}">
                        <button class="btn btn-success" type="submit">Thanh toan</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="card">
        <div class="card-body">
            <h5 class="card-title">Chi tiet dich vu</h5>
            <table class="table table-bordered table-hover mb-0">
                <thead class="table-light">
                <tr>
                    <th style="width: 80px">ID</th>
                    <th>Ten dich vu</th>
                    <th style="width: 110px">So luong</th>
                    <th style="width: 140px">Don gia</th>
                    <th style="width: 140px">Thanh tien</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${chiTietList}" var="ct">
                    <tr>
                        <td>${ct.id}</td>
                        <td>${ct.tenDichVu}</td>
                        <td>${ct.soLuong}</td>
                        <td>${ct.donGia}</td>
                        <td>${ct.thanhTien}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty chiTietList}">
                    <tr>
                        <td colspan="5" class="text-center text-muted">Chua co dich vu nao.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>

