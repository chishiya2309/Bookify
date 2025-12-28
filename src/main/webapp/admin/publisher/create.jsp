<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Publisher</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Create New Publisher</h2>

    <!-- Display error message if exists -->
    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/publishers?action=create" method="post" class="form-card publisher-form" novalidate>

        <div class="form-row">
            <label for="name">Publisher Name:</label>
            <input id="name" type="text" name="name" required
                   maxlength="255"
                   minlength="1"
                   placeholder="Enter publisher name (max 255 characters)"/>
        </div>

        <div class="form-row">
            <label for="contactEmail">Contact Email:</label>
            <input id="contactEmail" type="email" name="contactEmail" required
                   maxlength="100"
                   pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
                   placeholder="example@mail.com"
                   title="Email must be valid format"/>
        </div>

        <div class="form-row">
            <label for="address">Address:</label>
            <input id="address" type="text" name="address"
                   maxlength="500"
                   placeholder="Enter address (optional, max 500 characters)"/>
        </div>

        <div class="form-row">
            <label for="website">Website:</label>
            <input id="website" type="text" name="website"
                   maxlength="255"
                   pattern="^(https?://)?[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}.*$"
                   placeholder="https://www.example.com"
                   title="Website must be valid URL format"/>
        </div>

        <div class="buttons">
            <button type="submit" class="btn">Save</button>
            <a class="btn" href="${pageContext.request.contextPath}/admin/publishers">Cancel</a>
        </div>

    </form>
</div>

<jsp:include page="../footer_admin.jsp" />
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>