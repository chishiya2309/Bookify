<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Sách - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" type="text/css"/>
</head>
<body>
<jsp:include page="../header_admin.jsp" />

<div class="container">
    <h2>Quản lý Sách</h2>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>
    
    <c:if test="${not empty message}">
        <div class="success-banner" style="background: #d4edda; color: #155724; padding: 15px; border-radius: 4px; margin-bottom: 15px;">${message}</div>
    </c:if>

    <div class="actions-bar">
        <a href="${pageContext.request.contextPath}/admin/books?action=showCreate" class="btn-create">
            + Thêm sách
        </a>
        <span style="margin-left: auto; color: #666;">Tổng cộng: ${totalBooks} sách</span>
    </div>

    <table>
        <thead>
        <tr>
            <th>STT</th>
            <th>ID</th>
            <th>Hình ảnh</th>
            <th>Tiêu đề</th>
            <th>Tác giả</th>
            <th>Danh mục</th>
            <th>NXB</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Cập nhật</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${empty books}">
            <tr>
                <td colspan="11" style="text-align: center;">Không có dữ liệu.</td>
            </tr>
        </c:if>

        <c:forEach var="book" items="${books}" varStatus="status">
            <tr>
                <td>${currentPage * pageSize + status.index + 1}</td>
                <td>${book.bookId}</td>
                <td>
                    <c:if test="${not empty book.images}">
                        <img src="${book.images[0].url}" alt="${book.title}" style="width: 50px; height: 70px; object-fit: cover;">
                    </c:if>
                    <c:if test="${empty book.images}">
                        <span style="color: #999;">Không có ảnh</span>
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
                <td>${book.quantityInStock}</td>
                <td>
                    <c:if test="${not empty book.lastUpdated}">
                        ${book.lastUpdated}
                    </c:if>
                </td>
                <td>
                    <div class="action-buttons">
                        <a class="btn-action edit" href="${pageContext.request.contextPath}/admin/books?action=showUpdate&bookId=${book.bookId}">Sửa</a>
                        <a class="btn-action delete" href="${pageContext.request.contextPath}/admin/books?action=delete&bookId=${book.bookId}"
                           data-id="${book.bookId}">
                            Xoá
                        </a>
                    </div>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <!-- Phân trang -->
    <c:if test="${totalPages > 1}">
        <div class="pagination" style="display: flex; justify-content: center; align-items: center; gap: 8px; margin: 20px 0; flex-wrap: wrap;">
            <c:if test="${currentPage > 0}">
                <a href="${pageContext.request.contextPath}/admin/books?page=0&size=${pageSize}" 
                   class="btn" style="padding: 8px 12px;">Đầu</a>
                <a href="${pageContext.request.contextPath}/admin/books?page=${currentPage - 1}&size=${pageSize}" 
                   class="btn" style="padding: 8px 12px;">« Trước</a>
            </c:if>
            
            <c:forEach begin="${currentPage > 2 ? currentPage - 2 : 0}" 
                       end="${currentPage + 2 < totalPages - 1 ? currentPage + 2 : totalPages - 1}" 
                       var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span class="btn" style="padding: 8px 12px; background: #007bff; color: white;">${i + 1}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/admin/books?page=${i}&size=${pageSize}" 
                           class="btn" style="padding: 8px 12px;">${i + 1}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            
            <c:if test="${currentPage < totalPages - 1}">
                <a href="${pageContext.request.contextPath}/admin/books?page=${currentPage + 1}&size=${pageSize}" 
                   class="btn" style="padding: 8px 12px;">Sau »</a>
                <a href="${pageContext.request.contextPath}/admin/books?page=${totalPages - 1}&size=${pageSize}" 
                   class="btn" style="padding: 8px 12px;">Cuối</a>
            </c:if>
        </div>
        
        <div style="text-align: center; color: #666; margin-bottom: 20px;">
            Trang ${currentPage + 1} / ${totalPages}
        </div>
    </c:if>

    <div id="confirmModal" class="modal-backdrop">
        <div class="modal-box">
            <h3>Xác nhận xoá</h3>
            <p id="confirmText">Bạn có chắc chắn muốn xoá?</p>
            <div class="modal-actions">
                <button type="button" class="modal-btn cancel" id="btnCancel">Huỷ</button>
                <button type="button" class="modal-btn confirm" id="btnConfirm">Xoá</button>
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
