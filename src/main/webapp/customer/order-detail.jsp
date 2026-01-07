<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng #${order.orderId} - Bookify</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    
    <!-- Google Fonts - Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            /* Brand colors */
            --color-primary: #0D6EFD;
            --color-secondary: #6C757D;
            --color-accent: #FF9900;
            
            /* Background */
            --bg-body: #F8F9FA;
            --bg-white: #FFFFFF;
            
            /* Text */
            --text-main: #212529;
            --text-light: #6C757D;
            
            /* Status colors */
            --color-success: #198754;
            --color-error: #DC3545;
            --color-warning: #FFC107;
            
            /* Utils */
            --input-border: #DEE2E6;
            --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
            --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.1);
            --shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.12);
            --border-radius: 12px;
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
            color: var(--text-main);
            line-height: 1.6;
            min-height: 100vh;
            -webkit-font-smoothing: antialiased;
        }

        /* ==================== CONTAINER ==================== */
        .detail-container {
            max-width: 1000px;
            margin: 40px auto;
            padding: 0 20px;
        }

        /* ==================== BACK LINK ==================== */
        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--text-light);
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 20px;
            transition: var(--transition);
        }

        .back-link:hover {
            color: var(--color-primary);
        }

        /* ==================== INFO CARD ==================== */
        .info-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 32px;
            margin-bottom: 24px;
            box-shadow: var(--shadow-md);
        }

        .card-title {
            font-size: 20px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 24px;
            padding-bottom: 16px;
            border-bottom: 2px solid var(--input-border);
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .card-title i {
            color: var(--color-primary);
            font-size: 24px;
        }

        /* ==================== ORDER HEADER ==================== */
        .order-header-card {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            padding: 32px;
            border-radius: var(--border-radius);
            margin-bottom: 24px;
            box-shadow: var(--shadow-lg);
        }

        .order-title {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .order-date-header {
            font-size: 14px;
            opacity: 0.9;
        }

        /* ==================== STATUS TIMELINE ==================== */
        .status-timeline {
            display: flex;
            justify-content: space-between;
            margin: 32px 0;
            position: relative;
        }

        .status-timeline::before {
            content: '';
            position: absolute;
            top: 20px;
            left: 0;
            right: 0;
            height: 4px;
            background: var(--input-border);
            z-index: 0;
        }

        .status-step {
            flex: 1;
            text-align: center;
            position: relative;
            z-index: 1;
        }

        .status-step-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: var(--input-border);
            color: var(--text-light);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 12px;
            font-size: 18px;
            transition: var(--transition);
        }

        .status-step.active .status-step-icon {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.4);
        }

        .status-step.completed .status-step-icon {
            background: var(--color-success);
            color: white;
        }

        .status-step.cancelled .status-step-icon {
            background: var(--color-error);
            color: white;
        }

        .status-step-label {
            font-size: 13px;
            color: var(--text-light);
            font-weight: 500;
        }

        .status-step.active .status-step-label,
        .status-step.completed .status-step-label {
            color: var(--text-main);
            font-weight: 600;
        }

        /* ==================== DETAIL GRID ==================== */
        .detail-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 24px;
            margin-bottom: 24px;
        }

        .detail-item {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .detail-label {
            font-size: 13px;
            color: var(--text-light);
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .detail-value {
            font-size: 16px;
            color: var(--text-main);
            font-weight: 600;
        }

        /* ==================== STATUS BADGES ==================== */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .status-pending { background: #fff3cd; color: #856404; }
        .status-processing { background: #cce5ff; color: #004085; }
        .status-shipped { background: #d1ecf1; color: #0c5460; }
        .status-delivered { background: #d4edda; color: #155724; }
        .status-cancelled { background: #f8d7da; color: #721c24; }

        .payment-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
        }

        .payment-badge.cod {
            background: #fff3cd;
            color: #856404;
        }

        .payment-badge.online {
            background: #d1e7dd;
            color: #0f5132;
        }

        /* ==================== ORDER ITEMS ==================== */
        .order-item {
            display: flex;
            gap: 16px;
            padding: 20px 0;
            border-bottom: 1px solid var(--input-border);
        }

        .order-item:last-child {
            border-bottom: none;
        }

        .item-image {
            width: 80px;
            height: 110px;
            object-fit: cover;
            border-radius: 8px;
            box-shadow: var(--shadow-sm);
        }

        .item-details {
            flex: 1;
        }

        .item-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 6px;
        }

        .item-author {
            font-size: 14px;
            color: var(--text-light);
            font-style: italic;
            margin-bottom: 8px;
        }

        .item-quantity {
            font-size: 14px;
            color: var(--text-light);
        }

        .item-price {
            text-align: right;
        }

        .item-unit-price {
            font-size: 14px;
            color: var(--text-light);
            margin-bottom: 4px;
        }

        .item-subtotal {
            font-size: 18px;
            font-weight: 600;
            color: var(--color-primary);
        }

        /* ==================== ORDER TOTALS ==================== */
        .order-totals {
            margin-top: 24px;
            padding-top: 24px;
            border-top: 2px solid var(--input-border);
        }

        .total-row {
            display: flex;
            justify-content: space-between;
            padding: 12px 0;
            font-size: 15px;
        }

        .total-row.grand-total {
            font-size: 20px;
            font-weight: 700;
            color: var(--color-primary);
            padding-top: 16px;
            border-top: 2px solid var(--color-primary);
            margin-top: 12px;
        }

        /* ==================== SHIPPING INFO ==================== */
        .shipping-info {
            background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
            padding: 20px;
            border-radius: 8px;
            border-left: 4px solid var(--color-primary);
        }

        .shipping-info p {
            margin: 8px 0;
            display: flex;
            align-items: flex-start;
            gap: 12px;
        }

        .shipping-info i {
            color: var(--color-primary);
            margin-top: 2px;
            width: 18px;
        }

        /* ==================== ACTION BUTTONS ==================== */
        .action-buttons {
            display: flex;
            gap: 16px;
            justify-content: center;
            margin-top: 32px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 14px 32px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            border: none;
            font-family: 'Inter', sans-serif;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4);
        }

        .btn-secondary {
            background: white;
            color: var(--text-main);
            border: 2px solid var(--input-border);
        }

        .btn-secondary:hover {
            background: var(--bg-body);
            border-color: var(--text-light);
            transform: translateY(-2px);
        }

        .btn-danger {
            background: linear-gradient(135deg, #DC3545 0%, #c82333 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
        }

        .btn-danger:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(220, 53, 69, 0.4);
        }

        .btn-success {
            background: linear-gradient(135deg, var(--color-success) 0%, #146c43 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(25, 135, 84, 0.3);
        }

        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(25, 135, 84, 0.4);
        }

        /* ==================== RESPONSIVE ==================== */
        @media (max-width: 768px) {
            .detail-container {
                margin: 20px auto;
            }

            .info-card {
                padding: 24px;
            }

            .order-header-card {
                padding: 24px;
            }

            .order-title {
                font-size: 22px;
            }

            .detail-grid {
                grid-template-columns: 1fr;
                gap: 16px;
            }

            .status-timeline {
                flex-direction: column;
                gap: 20px;
            }

            .status-timeline::before {
                display: none;
            }

            .order-item {
                flex-direction: column;
            }

            .item-price {
                text-align: left;
                margin-top: 12px;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }
        }

        /* ==================== CANCEL MODAL ==================== */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        .modal-content {
            background: white;
            border-radius: 12px;
            padding: 32px;
            max-width: 450px;
            width: 90%;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
            animation: modalSlideIn 0.3s ease;
        }

        @keyframes modalSlideIn {
            from { opacity: 0; transform: scale(0.9); }
            to { opacity: 1; transform: scale(1); }
        }

        .modal-header {
            text-align: center;
            margin-bottom: 24px;
        }

        .modal-icon {
            font-size: 48px;
            color: #DC3545;
            margin-bottom: 16px;
        }

        .modal-title {
            font-size: 20px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
        }

        .modal-subtitle {
            font-size: 14px;
            color: var(--text-light);
        }

        .modal-body {
            margin-bottom: 24px;
        }

        .reason-select {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid var(--input-border);
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
            background: white;
            cursor: pointer;
        }

        .reason-select:focus {
            outline: none;
            border-color: var(--color-primary);
        }

        .modal-footer {
            display: flex;
            gap: 12px;
            justify-content: flex-end;
        }

        .modal-footer .btn {
            padding: 12px 24px;
            font-size: 14px;
            width: auto;
        }
    </style>
</head>
<body>

<jsp:include page="/customer/header_customer.jsp" />

<main class="detail-container">
    <!-- Success Message -->
    <c:if test="${not empty param.success}">
        <div style="max-width: 1000px; margin: 0 auto 24px auto; background: #d1e7dd; border-left: 4px solid var(--color-success); border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px;">
            <i class="fas fa-check-circle" style="color: var(--color-success); font-size: 20px;"></i>
            <span style="font-size: 14px; font-weight: 500; color: var(--color-success);">
                <c:out value="${param.success}"/>
            </span>
        </div>
    </c:if>

    <!-- Error Message -->
    <c:if test="${not empty param.error}">
        <div style="max-width: 1000px; margin: 0 auto 24px auto; background: #f8d7da; border-left: 4px solid var(--color-error); border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px;">
            <i class="fas fa-exclamation-circle" style="color: var(--color-error); font-size: 20px;"></i>
            <span style="font-size: 14px; font-weight: 500; color: #721c24;">
                <c:out value="${param.error}"/>
            </span>
        </div>
    </c:if>

    <!-- Back Link -->
    <a href="${pageContext.request.contextPath}/customer/orders" class="back-link">
        <i class="fas fa-arrow-left"></i>
        Quay lại lịch sử đơn hàng
    </a>

    <!-- Order Header -->
    <header class="order-header-card">
        <h1 class="order-title">Đơn hàng #${order.orderId}</h1>
        <p class="order-date-header">
            <i class="far fa-calendar-alt"></i>
            Đặt ngày: 
            <c:set var="orderDateTime" value="${order.orderDate.toString()}"/>
            ${orderDateTime.substring(8, 10)}/${orderDateTime.substring(5, 7)}/${orderDateTime.substring(0, 4)} 
            lúc ${orderDateTime.substring(11, 16)}
        </p>
    </header>

    <!-- Status Timeline -->
    <section class="info-card">
        <header class="card-title">
            <i class="fas fa-shipping-fast"></i>
            Trạng thái đơn hàng
        </header>
        
        <c:choose>
            <c:when test="${order.orderStatus == 'CANCELLED'}">
                <div class="status-timeline">
                    <div class="status-step cancelled">
                        <div class="status-step-icon">
                            <i class="fas fa-times"></i>
                        </div>
                        <div class="status-step-label">Đơn hàng đã bị hủy</div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="status-timeline">
                    <div class="status-step completed">
                        <div class="status-step-icon">
                            <i class="fas fa-check"></i>
                        </div>
                        <div class="status-step-label">Đã đặt hàng</div>
                    </div>
                    <div class="status-step ${order.orderStatus == 'PROCESSING' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'completed' : (order.orderStatus == 'PENDING' ? 'active' : '')}">
                        <div class="status-step-icon">
                            <i class="fas fa-box"></i>
                        </div>
                        <div class="status-step-label">Đang xử lý</div>
                    </div>
                    <div class="status-step ${order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'completed' : (order.orderStatus == 'PROCESSING' ? 'active' : '')}">
                        <div class="status-step-icon">
                            <i class="fas fa-truck"></i>
                        </div>
                        <div class="status-step-label">Đang giao</div>
                    </div>
                    <div class="status-step ${order.orderStatus == 'DELIVERED' ? 'completed' : (order.orderStatus == 'SHIPPED' ? 'active' : '')}">
                        <div class="status-step-icon">
                            <i class="fas fa-home"></i>
                        </div>
                        <div class="status-step-label">Đã giao</div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <!-- Order Details -->
    <section class="info-card">
        <header class="card-title">
            <i class="fas fa-info-circle"></i>
            Thông tin đơn hàng
        </header>
        
        <div class="detail-grid">
            <div class="detail-item">
                <span class="detail-label">Trạng thái đơn hàng</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${order.orderStatus == 'PENDING'}">
                            <span class="status-badge status-pending">
                                <i class="fas fa-clock"></i> Chờ xử lý
                            </span>
                        </c:when>
                        <c:when test="${order.orderStatus == 'PROCESSING'}">
                            <span class="status-badge status-processing">
                                <i class="fas fa-cog"></i> Đang xử lý
                            </span>
                        </c:when>
                        <c:when test="${order.orderStatus == 'SHIPPED'}">
                            <span class="status-badge status-shipped">
                                <i class="fas fa-truck"></i> Đang giao
                            </span>
                        </c:when>
                        <c:when test="${order.orderStatus == 'DELIVERED'}">
                            <span class="status-badge status-delivered">
                                <i class="fas fa-check-circle"></i> Đã giao
                            </span>
                        </c:when>
                        <c:when test="${order.orderStatus == 'CANCELLED'}">
                            <span class="status-badge status-cancelled">
                                <i class="fas fa-times-circle"></i> Đã hủy
                            </span>
                        </c:when>
                        <c:otherwise>${order.orderStatus}</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="detail-item">
                <span class="detail-label">Phương thức thanh toán</span>
                <span class="detail-value">
                    <c:choose>
                        <c:when test="${order.paymentMethod == 'COD'}">
                            <span class="payment-badge cod">
                                <i class="fas fa-money-bill-wave"></i> Thanh toán khi nhận hàng
                            </span>
                        </c:when>
                        <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">
                            <span class="payment-badge online">
                                <i class="fas fa-university"></i> Chuyển khoản ngân hàng
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="payment-badge online">
                                <i class="fas fa-credit-card"></i> ${order.paymentMethod}
                            </span>
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </section>

    <!-- Shipping Info -->
    <section class="info-card">
        <header class="card-title">
            <i class="fas fa-map-marker-alt"></i>
            Thông tin giao hàng
        </header>
        
        <div class="shipping-info">
            <p>
                <i class="fas fa-user"></i>
                <span><strong>Người nhận:</strong> ${order.recipientName}</span>
            </p>
            <c:if test="${not empty order.customer.phoneNumber}">
                <p>
                    <i class="fas fa-phone"></i>
                    <span><strong>Số điện thoại:</strong> ${order.customer.phoneNumber}</span>
                </p>
            </c:if>
            <c:if test="${not empty order.shippingAddress}">
                <p>
                    <i class="fas fa-map-marker-alt"></i>
                    <span>
                        <strong>Địa chỉ:</strong> 
                        ${order.shippingAddress.street}, 
                        ${order.shippingAddress.ward}, 
                        ${order.shippingAddress.district}, 
                        ${order.shippingAddress.province}
                        <c:if test="${not empty order.shippingAddress.zipCode}">
                            - ${order.shippingAddress.zipCode}
                        </c:if>
                    </span>
                </p>
            </c:if>
        </div>
    </section>

    <!-- Order Items -->
    <section class="info-card">
        <header class="card-title">
            <i class="fas fa-book"></i>
            Sản phẩm đã đặt (${order.totalQuantity} cuốn)
        </header>
        
        <c:forEach items="${order.orderDetails}" var="d" varStatus="st">
            <article class="order-item">
                <c:choose>
                    <c:when test="${not empty d.book.primaryImageUrl}">
                        <img src="${d.book.primaryImageUrl}" alt="${d.book.title}" class="item-image">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/no-image.jpg" alt="${d.book.title}" class="item-image">
                    </c:otherwise>
                </c:choose>
                
                <div class="item-details">
                    <div class="item-title">${d.book.title}</div>
                    <c:if test="${not empty d.book.authors}">
                        <div class="item-author">
                            <c:forEach items="${d.book.authors}" var="author" varStatus="authorSt">
                                ${author.name}<c:if test="${!authorSt.last}">, </c:if>
                            </c:forEach>
                        </div>
                    </c:if>
                    <div class="item-quantity">Số lượng: ${d.quantity}</div>
                </div>
                
                <div class="item-price">
                    <div class="item-unit-price">
                        <fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/> / cuốn
                    </div>
                    <div class="item-subtotal">
                        <fmt:formatNumber value="${d.subTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </div>
                </div>
            </article>
        </c:forEach>

        <!-- Order Totals -->
        <div class="order-totals">
            <div class="total-row">
                <span>Tạm tính</span>
                <span><fmt:formatNumber value="${order.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
            </div>
            <c:if test="${not empty order.voucherCode}">
                <div class="total-row" style="color: #198754;">
                    <span>
                        <i class="fas fa-tag"></i> Mã giảm giá: <strong>${order.voucherCode}</strong>
                    </span>
                    <span>-<fmt:formatNumber value="${order.voucherDiscount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                </div>
            </c:if>
            <div class="total-row">
                <span>Phí vận chuyển</span>
                <c:choose>
                    <c:when test="${order.shippingFee != null && order.shippingFee > 0}">
                        <span><fmt:formatNumber value="${order.shippingFee}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                    </c:when>
                    <c:otherwise>
                        <span style="color: #198754;">Miễn phí</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="total-row grand-total">
                <span>Tổng cộng</span>
                <span><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
            </div>
        </div>
    </section>

    <!-- Action Buttons -->
    <nav class="action-buttons">
        <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i>
            Quay lại lịch sử đơn hàng
        </a>
        <c:if test="${order.orderStatus == 'SHIPPED'}">
            <form method="post" action="${pageContext.request.contextPath}/customer/orders" style="display: inline;">
                <input type="hidden" name="action" value="markDelivered">
                <input type="hidden" name="orderId" value="${order.orderId}">
                <input type="hidden" name="redirect" value="detail">
                <button type="submit" class="btn btn-success" onclick="return confirm('Bạn có chắc chắn đã nhận được hàng?');">
                    <i class="fas fa-check-circle"></i>
                    Đã nhận được hàng
                </button>
            </form>
        </c:if>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
            <i class="fas fa-book"></i>
            Tiếp tục mua sắm
        </a>
        <c:if test="${order.orderStatus == 'PENDING' && (order.paymentMethod == 'COD' || (order.paymentMethod == 'BANK_TRANSFER' && (order.paymentStatus.name() == 'UNPAID' || order.paymentStatus == null)))}">
            <button type="button" class="btn btn-danger" onclick="showCancelModal()">
                <i class="fas fa-times"></i>
                Hủy đơn hàng
            </button>
        </c:if>
    </nav>

    <%-- Cancel Order Modal --%>
    <c:if test="${order.orderStatus == 'PENDING' && (order.paymentMethod == 'COD' || (order.paymentMethod == 'BANK_TRANSFER' && (order.paymentStatus.name() == 'UNPAID' || order.paymentStatus == null)))}">
        <div id="cancelModal" class="modal-overlay">
            <article class="modal-content">
                <header class="modal-header">
                    <div class="modal-icon">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <h3 class="modal-title">Xác nhận huỷ đơn hàng</h3>
                    <p class="modal-subtitle">Bạn có chắc chắn muốn huỷ đơn hàng #${order.orderId}?</p>
                </header>
                <form action="${pageContext.request.contextPath}/customer/cancel-order" method="post">
                    <input type="hidden" name="orderId" value="${order.orderId}">
                    <section class="modal-body">
                        <label for="cancelReason" style="display: block; margin-bottom: 8px; font-weight: 600; color: var(--text-main);">Lý do huỷ:</label>
                        <select name="reason" id="cancelReason" class="reason-select" required>
                            <option value="">-- Chọn lý do --</option>
                            <option value="Đổi ý, không muốn mua nữa">Đổi ý, không muốn mua nữa</option>
                            <option value="Đặt nhầm sản phẩm">Đặt nhầm sản phẩm</option>
                            <option value="Muốn thay đổi địa chỉ giao hàng">Muốn thay đổi địa chỉ giao hàng</option>
                            <option value="Tìm được giá rẻ hơn ở nơi khác">Tìm được giá rẻ hơn ở nơi khác</option>
                            <option value="Thời gian giao hàng quá lâu">Thời gian giao hàng quá lâu</option>
                            <option value="Lý do khác">Lý do khác</option>
                        </select>
                    </section>
                    <footer class="modal-footer">
                        <button type="button" class="btn btn-secondary" onclick="hideCancelModal()">
                            <i class="fas fa-arrow-left"></i> Quay lại
                        </button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-times"></i> Xác nhận huỷ
                        </button>
                    </footer>
                </form>
            </article>
        </div>
    </c:if>
</main>

<jsp:include page="/customer/footer_customer.jsp" />

<script>
    // Cancel modal functions
    function showCancelModal() {
        var modal = document.getElementById('cancelModal');
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden'; // Prevent background scroll
        }
    }

    function hideCancelModal() {
        var modal = document.getElementById('cancelModal');
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = ''; // Restore scroll
        }
    }

    // Close modal on ESC key or clicking outside
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            hideCancelModal();
        }
    });

    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal-overlay')) {
            hideCancelModal();
        }
    });
</script>

</body>
</html>

