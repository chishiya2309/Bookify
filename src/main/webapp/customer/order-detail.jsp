<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<html>
<head>
    <title>Order Detail</title>
</head>
<body>

<jsp:include page="/customer/header_customer.jsp" />

<h2>Order ID: ${order.orderId}</h2>

<p><b>Status:</b> ${order.orderStatus}</p>
<p><b>Order Date:</b>
    <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
</p>

<p><b>Total Amount:</b>
    <fmt:formatNumber value="${order.totalAmount}" type="currency"/>
</p>

<hr/>

<h3>Shipping Information</h3>
<p><b>Recipient:</b> ${order.recipientName}</p>
<p><b>Phone:</b> ${order.customer.phoneNumber}</p>

<p>
    ${order.shippingAddress.street},<br/>
    ${order.shippingAddress.ward}, ${order.shippingAddress.district},<br/>
    ${order.shippingAddress.province} ${order.shippingAddress.zipCode}
</p>

<hr/>

<h3>Payment</h3>
<p><b>Method:</b> ${order.payment.paymentMethod}</p>
<p><b>Status:</b> ${order.payment.paymentStatus}</p>

<hr/>

<h3>Ordered Books</h3>

<table border="1" width="100%" cellpadding="6">
    <tr>
        <th>#</th>
        <th>Book</th>
        <th>Price</th>
        <th>Qty</th>
        <th>Subtotal</th>
    </tr>

    <c:forEach items="${order.orderDetails}" var="d" varStatus="st">
        <tr>
            <td>${st.index + 1}</td>
            <td>${d.book.title}</td>
            <td>
                <fmt:formatNumber value="${d.unitPrice}" type="currency"/>
            </td>
            <td>${d.quantity}</td>
            <td>
                <fmt:formatNumber value="${d.subTotal}" type="currency"/>
            </td>
        </tr>
    </c:forEach>
</table>

<h3>Total Quantity: ${order.totalQuantity}</h3>

<br/>
<a href="${pageContext.request.contextPath}/customer/orders">
    ← Back to Order History
</a>

<jsp:include page="/customer/footer_customer.jsp" />
</body>
</html>
