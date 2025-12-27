<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Books Management - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Books Management</h2>

    <div class="actions-bar">
        <a href="${pageContext.request.contextPath}/admin/books?action=showCreate" class="btn-create">
            + Create New Book
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Index</th>
            <th>ID</th>
            <th>Image</th>
            <th>Title</th>
            <th>Author</th>
            <th>Category</th>
            <th>Publisher</th>
            <th>Price</th>
            <th>Last Updated</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty books}">
            <tr>
                <td colspan="10" style="text-align: center;">No data available.</td>
            </tr>
        </c:if>

        <c:forEach var="book" items="${books}" varStatus="status">
            <tr>
                <td>${status.index + 1}</td>
                <td>${book.bookId}</td>
                <td>
                    <c:if test="${not empty book.images}">
                        <c:forEach var="img" items="${book.images}">
                            <c:if test="${img.isPrimary}">
                                <img src="${img.url}" alt="${book.title}" style="width: 50px; height: 70px; object-fit: cover;">
                            </c:if>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty book.images}">
                        <span style="color: #999;">No image</span>
                    </c:if>
                </td>
                <td>${book.title}</td>
                <td>
                    <c:if test="${not empty book.authors}">
                        <c:forEach var="author" items="${book.authors}" varStatus="authorStatus">
                            ${author.name}<c:if test="${!authorStatus.last}">, </c:if>
                        </c:forEach>
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty book.category}">
                        ${book.category.name}
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty book.publisher}">
                        ${book.publisher.name}
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty book.price}">
                        ${book.price} VND
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
                    <div class="action-buttons">
                        <a class="btn-action edit" href="${pageContext.request.contextPath}/admin/books?action=showUpdate&bookId=${book.bookId}">Edit</a>
                        <a class="btn-action delete" href="${pageContext.request.contextPath}/admin/books?action=delete&bookId=${book.bookId}"
                           data-id="${book.bookId}">
                            Delete
                        </a>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div id="confirmModal" class="modal-backdrop">
        <div class="modal-box">
            <h3>Confirm Delete</h3>
            <p id="confirmText">Are you sure?</p>
            <div class="modal-actions">
                <button type="button" class="modal-btn cancel" id="btnCancel">Cancel</button>
                <button type="button" class="modal-btn confirm" id="btnConfirm">Delete</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer_admin.jsp" />

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>const contextPath = '${pageContext.request.contextPath}';</script>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
