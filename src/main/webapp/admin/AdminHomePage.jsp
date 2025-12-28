<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang quản trị - Bookify</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp"></jsp:include>

    <div align="center" class="container">

        <h2 class="page-title">Bảng điều khiển quản trị</h2>
        
        <div class="quick-actions" style="margin-bottom: 20px;">
            <h3>Thao tác nhanh:</h3>
            <a href="${pageContext.request.contextPath}/admin/books?action=showCreate">Thêm sách mới</a> &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/admin/categories?action=showCreate">Thêm danh mục</a> &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/admin/customers?action=create">Thêm khách hàng</a> &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/admin/publishers?action=showCreate">Thêm nhà xuất bản</a> &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/admin/vouchers?action=showCreate">Thêm voucher</a>
        </div>
        
        <hr width="60%">


        <h3>Đơn hàng gần đây:</h3>
        <table border="1" cellpadding="5">
            <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Số sách</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th>Trạng thái</th>
                <th>Ngày đặt</th>
            </tr>
            
            <c:forEach items="${listMostRecentSales}" var="order">
            <tr>
                <td><a href="view_order?id=${order.orderId}">${order.orderId}</a></td>
                <td><c:out value="${order.customer.fullName}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.orderDetails}">
                            ${order.orderDetails.size()}
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </td>
                <td>    
                    <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                </td>
                <td>${order.paymentMethod}</td>
                <td>${order.orderStatus}</td>
                <td>
                     ${order.orderDate}
                </td>
            </tr>
            </c:forEach>
            <c:if test="${empty listMostRecentSales}">
                <tr><td colspan="7" align="center">Chưa có đơn hàng nào.</td></tr>
            </c:if>
        </table>

        <br>
        <h3>Đánh giá gần đây:</h3>
        <table border="1" cellpadding="5">
            <tr>
                <th>Sách</th>
                <th>Điểm</th>
                <th>Tiêu đề</th>
                <th>Khách hàng</th>
                <th>Ngày đánh giá</th>
            </tr>
            
            <c:forEach items="${listMostRecentReviews}" var="review">
            <tr>
                <td>${review.book.title}</td>
                <td>${review.rating}</td>
                <td><a href="edit_review?id=${review.reviewId}">${review.headline}</a></td>
                <td><c:out value="${review.customer.fullName}"/></td>
                <td>
                    ${review.reviewDate}
                </td>
            </tr>
            </c:forEach>

            <c:if test="${empty listMostRecentReviews}">
                <tr><td colspan="5" align="center">Chưa có đánh giá nào.</td></tr>
            </c:if>
        </table>

        <div class="statistics" style="margin-top: 30px; padding: 10px; font-weight: bold;">
            <h3>Thống kê:</h3>
            Tổng người dùng: ${totalUsers} &nbsp;&nbsp;
            Tổng số sách: ${totalBooks} &nbsp;&nbsp;
            Tổng khách hàng: ${totalCustomers} &nbsp;&nbsp;
            Tổng đánh giá: ${totalReviews} &nbsp;&nbsp;
            Tổng đơn hàng: ${totalOrders}
        </div>
    </div>

    <jsp:include page="/admin/footer_admin.jsp"></jsp:include>
</body>
</html>