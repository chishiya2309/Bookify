<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create New Category</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>
<jsp:include page="../header_admin.jsp"/>
<main class="admin-content">
    <section class="page-main">
        <h2 style="text-align:center;">Create New Category</h2>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="form-card">
            <input type="hidden" name="action" value="create"/>

            <div>
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" required maxlength="100" placeholder="Category name"/>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn">Save</button>
                <a class="btn-secondary" href="${pageContext.request.contextPath}/admin/categories">Cancel</a>
            </div>
        </form>
    </section>
</main>
<jsp:include page="../footer_admin.jsp"/>
</body>
</html>
