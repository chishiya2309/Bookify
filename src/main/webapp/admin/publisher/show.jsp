<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Publisher Management - Admin</title>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
</head>
<body>
<h2>Publisher Management</h2>
<a href="${pageContext.request.contextPath}/admin/publishers?action=showCreate">Create New Publisher</a>
<br>
<table>
    <tr>
        <th>ID</th>
        <th>Full Name</th>
        <th>Email</th>
        <th>Address</th>
        <th>Website</th>
        <th>Actions</th>
    </tr>

    <c:if test="${empty listPublishers}">
        <tr>
            <td colspan="6" style="text-align: center;">No data available.</td>
        </tr>
    </c:if>

    <c:forEach var="user" items="${listPublishers}">
        <tr>
            <td>${user.publisherId}</td>
            <td>${user.name}</td>
            <td>${user.contactEmail}</td>
            <td>${user.address}</td>
            <td>${user.website}</td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/publishers?action=showUpdate&id=${user.publisherId}">Edit</a>
                <br>
                <a href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${user.publisherId}"
                   onclick="return confirm('Are you sure you want to delete the user with ID= ${user.publisherId}?')">
                    Delete
                </a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>