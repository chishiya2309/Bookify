<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Chỉnh sửa đơn hàng #${order.orderId}</title>
    <style>
        .edit-container {
            padding: 30px;
            background-color: #f8f9fa;
            min-height: 100vh;
        }

        .edit-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 30px;
            margin-bottom: 30px;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
        }

        .page-header {
            margin-bottom: 25px;
            border-bottom: 1px solid #eee;
            padding-bottom: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .page-header h2 {
            color: #1a237e;
            margin: 0;
            font-size: 24px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #555;
            font-size: 14px;
        }

        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.2s;
            box-sizing: border-box;
        }

        .form-control:focus {
            border-color: #1a237e;
            outline: none;
        }

        .section-title {
            color: #1a237e;
            font-size: 18px;
            margin: 30px 0 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e8eaf6;
        }

        .btn-container {
            margin-top: 30px;
            display: flex;
            gap: 15px;
        }

        .btn {
            padding: 10px 25px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s;
        }

        .btn-primary {
            background: #1a237e;
            color: white;
        }

        .btn-primary:hover {
            background: #283593;
        }

        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }

        .btn-secondary:hover {
            background: #d5d5d5;
        }

        .btn-danger {
            background: #ffebee;
            color: #c62828;
            padding: 6px 12px;
            font-size: 13px;
        }

        .btn-danger:hover {
            background: #c62828;
            color: white;
        }

        .btn-update {
            background: #e3f2fd;
            color: #1565c0;
            padding: 6px 12px;
            font-size: 13px;
        }

        .btn-update:hover {
            background: #1565c0;
            color: white;
        }

        /* Table Styles */
        .items-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        .items-table th {
            background: #f5f7ff;
            color: #1a237e;
            padding: 12px;
            text-align: left;
            font-weight: 600;
            font-size: 13px;
            border-bottom: 2px solid #e8eaf6;
        }

        .items-table td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            color: #333;
            vertical-align: middle;
        }

        .items-table tr:last-child td {
            border-bottom: none;
        }

        .price {
            font-weight: 600;
            color: #2e7d32;
        }

        .qty-input {
            width: 70px;
            padding: 6px;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-align: center;
        }

        .badge {
            padding: 5px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }

        .read-only-field {
            background-color: #f5f5f5;
            color: #666;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #eee;
        }
    </style>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp" />

    <div class="edit-container">
        <div class="edit-card">
            <div class="page-header">
                <h2>Chỉnh sửa đơn hàng #${order.orderId}</h2>
                <span class="badge" style="background: #e8eaf6; color: #1a237e;">
                    Ngày đặt: <fmt:parseDate value="${order.orderDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/><fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm"/>
                </span>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/admin/orders">
                <input type="hidden" name="action" value="save"/>
                <input type="hidden" name="orderId" value="${order.orderId}"/>

                <div class="form-grid">
                    <!-- Customer Info -->
                    <div class="form-group">
                        <label>Khách hàng</label>
                        <div class="read-only-field">${order.customer.fullName}</div>
                    </div>
                    
                    <div class="form-group">
                        <label>Email</label>
                        <div class="read-only-field">${order.customer.email}</div>
                    </div>

                    <!-- Recipient -->
                    <div class="form-group">
                        <label>Người nhận hàng</label>
                        <input type="text" name="recipientName" value="${order.recipientName}" required class="form-control"/>
                    </div>

                    <!-- Payment Method -->
                    <div class="form-group">
                        <label>Phương thức thanh toán</label>
                        <select name="paymentMethod" class="form-control">
                            <option value="COD" ${order.paymentMethod == 'COD' ? 'selected' : ''}>COD (Thanh toán khi nhận hàng)</option>
                            <option value="BANK_TRANSFER" ${order.paymentMethod == 'BANK_TRANSFER' ? 'selected' : ''}>Chuyển khoản ngân hàng</option>
                        </select>
                    </div>

                    <!-- Status -->
                    <div class="form-group" style="grid-column: span 2;">
                        <label>Trạng thái đơn hàng</label>
                        <select name="orderStatus" class="form-control" style="font-weight: 600; color: #1a237e;">
                            <c:forEach items="${statuses}" var="s">
                                <option value="${s}" ${s == order.orderStatus ? 'selected' : ''}>
                                    <c:choose>
                                        <c:when test="${s == 'PENDING'}">PENDING - Chờ xử lý</c:when>
                                        <c:when test="${s == 'PROCESSING'}">PROCESSING - Đang xử lý</c:when>
                                        <c:when test="${s == 'SHIPPED'}">SHIPPED - Đang giao hàng</c:when>
                                        <c:when test="${s == 'DELIVERED'}">DELIVERED - Đã giao hàng</c:when>
                                        <c:when test="${s == 'CANCELLED'}">CANCELLED - Đã hủy</c:when>
                                        <c:otherwise>${s}</c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- Shipping Address -->
                <h3 class="section-title">Địa chỉ giao hàng</h3>
                <div class="form-grid">
                    <div class="form-group" style="grid-column: span 2;">
                        <label>Số đia chỉ/Đường</label>
                        <input type="text" name="street" value="${order.shippingAddress.street}" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label>Phường/Xã</label>
                        <input type="text" name="ward" value="${order.shippingAddress.ward}" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label>Quận/Huyện</label>
                        <input type="text" name="district" value="${order.shippingAddress.district}" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label>Tỉnh/Thành phố</label>
                        <input type="text" name="province" value="${order.shippingAddress.province}" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label>Mã bưu chính</label>
                        <input type="text" name="zipCode" value="${order.shippingAddress.zipCode}" class="form-control"/>
                    </div>
                </div>

                <div class="btn-container">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save" style="margin-right: 8px;"></i> Lưu thay đổi
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">
                        <i class="fas fa-arrow-left" style="margin-right: 8px;"></i> Quay lại
                    </a>
                </div>
            </form>

            <!-- Order Details -->
            <h3 class="section-title" style="margin-top: 50px;">Chi tiết sản phẩm</h3>
            <table class="items-table">
                <thead>
                    <tr>
                        <th width="5%">#</th>
                        <th width="40%">Sản phẩm</th>
                        <th width="15%">Đơn giá</th>
                        <th width="20%">Số lượng</th>
                        <th width="15%">Thành tiền</th>
                        <th width="5%">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${order.orderDetails}" var="d" varStatus="st">
                        <tr>
                            <td>${st.index + 1}</td>
                            <td>${d.book.title}</td>
                            <td class="price">
                                <fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/orders" 
                                      style="display:flex; gap: 8px; align-items: center;">
                                    <input type="hidden" name="action" value="updateQty"/>
                                    <input type="hidden" name="orderId" value="${order.orderId}"/>
                                    <input type="hidden" name="detailId" value="${d.orderDetailId}"/>
                                    
                                    <input type="number" name="quantity" value="${d.quantity}" min="1" class="qty-input"/>
                                    <button type="submit" class="btn btn-update">Cập nhật</button>
                                </form>
                            </td>
                            <td class="price">
                                <fmt:formatNumber value="${d.subTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/orders" 
                                      onsubmit="return confirm('Xác nhận xóa sản phẩm này khỏi đơn hàng?')">
                                    <input type="hidden" name="action" value="removeBook"/>
                                    <input type="hidden" name="orderId" value="${order.orderId}"/>
                                    <input type="hidden" name="detailId" value="${d.orderDetailId}"/>
                                    
                                    <button type="submit" class="btn btn-danger">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr style="background-color: #fcfcfc;">
                        <td colspan="4" style="text-align: right; font-weight: bold; padding-right: 20px;">Tổng tiền:</td>
                        <td colspan="2" class="price" style="font-size: 16px;">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <jsp:include page="/admin/footer_admin.jsp" />
</body>
</html>
