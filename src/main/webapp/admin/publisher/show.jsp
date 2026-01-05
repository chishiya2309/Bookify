<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Nhà xuất bản - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Quản lý Nhà xuất bản</h2>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>
    
    <c:if test="${not empty message}">
        <div class="success-banner" style="background: #d4edda; color: #155724; padding: 15px; border-radius: 4px; margin-bottom: 15px;">${message}</div>
    </c:if>

    <div class="actions-bar">
        <a href="${pageContext.request.contextPath}/admin/publishers?action=showCreate" class="btn-create">
            + Thêm NXB
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Email</th>
            <th>Địa chỉ</th>
            <th>Website</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty listPublishers}">
            <tr>
                <td colspan="6" style="text-align: center;">Không có dữ liệu.</td>
            </tr>
        </c:if>

        <c:forEach var="publisher" items="${listPublishers}">
            <tr>
                <td>${publisher.publisherId}</td>
                <td>${publisher.name}</td>
                <td>${publisher.contactEmail}</td>
                <td>${publisher.address}</td>
                <td>
                    <a href="${publisher.website}" target="_blank">${publisher.website}</a>
                </td>
                <td>
                    <div class="action-buttons">
                        <a class="btn-action edit" href="${pageContext.request.contextPath}/admin/publishers?action=showUpdate&id=${publisher.publisherId}">Sửa</a>
                        <a class="btn-action delete" href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${publisher.publisherId}"
                           data-id="${publisher.publisherId}">
                            Xoá
                        </a>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div id="confirmModal" class="modal-backdrop">
        <div class="modal-box">
            <h3>Xác nhận xoá</h3>
            <p id="confirmText">Bạn có chắc chắn muốn xoá?</p>
            <div class="modal-actions">
                <button type="button" class="modal-btn cancel" id="btnCancel">Huỷ</button>
                <button type="button" class="modal-btn confirm" id="btnConfirm">Xoá</button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../footer_admin.jsp" />
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>const contextPath = '${pageContext.request.contextPath}';</script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>