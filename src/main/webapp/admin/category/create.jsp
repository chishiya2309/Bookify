<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Danh mục mới</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Thêm Danh mục mới</h2>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/categories?action=create" method="post" class="form-card category-form" novalidate>

        <div class="form-row">
            <label for="name">Tên danh mục:</label>
            <input id="name" type="text" name="name" required
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