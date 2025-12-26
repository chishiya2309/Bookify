<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý Đánh giá - Admin Bookify</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f4f4f4; }
        .container { max-width: 1400px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #333; }
        .search-filter { background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; display: flex; flex-wrap: wrap; gap: 15px; align-items: end; }
        .search-filter input, .search-filter select, .search-filter button { padding: 10px; font-size: 16px; border-radius: 4px; border: 1px solid #ccc; }
        .search-filter button { background: #007bff; color: white; border: none; cursor: pointer; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background: #007bff; color: white; }
        tr:nth-child(even) { background: #f9f9f9; }
        .action-btn { padding: 6px 12px; margin: 0 4px; border: none; border-radius: 4px; cursor: pointer; color: white; }
        .delete-btn { background: #dc3545; }
        .verify-btn { background: #28a745; }
        .unverify-btn { background: #ffc107; color: black; }
        .pagination { text-align: center; margin: 30px 0; }
        .pagination a { padding: 10px 15px; margin: 0 5px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        .pagination a.disabled { background: #6c757d; pointer-events: none; }
        .pagination .current { padding: 10px 15px; background: #0056b3; color: white; border-radius: 4px; }
        .message { padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>

<%@ include file="/admin/header_admin.jsp" %>

<div class="container">
    <h1>Quản lý Đánh giá Khách hàng (${totalReviews} đánh giá)</h1>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="message success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="message error">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <form method="get" action="${pageContext.request.contextPath}/admin/reviews" class="search-filter">
        <input type="hidden" name="action" value="list"/>
        <div>
            <label>Tên sách</label><br>
            <input type="text" name="bookTitle" value="${bookTitle}" placeholder="Nhập tên sách" style="width:200px;"/>
        </div>
        <div>
            <label>Khách hàng (tên/email)</label><br>
            <input type="text" name="customerSearch" value="${customerSearch}" placeholder="Tên hoặc email" style="width:200px;"/>
        </div>
        <div>
            <label>Rating</label><br>
            <select name="rating">
                <option value="">Tất cả</option>
                <option value="5" ${rating == 5 ? 'selected' : ''}>5 sao</option>
                <option value="4" ${rating == 4 ? 'selected' : ''}>4 sao</option>
                <option value="3" ${rating == 3 ? 'selected' : ''}>3 sao</option>
                <option value="2" ${rating == 2 ? 'selected' : ''}>2 sao</option>
                <option value="1" ${rating == 1 ? 'selected' : ''}>1 sao</option>
            </select>
        </div>
        <div>
            <label>Trạng thái duyệt</label><br>
            <select name="verified">
                <option value="">Tất cả</option>
                <option value="true" ${verified == true ? 'selected' : ''}>Đã duyệt</option>
                <option value="false" ${verified == false ? 'selected' : ''}>Chưa duyệt</option>
            </select>
        </div>
        <button type="submit">Tìm kiếm</button>
    </form>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Sách</th>
            <th>Khách hàng</th>
            <th>Rating</th>
            <th>Tiêu đề</th>
            <th>Nội dung</th>
            <th>Ngày</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${reviewList}" var="r">
            <tr>
                <td>${r.reviewId}</td>
                <td><c:out value="${r.book.title}"/></td>
                <td>
                    <c:out value="${r.customer.fullName}"/><br>
                    <small><c:out value="${r.customer.email}"/></small>
                </td>
                <td>
              <span style="color:#f39c12; font-size:20px;">
                <c:forEach begin="1" end="${r.rating}">★</c:forEach>
              </span>
                </td>
                <td><c:out value="${r.headline}"/></td>
                <td style="max-width:350px; word-wrap:break-word;"><c:out value="${r.comment}"/></td>
                <td>${r.reviewDate}</td>
                <td>
                    <c:choose>
                        <c:when test="${r.verified}">
                            <span style="color:green; font-weight:bold;">Đã duyệt</span>
                        </c:when>
                        <c:otherwise>
                            <span style="color:orange; font-weight:bold;">Chưa duyệt</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <!-- Nút toggle verified -->
                    <form method="post" action="${pageContext.request.contextPath}/admin/reviews" style="display:inline;">
                        <input type="hidden" name="action" value="toggleVerified"/>
                        <input type="hidden" name="reviewId" value="${r.reviewId}"/>
                        <c:if test="${not empty bookTitle}"><input type="hidden" name="bookTitle" value="${bookTitle}"/></c:if>
                        <c:if test="${not empty customerSearch}"><input type="hidden" name="customerSearch" value="${customerSearch}"/></c:if>
                        <c:if test="${not empty rating}"><input type="hidden" name="rating" value="${rating}"/></c:if>
                        <c:if test="${not empty verified}"><input type="hidden" name="verified" value="${verified}"/></c:if>
                        <input type="hidden" name="page" value="${currentPage}"/>
                        <button type="submit" class="action-btn ${r.verified ? 'unverify-btn' : 'verify-btn'}"
                                onclick="return confirm('Bạn có chắc muốn ${r.verified ? 'bỏ duyệt' : 'duyệt'} đánh giá này?')">
                                ${r.verified ? 'Bỏ duyệt' : 'Duyệt'}
                        </button>
                    </form>

                    <!-- Nút xóa -->
                    <form method="post" action="${pageContext.request.contextPath}/admin/reviews" style="display:inline;"
                          onsubmit="return confirm('Xóa đánh giá này? Không thể hoàn tác!');">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="reviewId" value="${r.reviewId}"/>
                        <c:if test="${not empty bookTitle}"><input type="hidden" name="bookTitle" value="${bookTitle}"/></c:if>
                        <c:if test="${not empty customerSearch}"><input type="hidden" name="customerSearch" value="${customerSearch}"/></c:if>
                        <c:if test="${not empty rating}"><input type="hidden" name="rating" value="${rating}"/></c:if>
                        <c:if test="${not empty verified}"><input type="hidden" name="verified" value="${verified}"/></c:if>
                        <input type="hidden" name="page" value="${currentPage}"/>
                        <button type="submit" class="action-btn delete-btn">Xóa</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/admin/reviews?action=list&page=${currentPage - 1}
             <c:if test="${not empty bookTitle}">&bookTitle=${bookTitle}</c:if>
             <c:if test="${not empty customerSearch}">&customerSearch=${customerSearch}</c:if>
             <c:if test="${not empty rating}">&rating=${rating}</c:if>
             <c:if test="${not empty verified}">&verified=${verified}</c:if>">
                    « Trước
                </a>
            </c:if>

            <span class="current">Trang ${currentPage} / ${totalPages}</span>

            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/admin/reviews?action=list&page=${currentPage + 1}
             <c:if test="${not empty bookTitle}">&bookTitle=${bookTitle}</c:if>
             <c:if test="${not empty customerSearch}">&customerSearch=${customerSearch}</c:if>
             <c:if test="${not empty rating}">&rating=${rating}</c:if>
             <c:if test="${not empty verified}">&verified=${verified}</c:if>">
                    Sau »
                </a>
            </c:if>
        </div>
    </c:if>
</div>

</body>
</html>