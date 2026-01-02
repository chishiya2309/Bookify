<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa Danh mục - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Chỉnh sửa Danh mục</h2>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="form-card category-form" novalidate>

        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="id" value="${category.categoryId}"/>

        <div class="form-row">
            <label for="name">Tên danh mục:</label>
            <input id="name" type="text" name="name" value="${category.name}" required
                   maxlength="100"
                   minlength="1"
                   placeholder="Nhập tên danh mục (tối đa 100 ký tự)"/>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Lưu</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/categories">Huỷ</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>