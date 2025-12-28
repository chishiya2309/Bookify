<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Category Management - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Edit Category</h2>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="form-card category-form" novalidate>

        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="id" value="${category.categoryId}"/>

        <div class="form-row">
            <label for="name">Category Name:</label>
            <input id="name" type="text" name="name" value="${category.name}" required
                   maxlength="100"
                   minlength="1"
                   placeholder="Enter category name (max 100 characters)"/>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Save</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/categories">Cancel</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>