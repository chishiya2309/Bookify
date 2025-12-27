<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>


<html>
<head>
    <title>Edit Order</title>
</head>
<body>
<jsp:include page="/admin/header_admin.jsp" />
<h2>Order ID: ${order.orderId}</h2>

<form method="post"
      action="${pageContext.request.contextPath}/admin/orders">

    <input type="hidden" name="action" value="save"/>
    <input type="hidden" name="orderId" value="${order.orderId}"/>

    <p><b>Customer:</b> ${order.customer.fullName}</p>
    <p><b>Order Date:</b> ${order.orderDate}</p>

    <p>
        <b>Recipient Name:</b>
        <input type="text" name="recipientName"
               value="${order.recipientName}" required/>
    </p>

    <h3>Shipping Address</h3>

    Street: <input type="text" name="street"
                   value="${order.shippingAddress.street}"/><br/>
    Ward: <input type="text" name="ward"
                 value="${order.shippingAddress.ward}"/><br/>
    District: <input type="text" name="district"
                     value="${order.shippingAddress.district}"/><br/>
    Province: <input type="text" name="province"
                     value="${order.shippingAddress.province}"/><br/>
    Zip Code: <input type="text" name="zipCode"
                     value="${order.shippingAddress.zipCode}"/><br/>

    <p>
        <b>Payment Method:</b>
        <select name="paymentMethod">
            <option value="COD"
                ${order.paymentMethod == 'COD' ? 'selected' : ''}>COD</option>
            <option value="CREDIT_CARD"
                ${order.paymentMethod == 'CREDIT_CARD' ? 'selected' : ''}>
                Credit Card
            </option>
            <option value="BANK_TRANSFER"
                ${order.paymentMethod == 'BANK_TRANSFER' ? 'selected' : ''}>BANK_TRANSFER</option>
            <option value="SEPAY"
                ${order.paymentMethod == 'SEPAY' ? 'selected' : ''}>SEPAY</option>
            <option value="MOMO"
                ${order.paymentMethod == 'MOMO' ? 'selected' : ''}>MOMO</option>
            <option value="VNPAY"
                ${order.paymentMethod == 'VNPAY' ? 'selected' : ''}>VNPAY</option>
            
        </select>
    </p>

    <p>
        <b>Status:</b>
        <select name="orderStatus">
            <c:forEach items="${statuses}" var="s">
                <option value="${s}"
                    ${s == order.orderStatus ? 'selected' : ''}>
                    ${s}
                </option>
            </c:forEach>
        </select>
    </p>

    <button type="submit">Save</button>
    <a href="${pageContext.request.contextPath}/admin/orders">Cancel</a>
</form>


<hr/>

<h3>Ordered Books</h3>

<table border="1" width="100%" cellpadding="6">
    <tr>
        <th>#</th>
        <th>Book</th>
        <th>Price</th>
        <th>Qty</th>
        <th>Subtotal</th>
        <th>Action</th>
    </tr>

    <c:forEach items="${order.orderDetails}" var="d" varStatus="st">
        <tr>
            <td>${st.index + 1}</td>

            <td>${d.book.title}</td>

            <td>
                <fmt:formatNumber value="${d.unitPrice}" type="currency"/>
            </td>

            <!-- UPDATE QTY -->
            <td>
                <form method="post"
                      action="${pageContext.request.contextPath}/admin/orders"
                      style="display:inline">

                    <input type="hidden" name="action" value="updateQty"/>
                    <input type="hidden" name="orderId"
                           value="${order.orderId}"/>
                    <input type="hidden" name="detailId"
                           value="${d.orderDetailId}"/>

                    <input type="number" name="quantity"
                           value="${d.quantity}" min="1" style="width:60px"/>

                    <button type="submit">Update</button>
                </form>
            </td>

            <td>
                <fmt:formatNumber value="${d.subTotal}" type="currency"/>
            </td>

            <!-- REMOVE BOOK -->
            <td>
                <form method="post"
                      action="${pageContext.request.contextPath}/admin/orders"
                      style="display:inline"
                      onsubmit="return confirm('Remove this book?')">

                    <input type="hidden" name="action" value="removeBook"/>
                    <input type="hidden" name="orderId"
                           value="${order.orderId}"/>
                    <input type="hidden" name="detailId"
                           value="${d.orderDetailId}"/>

                    <button type="submit">Remove</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>


<jsp:include page="/admin/footer_admin.jsp" />

</body>

</html>

