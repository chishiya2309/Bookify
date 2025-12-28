<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa Nhà xuất bản - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Chỉnh sửa Nhà xuất bản</h2>

    <!-- Hiển thị thông báo lỗi nếu có -->
    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/publishers" method="post" class="form-card publisher-form" novalidate>
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="id" value="${publisher.publisherId}"/>

        <div class="form-row">
            <label for="name">Tên NXB:</label>
            <input id="name" type="text" name="name" value="${publisher.name}" required
                   maxlength="255"
                   minlength="1"
                   placeholder="Nhập tên nhà xuất bản (tối đa 255 ký tự)"/>
        </div>

        <div class="form-row">
            <label for="contactEmail">Email liên hệ:</label>
            <input id="contactEmail" type="email" name="contactEmail" value="${publisher.contactEmail}" required
                   maxlength="100"
                   pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
                   placeholder="example@mail.com"
                   title="Email phải đúng định dạng"/>
        </div>

        <div class="form-row">
            <label for="address">Địa chỉ:</label>
            <input id="address" type="text" name="address" value="${publisher.address}"
                   maxlength="500"
                   placeholder="Nhập địa chỉ (tuỳ chọn, tối đa 500 ký tự)"/>
        </div>

        <div class="form-row">
            <label for="website">Website:</label>
            <input id="website" type="text" name="website" value="${publisher.website}"
                   maxlength="255"
                   pattern="^(https?://)?[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*$"
                   placeholder="https://www.example.com"
                   title="Website phải đúng định dạng URL"/>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Lưu</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/publishers">Huỷ</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>