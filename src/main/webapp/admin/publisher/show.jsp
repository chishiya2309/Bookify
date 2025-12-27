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

    <c:forEach var="publisher" items="${listPublishers}">
        <tr>
            <td>${publisher.publisherId}</td>
            <td><c:out value="${publisher.name}"/></td>
            <td><c:out value="${publisher.contactEmail}"/></td>
            <td><c:out value="${publisher.address}"/></td>
            <td><c:out value="${publisher.website}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/publishers?action=showUpdate&id=${publisher.publisherId}">Edit</a>
                <br>
                <a href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${publisher.publisherId}"
                   onclick="return confirm('Are you sure you want to delete the user with ID= ${publisher.publisherId}?')">
                    Delete
                </a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>