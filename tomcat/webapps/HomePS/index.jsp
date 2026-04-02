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
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/dichvu">Quan ly dich vu</a>
    </div>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert-info mt-3" role="alert">${flashMessage}</div>
    </c:if>

    <div class="d-flex flex-wrap mt-3">
        <c:forEach items="${danhSachMay}" var="m">
            <div class="may-card shadow-sm
                ${m.tinhTrang == 'BINH_THUONG' ? 'available' : (m.tinhTrang == 'DANG_CHOI' ? 'playing' : 'broken')}">
                <h5>${m.tenMay}</h5>
                <p class="small mb-3">Status: ${m.tinhTrang}</p>

                <c:if test="${m.tinhTrang == 'BINH_THUONG'}">
                    <form action="${pageContext.request.contextPath}/home" method="post">
                        <input type="hidden" name="action" value="open">
                        <input type="hidden" name="mayId" value="${m.id}">
                        <button class="btn btn-sm btn-primary" type="submit">Open</button>
                    </form>
                </c:if>

                <c:if test="${m.tinhTrang == 'DANG_CHOI'}">
                    <form action="${pageContext.request.contextPath}/home" method="post">
                        <input type="hidden" name="action" value="close">
                        <input type="hidden" name="mayId" value="${m.id}">
                        <button class="btn btn-sm btn-warning" type="submit">Close & bill</button>
                    </form>
                </c:if>

                <c:if test="${m.tinhTrang != 'BINH_THUONG' && m.tinhTrang != 'DANG_CHOI'}">
                    <button class="btn btn-sm btn-secondary" type="button" disabled>Unavailable</button>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>