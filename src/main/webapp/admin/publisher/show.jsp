<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Publisher Management - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Publisher Management</h2>

    <div class="actions-bar">
        <a href="${pageContext.request.contextPath}/admin/publishers?action=showCreate" class="btn-create">
            + Create New Publisher
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Full Name</th>
            <th>Email</th>
            <th>Address</th>
            <th>Website</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty listPublishers}">
            <tr>
                <td colspan="6" style="text-align: center;">No data available.</td>
            </tr>
        </c:if>

        <c:forEach var="publisher" items="${listPublishers}">
            <tr>
                <td>${publisher.publisherId}</td>
                <td>${publisher.name}</td>
                <td>${publisher.contactEmail}</td>
                <td>${publisher.address}</td>
                <td>
                    <a href="${publisher.website}" target="_blank">${publisher.website}</a>
                </td>
                <td>
                    <div class="action-buttons">
                        <a class="btn-action edit" href="${pageContext.request.contextPath}/admin/publishers?action=showUpdate&id=${publisher.publisherId}">Edit</a>
                        <a class="btn-action delete" href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${publisher.publisherId}"
                           data-id="${publisher.publisherId}">
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
