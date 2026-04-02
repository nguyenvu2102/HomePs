<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePS - Dich vu</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
<div class="container py-3">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="m-0">Quan ly dich vu</h2>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/home">Back to dashboard</a>
    </div>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert-info" role="alert">${flashMessage}</div>
    </c:if>

    <c:set var="isEdit" value="${not empty editDichVu}" />
    <div class="card mb-4">
        <div class="card-body">
            <h5>${isEdit ? 'Cap nhat dich vu' : 'Them dich vu moi'}</h5>
            <form action="${pageContext.request.contextPath}/dichvu" method="post" class="row g-2 mt-1">
                <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${editDichVu.id}">
                </c:if>

                <div class="col-md-4">
                    <label class="form-label">Ten dich vu</label>
                    <input class="form-control" name="tenDichVu" value="${isEdit ? editDichVu.tenDichVu : ''}" required>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Don gia</label>
                    <input class="form-control" name="donGia" type="number" min="0" step="1000"
                           value="${isEdit ? editDichVu.donGia : ''}" required>
                </div>

                <div class="col-md-3">
                    <label class="form-label">Loai</label>
                    <select class="form-select" name="loai">
                        <option value="DO_AN" ${isEdit && editDichVu.loai == 'DO_AN' ? 'selected' : ''}>DO_AN</option>
                        <option value="NUOC" ${isEdit && editDichVu.loai == 'NUOC' ? 'selected' : ''}>NUOC</option>
                        <option value="KHAC" ${isEdit && editDichVu.loai == 'KHAC' ? 'selected' : ''}>KHAC</option>
                    </select>
                </div>

                <div class="col-md-2 d-flex align-items-end">
                    <button class="btn btn-primary w-100" type="submit">${isEdit ? 'Update' : 'Create'}</button>
                </div>
            </form>

            <c:if test="${isEdit}">
                <a class="btn btn-link p-0 mt-2" href="${pageContext.request.contextPath}/dichvu">Bo chinh sua</a>
            </c:if>
        </div>
    </div>

    <table class="table table-bordered table-hover">
        <thead class="table-light">
        <tr>
            <th style="width: 70px">ID</th>
            <th>Ten dich vu</th>
            <th style="width: 130px">Don gia</th>
            <th style="width: 120px">Loai</th>
            <th style="width: 180px">Thao tac</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${danhSachDichVu}" var="dv">
            <tr>
                <td>${dv.id}</td>
                <td>${dv.tenDichVu}</td>
                <td>${dv.donGia}</td>
                <td>${dv.loai}</td>
                <td>
                    <a class="btn btn-sm btn-warning" href="${pageContext.request.contextPath}/dichvu?editId=${dv.id}">Edit</a>
                    <form action="${pageContext.request.contextPath}/dichvu" method="post" style="display:inline-block"
                          onsubmit="return confirm('Delete this service?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${dv.id}">
                        <button class="btn btn-sm btn-danger" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>

