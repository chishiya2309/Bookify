<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Order Management</title>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp" />

<h2>Order Management</h2>

<table border="1" width="100%" cellpadding="8">
    <tr>
        <th>#</th>
        <th>Order ID</th>
        <th>Customer</th>
        <th>Quantity</th>
        <th>Total</th>
        <th>Payment</th>
        <th>Status</th>
        <th>Order Date</th>
        <th>Action</th>
    </tr>

    <c:forEach items="${orders}" var="o" varStatus="st">
<tr>
    <td>${st.index + 1}</td>
    <td>${o.orderId}</td>
    <td>${o.customer.fullName}</td>
    <td>${o.totalQuantity}</td>
    <td>$${o.totalAmount}</td>
    <td>${o.paymentMethod}</td>
    <td>${o.orderStatus}</td>
    <td>${o.orderDate}</td>
    <td>
        <a href="${pageContext.request.contextPath}/admin/orders?action=edit&id=${o.orderId}">
            View / Edit
        </a>
        |
        <a href="${pageContext.request.contextPath}/admin/orders?action=delete&id=${o.orderId}"
           onclick="return confirm('Delete order #${o.orderId}?')">
            Delete
        </a>
    </td>
</tr>
</c:forEach>

</table>

<jsp:include page="/admin/footer_admin.jsp" />
</body>
</html>
