<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Category Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>
<jsp:include page="../header_admin.jsp"/>

<main class="admin-content">
    <section class="page-main">
        <div class="page-header">
            <h2>Category Management</h2>
            <a class="btn" href="${pageContext.request.contextPath}/admin/categories?action=showCreate">Create New Category</a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success"><c:out value="${message}"/></div>
        </c:if>

        <table class="admin-table">
            <thead>
            <tr>
                <th style="width:70px;">Index</th>
                <th style="width:70px;">ID</th>
                <th>Category Name</th>
                <th style="width:200px;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${empty categories}">
                <tr>
                    <td colspan="4" style="text-align:center;">No categories found.</td>
                </tr>
            </c:if>
            <c:forEach var="category" items="${categories}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td>${category.categoryId}</td>
                    <td><c:out value="${category.name}"/></td>
                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/admin/categories?action=showUpdate&id=${category.categoryId}">Edit</a>
                        |
                        <form id="deleteForm-${category.categoryId}" action="${pageContext.request.contextPath}/admin/categories" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="id" value="${category.categoryId}"/>
                            <button type="button" class="link-button" onclick="openDeleteModal('${category.categoryId}');">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
</main>

<div id="deleteModal" class="modal-backdrop" role="dialog" aria-hidden="true">
    <div class="modal" role="document">
        <div class="modal-body">
            <p id="deleteModalText">Are you sure you want to delete the category with ID <span id="deleteId"></span>?</p>
        </div>
        <div class="modal-actions">
            <button class="btn" type="button" onclick="closeDeleteModal();">Cancel</button>
            <button class="btn" type="button" onclick="confirmDelete();">OK</button>
        </div>
    </div>
</div>

<script>
    let pendingDeleteId = null;
    function openDeleteModal(id) {
        pendingDeleteId = id;
        document.getElementById('deleteId').textContent = id;
        const backdrop = document.getElementById('deleteModal');
        backdrop.style.display = 'flex';
        backdrop.setAttribute('aria-hidden', 'false');
    }
    function closeDeleteModal() {
        pendingDeleteId = null;
        const backdrop = document.getElementById('deleteModal');
        backdrop.style.display = 'none';
        backdrop.setAttribute('aria-hidden', 'true');
    }
    function confirmDelete() {
        if (!pendingDeleteId) return closeDeleteModal();
        const form = document.getElementById('deleteForm-' + pendingDeleteId);
        if (form) form.submit();
        else closeDeleteModal();
    }
    document.getElementById('deleteModal').addEventListener('click', function (e) {
        if (e.target === this) closeDeleteModal();
    });
</script>

<jsp:include page="../footer_admin.jsp"/>
</body>
</html>
>>>>>>> main
