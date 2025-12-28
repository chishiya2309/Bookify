<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận đơn hàng - Bookify</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    
    <!-- Google Fonts - Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            /* Màu thương hiệu */
            --color-primary: #0D6EFD;
            --color-secondary: #6C757D;
            --color-accent: #FF9900;
            
            /* Màu nền */
            --bg-body: #F8F9FA;
            --bg-white: #FFFFFF;
            
            /* Màu chữ */
            --text-main: #212529;
            --text-light: #6C757D;
            
            /* Màu trạng thái */
            --color-success: #198754;
            --color-error: #DC3545;
            --color-warning: #FFC107;
            
            /* Màu bổ sung */
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
            -moz-osx-font-smoothing: grayscale;
        }

        /* ==================== CONTAINER ==================== */
        .confirmation-container {
            max-width: 1000px;
            margin: 40px auto;
            padding: 0 20px;
        }

        /* ==================== SUCCESS BANNER ==================== */
        .success-banner {
            background: linear-gradient(135deg, var(--color-success) 0%, #146c43 100%);
            color: white;
            padding: 40px;
            border-radius: var(--border-radius);
            text-align: center;
            margin-bottom: 32px;
            box-shadow: var(--shadow-lg);
            animation: slideDown 0.6s ease-out;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .success-icon {
            font-size: 64px;
            margin-bottom: 20px;
            animation: scaleIn 0.5s ease-out 0.2s both;
        }

        @keyframes scaleIn {
            from {
                transform: scale(0);
            }
            to {
                transform: scale(1);
            }
        }

        .success-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 12px;
        }

        .success-message {
            font-size: 16px;
            opacity: 0.95;
        }

        .order-number {
            font-size: 20px;
            font-weight: 600;
            margin-top: 16px;
            padding: 12px 24px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 8px;
            display: inline-block;
        }

        /* ==================== CARDS ==================== */
        .info-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 32px;
            margin-bottom: 24px;
            box-shadow: var(--shadow-md);
            animation: fadeInUp 0.6s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
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

        /* ==================== ORDER STATUS ==================== */
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

        /* ==================== ORDER DETAILS ==================== */
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
            font-weight: 600;
            color: var(--text-main);
        }

        .item-unit-price {
            font-size: 14px;
            color: var(--text-light);
            margin-bottom: 4px;
        }

        .item-subtotal {
            font-size: 18px;
            color: var(--color-primary);
        }

        /* ==================== TOTALS ==================== */
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

        /* ==================== PAYMENT INFO ==================== */
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

        .status-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .status-badge.pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-badge.paid {
            background: #d1e7dd;
            color: #0f5132;
        }

        .status-badge.unpaid {
            background: #f8d7da;
            color: #842029;
        }

        /* ==================== ACTIONS ==================== */
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
            box-shadow: var(--shadow-sm);
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
        }

        /* ==================== ERROR MESSAGE ==================== */
        .error-container {
            text-align: center;
            padding: 80px 20px;
            background: var(--bg-white);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-md);
        }

        .error-icon {
            font-size: 80px;
            color: var(--color-error);
            margin-bottom: 24px;
        }

        .error-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 12px;
        }

        .error-message {
            font-size: 16px;
            color: var(--text-light);
            margin-bottom: 32px;
        }

        /* ==================== RESPONSIVE ==================== */
        @media (max-width: 768px) {
            .confirmation-container {
                margin: 20px auto;
            }

            .success-banner {
                padding: 30px 20px;
            }

            .success-title {
                font-size: 24px;
            }

            .info-card {
                padding: 24px;
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

            .action-buttons {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    
    <!-- Reuse Header Logic -->
    <c:choose>
        <c:when test="${isGuest}">
            <jsp:include page="/customer/header_sign_in.jsp"/>
        </c:when>
        <c:otherwise>
            <jsp:include page="/customer/header_customer.jsp"/>
        </c:otherwise>
    </c:choose>

    <main class="confirmation-container">
        <c:choose>
            <c:when test="${not empty error}">
                <!-- Error State -->
                <section class="error-container">
                    <i class="fas fa-exclamation-circle error-icon"></i>
                    <h1 class="error-title">Có lỗi xảy ra</h1>
                    <p class="error-message"><c:out value="${error}"/></p>
                    <nav class="action-buttons">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                            <i class="fas fa-home"></i> Về trang chủ
                        </a>
                    </nav>
                </section>
            </c:when>
            <c:otherwise>
                <!-- Banner - Changes based on order status -->
                <c:choose>
                    <c:when test="${param.cancelled == 'true' || order.orderStatus == 'CANCELLED'}">
                        <!-- Cancelled Order Banner -->
                        <header class="success-banner" style="background: linear-gradient(135deg, #6C757D 0%, #495057 100%);">
                            <div class="success-icon">
                                <i class="fas fa-ban"></i>
                            </div>
                            <h1 class="success-title">Đơn hàng đã được hủy</h1>
                            <p class="success-message">
                                <c:choose>
                                    <c:when test="${not empty param.message}">
                                        ${param.message}
                                    </c:when>
                                    <c:otherwise>
                                        Đơn hàng của bạn đã được hủy thành công.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <div class="order-number">
                                Mã đơn hàng: #${order.orderId}
                            </div>
                        </header>
                    </c:when>
                    <c:otherwise>
                        <!-- Success Banner -->
                        <header class="success-banner">
                            <div class="success-icon">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <h1 class="success-title">Đặt hàng thành công!</h1>
                            <p class="success-message">
                                Cảm ơn bạn đã đặt hàng tại Bookify. Chúng tôi sẽ xử lý đơn hàng của bạn trong thời gian sớm nhất.
                            </p>
                            <div class="order-number">
                                Mã đơn hàng: #${order.orderId}
                            </div>
                        </header>
                    </c:otherwise>
                </c:choose>

                <!-- Order Status Timeline -->
                <section class="info-card">
                    <header class="card-title">
                        <i class="fas fa-shipping-fast"></i>
                        Trạng thái đơn hàng
                    </header>
                    
                    <c:choose>
                        <c:when test="${order.orderStatus == 'CANCELLED'}">
                            <!-- Cancelled Order Timeline -->
                            <div class="status-timeline">
                                <div class="status-step" style="flex: none; width: 100%;">
                                    <div class="status-step-icon" style="background: #DC3545; color: white;">
                                        <i class="fas fa-times"></i>
                                    </div>
                                    <div class="status-step-label" style="color: #DC3545; font-weight: 600;">Đơn hàng đã bị hủy</div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Normal Order Timeline -->
                            <div class="status-timeline">
                                <div class="status-step completed">
                                    <div class="status-step-icon">
                                        <i class="fas fa-check"></i>
                                    </div>
                                    <div class="status-step-label">Đã đặt hàng</div>
                                </div>
                                <div class="status-step ${order.orderStatus == 'PROCESSING' || order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'active' : ''}">
                                    <div class="status-step-icon">
                                        <i class="fas fa-box"></i>
                                    </div>
                                    <div class="status-step-label">Đang xử lý</div>
                                </div>
                                <div class="status-step ${order.orderStatus == 'SHIPPED' || order.orderStatus == 'DELIVERED' ? 'active' : ''}">
                                    <div class="status-step-icon">
                                        <i class="fas fa-truck"></i>
                                    </div>
                                    <div class="status-step-label">Đang giao</div>
                                </div>
                                <div class="status-step ${order.orderStatus == 'DELIVERED' ? 'active' : ''}">
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
                            <span class="detail-label">Ngày đặt hàng</span>
                            <span class="detail-value">
                                <c:set var="orderDateTime" value="${order.orderDate.toString()}"/>
                                ${orderDateTime.substring(8, 10)}/${orderDateTime.substring(5, 7)}/${orderDateTime.substring(0, 4)} 
                                ${orderDateTime.substring(11, 16)}
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Trạng thái đơn hàng</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${order.orderStatus == 'PENDING'}">Chờ xử lý</c:when>
                                    <c:when test="${order.orderStatus == 'PROCESSING'}">Đang xử lý</c:when>
                                    <c:when test="${order.orderStatus == 'SHIPPED'}">Đang giao hàng</c:when>
                                    <c:when test="${order.orderStatus == 'DELIVERED'}">Đã giao hàng</c:when>
                                    <c:when test="${order.orderStatus == 'CANCELLED'}">Đã hủy</c:when>
                                    <c:otherwise><c:out value="${order.orderStatus}"/></c:otherwise>
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
                                    <c:otherwise>
                                        <span class="payment-badge online">
                                            <i class="fas fa-credit-card"></i> <c:out value="${order.paymentMethod}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-item">
                            <span class="detail-label">Trạng thái thanh toán</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${order.paymentStatus == 'PAID'}">
                                        <span class="status-badge paid">Đã thanh toán</span>
                                    </c:when>
                                    <c:when test="${order.paymentStatus == 'UNPAID'}">
                                        <span class="status-badge unpaid">Chưa thanh toán</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge pending"><c:out value="${order.paymentStatus}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </section>

                <!-- VietQR Payment Section (for BANK_TRANSFER) -->
                <c:if test="${showPaymentQR}">
                    <section class="info-card" style="background: linear-gradient(135deg, #fff8e1 0%, #ffffff 100%); border: 2px solid #ffc107;">
                        <header class="card-title" style="color: #856404;">
                            <i class="fas fa-qrcode"></i>
                            Quét mã để thanh toán
                        </header>
                        
                        <div style="display: flex; gap: 32px; flex-wrap: wrap; align-items: flex-start;">
                            <!-- QR Code -->
                            <div style="text-align: center;">
                                <img src="${vietQRUrl}" alt="VietQR" style="width: 250px; height: 250px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                                <p style="margin-top: 12px; font-size: 13px; color: var(--text-light);">
                                    Sử dụng app ngân hàng để quét mã QR
                                </p>
                            </div>
                            
                            <!-- Bank Info -->
                            <div style="flex: 1; min-width: 280px;">
                                <div style="background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">
                                    <h3 style="font-size: 16px; margin-bottom: 16px; color: var(--text-main);">
                                        <i class="fas fa-university" style="color: #0D6EFD;"></i>
                                        Thông tin chuyển khoản
                                    </h3>
                                    
                                    <div style="margin-bottom: 14px;">
                                        <span style="font-size: 13px; color: var(--text-light);">Ngân hàng</span>
                                        <div style="font-weight: 600; font-size: 16px;">${bankName}</div>
                                    </div>
                                    
                                    <div style="margin-bottom: 14px;">
                                        <span style="font-size: 13px; color: var(--text-light);">Số tài khoản</span>
                                        <div style="display: flex; align-items: center; gap: 10px;">
                                            <span style="font-weight: 600; font-size: 18px; font-family: monospace;">${accountNumber}</span>
                                            <button onclick="copyToClipboard('${accountNumber}')" 
                                                    style="border: none; background: #e3f2fd; color: #1976d2; padding: 4px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;">
                                                <i class="fas fa-copy"></i> Sao chép
                                            </button>
                                        </div>
                                    </div>
                                    
                                    <div style="margin-bottom: 14px;">
                                        <span style="font-size: 13px; color: var(--text-light);">Nội dung chuyển khoản</span>
                                        <div style="display: flex; align-items: center; gap: 10px;">
                                            <span style="font-weight: 700; font-size: 18px; color: #d32f2f; font-family: monospace;">${transferContent}</span>
                                            <button onclick="copyToClipboard('${transferContent}')" 
                                                    style="border: none; background: #ffebee; color: #c62828; padding: 4px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;">
                                                <i class="fas fa-copy"></i> Sao chép
                                            </button>
                                        </div>
                                    </div>
                                    
                                    <div style="margin-bottom: 14px;">
                                        <span style="font-size: 13px; color: var(--text-light);">Số tiền</span>
                                        <div style="font-weight: 700; font-size: 20px; color: #198754;">
                                            <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
                                        </div>
                                    </div>
                                    
                                    <div style="margin-top: 20px; padding: 12px; background: #fff3cd; border-radius: 8px; border-left: 4px solid #ffc107;">
                                        <p style="margin: 0; font-size: 13px; color: #856404;">
                                            <i class="fas fa-exclamation-triangle"></i>
                                            <strong>Lưu ý quan trọng:</strong> Vui lòng nhập <strong>chính xác</strong> nội dung chuyển khoản để đơn hàng được xác nhận tự động.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </c:if>

                <!-- Shipping Address -->
                <section class="info-card">
                    <header class="card-title">
                        <i class="fas fa-map-marker-alt"></i>
                        Địa chỉ giao hàng
                    </header>
                    
                    <aside class="shipping-info">
                        <p>
                            <i class="fas fa-user"></i>
                            <strong><c:out value="${order.recipientName}"/></strong>
                        </p>
                        <p>
                            <i class="fas fa-phone"></i>
                            <c:out value="${order.shippingAddress.phoneNumber}"/>
                        </p>
                        <p>
                            <i class="fas fa-map-marked-alt"></i>
                            <c:out value="${order.shippingAddress.street}"/>, <c:out value="${order.shippingAddress.ward}"/>, 
                            <c:out value="${order.shippingAddress.district}"/>, <c:out value="${order.shippingAddress.province}"/>
                        </p>
                    </aside>
                </section>

                <!-- Order Items -->
                <section class="info-card">
                    <header class="card-title">
                        <i class="fas fa-shopping-bag"></i>
                        Sản phẩm đã đặt
                    </header>
                    
                    <c:forEach var="detail" items="${order.orderDetails}">
                        <article class="order-item">
                            <img src="${not empty detail.book.primaryImageUrl ? detail.book.primaryImageUrl : pageContext.request.contextPath.concat('/images/no-image.jpg')}" 
                                 class="item-image"
                                 alt="<c:out value='${detail.book.title}'/>"
                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.jpg';">
                            
                            <div class="item-details">
                                <div class="item-title"><c:out value="${detail.book.title}"/></div>
                                <div class="item-author">
                                    <c:forEach var="author" items="${detail.book.authors}" varStatus="status">
                                        <c:out value="${author.name}"/>${!status.last ? ', ' : ''}
                                    </c:forEach>
                                </div>
                                <div class="item-quantity">Số lượng: ${detail.quantity}</div>
                            </div>
                            
                            <div class="item-price">
                                <div class="item-unit-price">
                                    <fmt:formatNumber value="${detail.unitPrice}" pattern="#,###"/>₫
                                </div>
                                <div class="item-subtotal">
                                    <fmt:formatNumber value="${detail.subTotal}" pattern="#,###"/>₫
                                </div>
                            </div>
                        </article>
                    </c:forEach>

                    <!-- Order Totals -->
                    <footer class="order-totals">
                        <div class="total-row">
                            <span>Tạm tính</span>
                            <span>
                                <c:choose>
                                    <c:when test="${not empty order.subtotal}">
                                        <fmt:formatNumber value="${order.subtotal}" pattern="#,###"/>₫
                                    </c:when>
                                    <c:otherwise>
                                        <%-- Calculate subtotal = totalAmount - shippingFee if subtotal not set --%>
                                        <c:set var="calcSubtotal" value="${order.totalAmount}"/>
                                        <c:if test="${not empty order.shippingFee}">
                                            <c:set var="calcSubtotal" value="${order.totalAmount - order.shippingFee}"/>
                                        </c:if>
                                        <fmt:formatNumber value="${calcSubtotal}" pattern="#,###"/>₫
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="total-row">
                            <span>Phí vận chuyển</span>
                            <span>
                                <c:choose>
                                    <c:when test="${empty order.shippingFee or order.shippingFee == 0}">
                                        <span style="color: var(--color-success); font-weight: bold;">Miễn phí</span>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${order.shippingFee}" pattern="#,###"/>₫
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        
                        <!-- Voucher Discount Row -->
                        <c:if test="${not empty order.voucherDiscount and order.voucherDiscount > 0}">
                            <div class="total-row" style="color: #dc3545;">
                                <span>
                                    <i class="fas fa-tag"></i> Giảm giá
                                    <c:if test="${not empty order.voucherCode}">
                                        (<c:out value="${order.voucherCode}"/>)
                                    </c:if>
                                </span>
                                <span style="font-weight: bold;">-<fmt:formatNumber value="${order.voucherDiscount}" pattern="#,###"/>₫</span>
                            </div>
                        </c:if>
                        
                        <div class="total-row grand-total">
                            <span>Tổng cộng</span>
                            <span><fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫</span>
                        </div>
                    </footer>
                </section>

                <!-- Action Buttons -->
                <nav class="action-buttons">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                        <i class="fas fa-home"></i> Về trang chủ
                    </a>
                    <a href="${pageContext.request.contextPath}/customer/orders" class="btn btn-primary">
                        <i class="fas fa-history"></i> Xem đơn hàng của tôi
                    </a>
                    <%-- Cancel button: Only for COD orders in PENDING or PROCESSING status --%>
                    <c:if test="${order.paymentMethod == 'COD' && (order.orderStatus == 'PENDING' || order.orderStatus == 'PROCESSING')}">
                        <button type="button" class="btn btn-danger" onclick="showCancelModal()">
                            <i class="fas fa-times-circle"></i> Huỷ đơn hàng
                        </button>
                    </c:if>
                </nav>

                <%-- Cancel Order Modal --%>
                <c:if test="${order.paymentMethod == 'COD' && (order.orderStatus == 'PENDING' || order.orderStatus == 'PROCESSING')}">
                    <dialog id="cancelModal" class="modal-overlay">
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
                    </dialog>
                </c:if>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="/customer/footer_customer.jsp"/>

    <!-- Copy to Clipboard Script -->
    <script>
        function copyToClipboard(text) {
            navigator.clipboard.writeText(text).then(function() {
                // Show toast notification
                var toast = document.createElement('div');
                toast.textContent = 'Đã sao chép: ' + text;
                toast.style.cssText = 'position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background: #333; color: white; padding: 12px 24px; border-radius: 8px; z-index: 9999; animation: fadeIn 0.3s ease;';
                document.body.appendChild(toast);
                setTimeout(function() {
                    toast.remove();
                }, 2000);
            }).catch(function(err) {
                console.error('Không thể sao chép: ', err);
            });
        }

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
