<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Books Management - Admin</title>
    <link rel="stylesheet" href="../../css/styles.css" type="text/css"/>
</head>
<body>
<h2>Books Management</h2>
<a href="${pageContext.request.contextPath}/admin/books?action=showCreate">Create New Book</a>
<br>
<table>
    <tr>
        <th>Index</th>
        <th>ID</th>
        <th>Title</th>
        <th>Author</th>
        <th>Category</th>
        <th>Price</th>
        <th>Last Updated</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="book" items="${books}" varStatus="status">
        <tr>
            <td>${status.index + 1}</td>
            <td>${book.bookId}</td>
            <td>${book.title}</td>
            <td>
                <c:if test="${not empty book.authors}">
                    <c:forEach var="author" items="${book.authors}" varStatus="authorStatus">
                        ${author.name}
                        <c:if test="${!authorStatus.last}">, </c:if>
                    </c:forEach>
                </c:if>
            </td>
            <td>
                <c:if test="${not empty book.category}">
                    ${book.category.name}
                </c:if>
            </td>
            <td>
                <c:if test="${not empty book.price}">
                    $${book.price}
                </c:if>
                <c:if test="${empty book.price}">
                    N/A
                </c:if>
            </td>
            <td>
                <c:if test="${not empty book.lastUpdated}">
                    ${book.lastUpdated}
                </c:if>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/books?action=showUpdate&bookId=${book.bookId}">Edit</a>
                <br>
                <a href="${pageContext.request.contextPath}/admin/books?action=delete&bookId=${book.bookId}"
                                 onclick="return confirm('Are you sure you want to delete the book with ID= ${book.bookId}?')">
                Delete
            </a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
