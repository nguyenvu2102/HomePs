<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
<div class="container py-5" style="max-width: 640px;">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h2 class="mb-2">HomePS Login</h2>
            <p class="text-muted mb-4">Chon nhan vien de dang nhap vao he thong.</p>

            <c:if test="${not empty flashMessage}">
                <div class="alert alert-info">${flashMessage}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" class="row g-3">
                <div class="col-12">
                    <label class="form-label">Nhan vien</label>
                    <select class="form-select" name="nhanVienId" required>
                        <option value="">-- Chon nhan vien --</option>
                        <c:forEach items="${danhSachNhanVien}" var="nv">
                            <option value="${nv.id}">${nv.tenNhanVien} - ${nv.vaiTro}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-12 d-grid">
                    <button class="btn btn-primary" type="submit">Dang nhap</button>
                </div>
            </form>

            <div class="mt-4 small text-muted">
                <div><strong>ADMIN:</strong> duoc vao quan ly may, dich vu, su kien, thong ke.</div>
                <div><strong>NHAN_VIEN:</strong> duoc quan ly may va hoa don.</div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

