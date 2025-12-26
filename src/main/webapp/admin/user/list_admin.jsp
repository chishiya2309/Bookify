<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Admin - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" type="text/css"/>
</head>
<body>
    <%@ include file="../header_admin.jsp" %>
    
    <main class="admin-content">
        <header>
            <h2 class="page-title">Quản lý Admin</h2>
        </header>
        
        <c:if test="${not empty success}">
            <aside class="alert alert-success" role="alert">${success}</aside>
        </c:if>
        
        <c:if test="${not empty error}">
            <aside class="alert alert-danger" role="alert">${error}</aside>
        </c:if>
        
        <nav class="toolbar" aria-label="Công cụ tìm kiếm và thêm mới">
            <form class="search-form" action="${pageContext.request.contextPath}/admin/user" method="get" role="search">
                <input type="hidden" name="action" value="list">
                <input type="text" name="search" placeholder="Tìm kiếm theo email hoặc tên..." value="${search}">
                <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                <c:if test="${not empty search}">
                    <a href="${pageContext.request.contextPath}/admin/user?action=list" class="btn btn-secondary">Xóa bộ lọc</a>
                </c:if>
            </form>
            <a href="${pageContext.request.contextPath}/admin/user?action=create" class="btn btn-success">+ Thêm Admin</a>
        </nav>
        
        <section class="table-container">
        <table>
            <thead>
                <tr>
                    <th style="width: 80px;">ID</th>
                    <th>Email</th>
                    <th>Họ và Tên</th>
                    <th style="width: 150px;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty adminList}">
                    <tr>
                        <td colspan="4" class="empty-message">
                            <c:choose>
                                <c:when test="${not empty search}">
                                    Không tìm thấy admin nào với từ khóa "${search}"
                                </c:when>
                                <c:otherwise>
                                    Chưa có admin nào trong hệ thống
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:if>
                
                <c:forEach var="admin" items="${adminList}">
                    <tr>
                        <td>${admin.userId}</td>
                        <td style="text-align: left;">${admin.email}</td>
                        <td style="text-align: left;">${admin.fullName}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/user?action=edit&id=${admin.userId}" 
                               class="btn btn-primary btn-sm">Sửa</a>
                            <button type="button" class="btn btn-danger btn-sm"
                                    onclick="confirmDelete(${admin.userId}, '${admin.email}')">Xóa</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        </section>

        <c:if test="${totalPages > 1}">
            <c:set var="searchParam" value="${not empty search ? '&search='.concat(search) : ''}" />
            <nav class="pagination" aria-label="Phân trang">
                <c:choose>
                    <c:when test="${hasPrevious}">
                        <a href="${pageContext.request.contextPath}/admin/user?action=list&page=${currentPage - 1}${searchParam}" 
                           class="btn btn-secondary btn-sm">« Trước</a>
                    </c:when>
                    <c:otherwise>
                        <span class="btn btn-secondary btn-sm btn-disabled">« Trước</span>
                    </c:otherwise>
                </c:choose>
                
                <span class="page-numbers">
                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                        <c:choose>
                            <c:when test="${pageNum == currentPage}">
                                <span class="btn btn-primary btn-sm" aria-current="page">${pageNum}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/admin/user?action=list&page=${pageNum}${searchParam}" 
                                   class="btn btn-secondary btn-sm">${pageNum}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </span>
                
                <c:choose>
                    <c:when test="${hasNext}">
                        <a href="${pageContext.request.contextPath}/admin/user?action=list&page=${currentPage + 1}${searchParam}" 
                           class="btn btn-secondary btn-sm">Sau »</a>
                    </c:when>
                    <c:otherwise>
                        <span class="btn btn-secondary btn-sm btn-disabled">Sau »</span>
                    </c:otherwise>
                </c:choose>
            </nav>
            <p class="pagination-info">Trang ${currentPage} / ${totalPages} (Tổng: ${totalItems} admin)</p>
        </c:if>
        
        <c:if test="${totalPages <= 1 && not empty adminList}">
            <p class="pagination-info">Tổng: ${totalItems} admin</p>
        </c:if>
    </main>
    
    <%@ include file="../footer_admin.jsp" %>
    
    <!-- Delete Confirmation Form (Hidden) -->
    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/user" style="display: none;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" id="deleteId">
    </form>
    
    <script>
        function confirmDelete(adminId, adminEmail) {
            if (confirm('Bạn có chắc muốn xóa admin này?\n\nEmail: ' + adminEmail)) {
                document.getElementById('deleteId').value = adminId;
                document.getElementById('deleteForm').submit();
            }
        }
    </script>
</body>
</html>
