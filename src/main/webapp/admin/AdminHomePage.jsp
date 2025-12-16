<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp"></jsp:include>

    <div align="center" class="container">
        <h2 class="page-title">Administrative Dashboard</h2>
        
        <div class="quick-actions" style="margin-bottom: 20px;">
            <h3>Quick Actions:</h3>
            <a href="new_book">New Book</a> &nbsp;|&nbsp;
            <a href="create_user">New User</a> &nbsp;|&nbsp;
            <a href="create_category">New Category</a> &nbsp;|&nbsp;
            <a href="create_customer">New Customer</a>
        </div>
        
        <hr width="60%">

        <h3>Recent Sales:</h3>
        <table border="1" cellpadding="5">
            <tr>
                <th>Order ID</th>
                <th>Ordered by</th>
                <th>Book Copies</th>
                <th>Total</th>
                <th>Payment Method</th>
                <th>Status</th>
                <th>Order Date</th>
            </tr>
            
            <c:forEach items="${listMostRecentSales}" var="order">
            <tr>
                <td><a href="view_order?id=${order.orderId}">${order.orderId}</a></td>
                <td>${order.customer.fullName}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.orderDetails}">
                            ${order.orderDetails.size()}
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="$"/>
                </td>
                <td>${order.paymentMethod}</td>
                <td>${order.orderStatus}</td>
                <td>
                     ${order.orderDate}
                </td>
            </tr>
            </c:forEach>
            <c:if test="${empty listMostRecentSales}">
                <tr><td colspan="7" align="center">No recent sales found.</td></tr>
            </c:if>
        </table>

        <br>

        <h3>Recent Reviews:</h3>
        <table border="1" cellpadding="5">
            <tr>
                <th>Book</th>
                <th>Rating</th>
                <th>Headline</th>
                <th>Customer</th>
                <th>Review On</th>
            </tr>
            
            <c:forEach items="${listMostRecentReviews}" var="review">
            <tr>
                <td>${review.book.title}</td>
                <td>${review.rating}</td>
                <td><a href="edit_review?id=${review.reviewId}">${review.headline}</a></td>
                <td>${review.customer.fullName}</td>
                <td>
                    ${review.reviewDate}
                </td>
            </tr>
            </c:forEach>

            <c:if test="${empty listMostRecentReviews}">
                <tr><td colspan="5" align="center">No recent reviews found.</td></tr>
            </c:if>
        </table>

        <div class="statistics" style="margin-top: 30px; padding: 10px; font-weight: bold;">
            <h3>Statistics:</h3>
            Total Users: ${totalUsers} &nbsp;&nbsp;
            Total Books: ${totalBooks} &nbsp;&nbsp;
            Total Customers: ${totalCustomers} &nbsp;&nbsp;
            Total Reviews: ${totalReviews} &nbsp;&nbsp;
            Total Orders: ${totalOrders}
        </div>
    </div>

    <jsp:include page="/admin/footer_admin.jsp"></jsp:include>
</body>
</html>