<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Category</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>
    
<jsp:include page="../header_admin.jsp"/>

<main class="admin-content">
    
    <div class="page-header">
        <h2>Edit Category</h2>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" style="text-align: center; color: red;"><c:out value="${errorMessage}"/></div>
    </c:if>

    <c:if test="${not empty category}">
        <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="form-card">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" value="${category.categoryId}"/>

            <div>
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" value="<c:out value='${category.name}'/>" required maxlength="100"/>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn">Save</button>
                <a class="btn-secondary" href="${pageContext.request.contextPath}/admin/categories">Cancel</a>
            </div>
        </form>
    </c:if>

    <c:if test="${empty category}">
        <div style="text-align: center; margin-top: 20px;">
            <p>Category not found.</p>
            <a href="${pageContext.request.contextPath}/admin/categories">Back to list</a>
        </div>
    </c:if>
    
</main>

<jsp:include page="../footer_admin.jsp"/>
</body>
</html>