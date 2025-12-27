<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Khách hàng - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" type="text/css"/>
</head>
<body>
    <%@ include file="../header_admin.jsp" %>
    
    <main class="admin-content">
        <header>
            <h2 class="page-title">Quản lý Khách hàng</h2>
        </header>
        
        <c:if test="${not empty success}">
            <aside class="alert alert-success" role="alert">${success}</aside>
        </c:if>
        
        <c:if test="${not empty error}">
            <aside class="alert alert-danger" role="alert">${error}</aside>
        </c:if>
        
        <nav class="toolbar" aria-label="Công cụ tìm kiếm và thêm mới">
            <form class="search-form" action="${pageContext.request.contextPath}/admin/customers" method="get" role="search">
                <input type="hidden" name="action" value="list">
                <input type="text" name="search" placeholder="Tìm kiếm theo email, tên hoặc số điện thoại..." value="${search}">
                <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                <c:if test="${not empty search}">
                    <a href="${pageContext.request.contextPath}/admin/customers?action=list" class="btn btn-secondary">Xóa bộ lọc</a>
                </c:if>
            </form>
            <a href="${pageContext.request.contextPath}/admin/customers?action=create" class="btn btn-success">+ Thêm Khách hàng</a>
        </nav>
        
        <section class="table-container">
        <table>
            <thead>
                <tr>
                    <th style="width: 50px;">STT</th>
                    <th style="width: 60px;">ID</th>
                    <th>Email</th>
                    <th>Họ và Tên</th>
                    <th>Số điện thoại</th>
                    <th style="width: 120px;">Ngày đăng ký</th>
                    <th style="width: 150px;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty customerList}">
                    <tr>
                        <td colspan="7" class="empty-message">
                            <c:choose>
                                <c:when test="${not empty search}">Không tìm thấy khách hàng nào với từ khóa "${search}"</c:when>
                                <c:otherwise>Chưa có khách hàng nào trong hệ thống</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:if>
                
                <c:set var="dateFormatter" value='<%= java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy") %>' />
                <c:forEach var="customer" items="${customerList}" varStatus="loop">
                    <tr>
                        <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                        <td>${customer.userId}</td>
                        <td style="text-align: left;"><c:out value="${customer.email}"/></td>
                        <td style="text-align: left;"><c:out value="${customer.fullName}"/></td>
                        <td><c:out value="${customer.phoneNumber}"/></td>
                        <td>
                            ${customer.registerDate != null ? customer.registerDate.format(dateFormatter) : 'N/A'}
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/customers?action=edit&id=${customer.userId}" 
                               class="btn btn-primary btn-sm">Sửa</a>
                            <button type="button" class="btn btn-danger btn-sm"
                                    onclick="confirmDelete(${customer.userId}, '${customer.email}')">Xóa</button>
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
                        <a href="${pageContext.request.contextPath}/admin/customers?action=list&page=${currentPage - 1}${searchParam}" class="btn btn-secondary btn-sm">« Trước</a>
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
                                <a href="${pageContext.request.contextPath}/admin/customers?action=list&page=${pageNum}${searchParam}" class="btn btn-secondary btn-sm">${pageNum}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </span>
                
                <c:choose>
                    <c:when test="${hasNext}">
                        <a href="${pageContext.request.contextPath}/admin/customers?action=list&page=${currentPage + 1}${searchParam}" class="btn btn-secondary btn-sm">Sau »</a>
                    </c:when>
                    <c:otherwise>
                        <span class="btn btn-secondary btn-sm btn-disabled">Sau »</span>
                    </c:otherwise>
                </c:choose>
            </nav>
            <p class="pagination-info">Trang ${currentPage} / ${totalPages} (Tổng: ${totalItems} khách hàng)</p>
        </c:if>
        
        <c:if test="${totalPages <= 1 && not empty customerList}">
            <p class="pagination-info">Tổng: ${totalItems} khách hàng</p>
        </c:if>
    </main>
    
    <%@ include file="../footer_admin.jsp" %>
    
    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/customers" style="display: none;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="id" id="deleteId">
    </form>
    
    <script>
        function confirmDelete(customerId, customerEmail) {
            if (confirm('Bạn có chắc muốn xóa khách hàng này?\n\nEmail: ' + customerEmail)) {
                document.getElementById('deleteId').value = customerId;
                document.getElementById('deleteForm').submit();
            }
        }
    </script>
</body>
</html>
