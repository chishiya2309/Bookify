<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<html>
<head>
    <title>My Order History</title>
</head>
<body>

<jsp:include page="/customer/header_customer.jsp" />

<h2>My Order History</h2>

<table border="1" width="100%" cellpadding="8">
    <tr>
        <th>#</th>
        <th>Order ID</th>
        <th>Quantity</th>
        <th>Total</th>
        <th>Date</th>
        <th>Status</th>
        <th>Action</th>
    </tr>

    <c:forEach items="${orders}" var="o" varStatus="st">
        <tr>
            <td>${st.index + 1}</td>
            <td>${o.orderId}</td>
            <td>${o.totalQuantity}</td>
            <td>
                <fmt:formatNumber value="${o.totalAmount}" type="currency"/>
            </td>
            <td>
                <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
            </td>
            <td>${o.orderStatus}</td>
            <td>
                <a href="${pageContext.request.contextPath}/customer/orders?action=detail&id=${o.orderId}
">
                    View Detail
                </a>
            </td>
        </tr>
    </c:forEach>
</table>

<jsp:include page="/customer/footer_customer.jsp" />
</body>
</html>
