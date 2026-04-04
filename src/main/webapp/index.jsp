<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        .may-card { width: 210px; margin: 10px; text-align: center; padding: 16px; border-radius: 10px; }
        .available { background-color: #d4edda; border: 2px solid #28a745; }
        .playing { background-color: #fff3cd; border: 2px solid #ffc107; }
        .broken { background-color: #f8d7da; border: 2px solid #dc3545; }
    </style>
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mt-3">
        <h2 class="m-0">HomePS Machine Dashboard</h2>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/dichvu">Quan ly dich vu</a>
            <a class="btn btn-outline-success" href="${pageContext.request.contextPath}/sukien?action=list">Quan ly su kien</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/thongke">Thong ke</a>
        </div>
    </div>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert-info mt-3" role="alert">${flashMessage}</div>
    </c:if>

    <div class="card mt-3">
        <div class="card-body">
            <h6 class="card-title">Mo hoa don theo luot choi</h6>
            <form action="${pageContext.request.contextPath}/hoadon" method="get" class="row g-2 align-items-end">
                <input type="hidden" name="action" value="view">
                <div class="col-md-3">
                    <label class="form-label">Luot choi ID</label>
                    <input class="form-control" type="number" min="1" name="luotChoiId" required>
                </div>
                <div class="col-md-2">
                    <button class="btn btn-primary w-100" type="submit">Xem hoa don</button>
                </div>
            </form>
        </div>
    </div>

    <div class="d-flex flex-wrap mt-3">
        <c:forEach items="${danhSachMay}" var="m">
            <div class="may-card shadow-sm
                ${(m.tinhTrang == 'BINH_THUONG' || m.tinhTrang == 'TRONG') ? 'available' : ((m.tinhTrang == 'DANG_CHOI' || m.tinhTrang == 'DANG_SU_DUNG') ? 'playing' : 'broken')}">
                <h5>${m.tenMay}</h5>
                <p class="small mb-3">Status: ${m.tinhTrang}</p>

                <c:if test="${m.tinhTrang == 'BINH_THUONG' || m.tinhTrang == 'TRONG'}">
                    <form action="${pageContext.request.contextPath}/home" method="post">
                        <input type="hidden" name="action" value="open">
                        <input type="hidden" name="mayId" value="${m.id}">
                        <button class="btn btn-sm btn-primary" type="submit">Open</button>
                    </form>
                </c:if>

                <c:if test="${m.tinhTrang == 'DANG_CHOI' || m.tinhTrang == 'DANG_SU_DUNG'}">
                    <form action="${pageContext.request.contextPath}/home" method="post">
                        <input type="hidden" name="action" value="close">
                        <input type="hidden" name="mayId" value="${m.id}">
                        <button class="btn btn-sm btn-warning" type="submit">Close & bill</button>
                    </form>
                </c:if>

                <c:if test="${m.tinhTrang != 'BINH_THUONG' && m.tinhTrang != 'TRONG' && m.tinhTrang != 'DANG_CHOI' && m.tinhTrang != 'DANG_SU_DUNG'}">
                    <button class="btn btn-sm btn-secondary" type="button" disabled>Unavailable</button>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>