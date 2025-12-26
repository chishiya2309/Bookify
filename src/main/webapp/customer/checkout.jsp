<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - Bookify</title>
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
            --input-focus: #86B7FE;
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

        /* ==================== CHECKOUT CONTAINER ==================== */
        .checkout-container {
            max-width: 1100px;
            margin: 40px auto;
            background: var(--bg-white);
            padding: 48px;
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-lg);
            animation: fadeInUp 0.6s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* ==================== SECTION TITLES ==================== */
        .section-title {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 28px;
            padding-bottom: 16px;
            border-bottom: 3px solid var(--color-primary);
            position: relative;
            letter-spacing: -0.5px;
        }

        .section-title::after {
            content: '';
            position: absolute;
            bottom: -3px;
            left: 0;
            width: 80px;
            height: 3px;
            background: var(--color-accent);
            border-radius: 2px;
        }

        /* ==================== ORDER DETAILS TABLE ==================== */
        .order-details {
            margin-bottom: 40px;
            overflow-x: auto;
        }

        .order-details table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background: var(--bg-white);
            border-radius: 8px;
            overflow: hidden;
            box-shadow: var(--shadow-sm);
        }

        .order-details th {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            padding: 16px 12px;
            text-align: left;
            font-weight: 600;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .order-details td {
            padding: 16px 12px;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
            vertical-align: middle;
        }

        .order-details tr:last-child td {
            border-bottom: none;
        }

        .order-details tbody tr {
            transition: var(--transition);
        }

        .order-details tbody tr:hover {
            background: #f8f9ff;
            transform: scale(1.01);
        }

        .book-info {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .book-img-checkout {
            width: 60px;
            height: 85px;
            object-fit: cover;
            border-radius: 6px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
            transition: var(--transition);
        }

        .book-img-checkout:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }

        .book-title {
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 4px;
            font-size: 15px;
        }

        .book-authors {
            color: var(--text-light);
            font-size: 13px;
            font-style: italic;
        }

        .total-row td {
            border-top: 3px solid var(--color-primary) !important;
            font-weight: 700;
            font-size: 16px;
            padding: 20px 12px !important;
            background: linear-gradient(to right, #f8f9ff 0%, #ffffff 100%);
        }

        .total-label {
            text-align: right;
            padding-right: 24px !important;
            color: var(--text-main);
        }

        /* ==================== ADDRESS SELECTION ==================== */
        .address-selection-section {
            margin-bottom: 40px;
        }

        .address-list {
            display: grid;
            gap: 16px;
            margin-bottom: 20px;
        }

        .address-card {
            border: 2px solid var(--input-border);
            border-radius: 10px;
            transition: var(--transition);
            background: var(--bg-white);
            overflow: hidden;
        }

        .address-card:hover {
            border-color: var(--color-primary);
            box-shadow: var(--shadow-md);
            transform: translateY(-2px);
        }

        .address-card.default-address {
            border-color: var(--color-primary);
            background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.1);
        }

        .address-radio-label {
            display: flex;
            gap: 16px;
            padding: 20px;
            cursor: pointer;
            align-items: flex-start;
        }

        .address-radio-label input[type="radio"] {
            margin-top: 4px;
            width: 20px;
            height: 20px;
            cursor: pointer;
            accent-color: var(--color-primary);
        }

        .address-content {
            flex: 1;
        }

        .address-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;
        }

        .recipient-name {
            font-size: 17px;
            font-weight: 600;
            color: var(--text-main);
        }

        .default-badge {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            box-shadow: 0 2px 6px rgba(13, 110, 253, 0.3);
        }

        .address-details p {
            margin: 8px 0;
            font-size: 14px;
            color: var(--text-light);
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }

        .address-details i {
            margin-top: 2px;
            width: 18px;
            color: var(--color-primary);
        }

        .btn-add-address {
            width: 100%;
            padding: 16px 24px;
            background: white;
            border: 2px dashed var(--color-primary);
            border-radius: 10px;
            color: var(--color-primary);
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-family: 'Inter', sans-serif;
        }

        .btn-add-address:hover {
            background: linear-gradient(135deg, #f0f7ff 0%, #e6f2ff 100%);
            border-style: solid;
            transform: translateY(-2px);
            box-shadow: var(--shadow-md);
        }

        .btn-add-address i {
            font-size: 18px;
        }

        .no-address-message {
            text-align: center;
            padding: 60px 20px;
            color: var(--text-light);
            background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
            border-radius: 10px;
            border: 2px dashed var(--input-border);
        }

        .no-address-message i {
            font-size: 64px;
            margin-bottom: 20px;
            opacity: 0.2;
            color: var(--color-primary);
        }

        .no-address-message p {
            font-size: 15px;
            font-weight: 500;
        }

        /* ==================== PAYMENT SECTION ==================== */
        .payment-section {
            margin-bottom: 40px;
        }

        .payment-section label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: var(--text-main);
            font-size: 15px;
        }

        .payment-section select {
            width: 100%;
            padding: 14px 16px;
            border: 2px solid var(--input-border);
            border-radius: 8px;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            color: var(--text-main);
            background: white;
            transition: var(--transition);
            cursor: pointer;
        }

        .payment-section select:focus {
            outline: none;
            border-color: var(--color-primary);
            box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.1);
        }

        .payment-section select:hover {
            border-color: var(--color-primary);
        }

        /* ==================== ACTION BUTTONS ==================== */
        .action-buttons {
            display: flex;
            gap: 16px;
            justify-content: flex-end;
            margin-top: 32px;
            flex-wrap: wrap;
        }

        .btn-continue-shopping,
        .btn-place-order {
            padding: 16px 32px;
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

        .btn-continue-shopping {
            background: white;
            color: var(--text-main);
            border: 2px solid var(--input-border);
        }

        .btn-continue-shopping:hover {
            background: var(--bg-body);
            border-color: var(--text-light);
            transform: translateY(-2px);
            box-shadow: var(--shadow-sm);
        }

        .btn-place-order {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.3);
        }

        .btn-place-order:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4);
        }

        .btn-place-order:active {
            transform: translateY(0);
        }

        /* ==================== EMPTY CART MESSAGE ==================== */
        .empty-cart-msg {
            text-align: center;
            padding: 80px 20px;
            background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
            border-radius: var(--border-radius);
            border: 2px dashed var(--input-border);
        }

        .empty-cart-msg h2 {
            color: var(--text-main);
            margin-bottom: 16px;
            font-size: 28px;
            font-weight: 700;
        }

        .empty-cart-msg p {
            color: var(--text-light);
            margin-bottom: 24px;
            font-size: 16px;
        }

        .empty-cart-msg i {
            font-size: 80px;
            color: var(--text-light);
            opacity: 0.3;
            margin-bottom: 24px;
        }

        /* ==================== RESPONSIVE DESIGN ==================== */
        @media (max-width: 768px) {
            .checkout-container {
                margin: 20px;
                padding: 24px;
            }

            .section-title {
                font-size: 22px;
            }

            .order-details table {
                font-size: 13px;
            }

            .book-img-checkout {
                width: 50px;
                height: 70px;
            }

            .action-buttons {
                flex-direction: column;
            }

            .btn-continue-shopping,
            .btn-place-order {
                width: 100%;
                justify-content: center;
            }
        }

        /* ==================== LOADING ANIMATION ==================== */
        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: 0.5;
            }
        }

        .loading {
            animation: pulse 1.5s ease-in-out infinite;
        }

        /* ==================== 2-COLUMN LAYOUT ==================== */
        .checkout-grid {
            display: grid;
            grid-template-columns: 1fr 420px;
            gap: 32px;
            align-items: start;
        }

        .checkout-main {
            min-width: 0; /* Prevent grid blowout */
        }

        .checkout-sidebar {
            position: sticky;
            top: 20px;
        }

        .order-summary-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-lg);
            padding: 28px;
            border: 2px solid var(--input-border);
        }

        .order-summary-title {
            font-size: 20px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 20px;
            padding-bottom: 16px;
            border-bottom: 2px solid var(--input-border);
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .order-summary-title i {
            color: var(--color-primary);
        }

        .order-item {
            display: flex;
            gap: 12px;
            padding: 16px 0;
            border-bottom: 1px solid #f0f0f0;
        }

        .order-item:last-child {
            border-bottom: none;
        }

        .order-item-img {
            width: 60px;
            height: 85px;
            object-fit: cover;
            border-radius: 6px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }

        .order-item-details {
            flex: 1;
            min-width: 0;
        }

        .order-item-title {
            font-weight: 600;
            font-size: 14px;
            color: var(--text-main);
            margin-bottom: 4px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            line-clamp: 2;
            overflow: hidden;
        }

        .order-item-author {
            font-size: 12px;
            color: var(--text-light);
            font-style: italic;
            margin-bottom: 8px;
        }

        .order-item-quantity {
            font-size: 13px;
            color: var(--text-light);
        }

        .order-item-price {
            font-weight: 600;
            color: var(--text-main);
            font-size: 15px;
            text-align: right;
        }

        .order-summary-totals {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 2px solid var(--input-border);
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            font-size: 14px;
        }

        .summary-row.total {
            font-size: 18px;
            font-weight: 700;
            color: var(--color-primary);
            padding-top: 16px;
            border-top: 2px solid var(--color-primary);
            margin-top: 12px;
        }

        .summary-label {
            color: var(--text-light);
        }

        .summary-value {
            font-weight: 600;
            color: var(--text-main);
        }

        .summary-row.total .summary-label,
        .summary-row.total .summary-value {
            color: var(--color-primary);
        }

        /* Form sections in main column */
        .form-section {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 28px;
            margin-bottom: 24px;
            box-shadow: var(--shadow-md);
            border: 1px solid var(--input-border);
        }

        .form-section-title {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-section-title i {
            color: var(--color-primary);
            font-size: 20px;
        }

        /* Responsive adjustments */
        @media (max-width: 1024px) {
            .checkout-grid {
                grid-template-columns: 1fr;
            }

            .checkout-sidebar {
                position: static;
                order: -1; /* Show summary first on mobile */
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

    <!-- Message Display -->
    <c:if test="${not empty message}">
        <div style="max-width: 1000px; margin: 20px auto 0 auto;">
            <div style="background: #d1e7dd; border-left: 4px solid #198754; border-radius: 8px; padding: 16px; display: flex; align-items: center; gap: 12px;">
                <i class="fas fa-check-circle" style="color: #198754; font-size: 20px;"></i>
                <span style="font-size: 14px; font-weight: 500; color: #198754;">
                    <c:out value="${message}"/>
                </span>
            </div>
        </div>
    </c:if>

    <!-- Error Display -->
    <c:if test="${not empty error}">
        <div style="max-width: 1000px; margin: 20px auto 0 auto;">
            <div style="background: #f8d7da; border-left: 4px solid #dc3545; border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: flex-start; gap: 12px;">
                    <i class="fas fa-exclamation-circle" style="color: #dc3545; font-size: 20px; margin-top: 2px;"></i>
                    <div>
                        <strong style="color: #dc3545; font-size: 15px;">Không thể đặt hàng</strong>
                        <p style="font-size: 14px; color: #721c24; margin: 8px 0 0 0; white-space: pre-line;"><c:out value="${error}"/></p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Stock Warnings -->
    <c:if test="${not empty stockWarnings}">
        <div style="max-width: 1000px; margin: 20px auto 0 auto;">
            <div style="background: #fff3cd; border-left: 4px solid #ffc107; border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: flex-start; gap: 12px;">
                    <i class="fas fa-exclamation-triangle" style="color: #856404; font-size: 20px; margin-top: 2px;"></i>
                    <div style="flex: 1;">
                        <strong style="color: #856404; font-size: 15px;">Cảnh báo tồn kho</strong>
                        <ul style="font-size: 14px; color: #856404; margin: 8px 0 0 0; padding-left: 20px;">
                            <c:forEach var="warning" items="${stockWarnings}">
                                <li><c:out value="${warning}"/></li>
                            </c:forEach>
                        </ul>
                        <p style="font-size: 13px; color: #856404; margin: 12px 0 0 0;">
                            <i class="fas fa-info-circle"></i> Vui lòng quay lại giỏ hàng để điều chỉnh số lượng hoặc tiếp tục đặt hàng với số lượng còn lại.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Price Changes -->
    <c:if test="${not empty priceChanges}">
        <div style="max-width: 1000px; margin: 20px auto 0 auto;">
            <div style="background: #cfe2ff; border-left: 4px solid #0d6efd; border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: flex-start; gap: 12px;">
                    <i class="fas fa-info-circle" style="color: #084298; font-size: 20px; margin-top: 2px;"></i>
                    <div style="flex: 1;">
                        <strong style="color: #084298; font-size: 15px;">Thông báo thay đổi giá</strong>
                        <ul style="font-size: 14px; color: #084298; margin: 8px 0 0 0; padding-left: 20px;">
                            <c:forEach var="change" items="${priceChanges}">
                                <li><c:out value="${change}"/></li>
                            </c:forEach>
                        </ul>
                        <p style="font-size: 13px; color: #084298; margin: 12px 0 0 0;">
                            <i class="fas fa-sync"></i> Giỏ hàng đã được cập nhật với giá mới nhất.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <main class="checkout-container">
        <c:choose>
            <c:when test="${empty cart or empty cart.items}">
                <section class="empty-cart-msg">
                    <i class="fas fa-shopping-cart"></i>
                    <h2>Giỏ hàng của bạn đang trống</h2>
                    <p>Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-place-order" style="display:inline-block; margin-top:20px; text-decoration:none;">
                        <i class="fas fa-shopping-bag"></i> Mua sắm ngay
                    </a>
                </section>
            </c:when>
            <c:otherwise>
                <!-- 2-Column Layout -->
                <div class="checkout-grid">
                    <!-- LEFT COLUMN: Forms -->
                    <section class="checkout-main">
                        <form action="checkout" method="POST" id="checkoutForm">
                            <!-- Address Section -->
                            <section class="form-section">
                                <header class="form-section-title">
                                    <i class="fas fa-map-marker-alt"></i>
                                    Địa chỉ giao hàng
                                </header>
                                
                                <div class="address-list">
                                    <c:choose>
                                        <c:when test="${not empty customerAddresses}">
                                            <c:forEach var="addr" items="${customerAddresses}">
                                                <article class="address-card ${addr.isDefault ? 'default-address' : ''}">
                                                    <label class="address-radio-label">
                                                        <input type="radio" name="selectedAddressId" 
                                                               value="${addr.addressId}" 
                                                               ${addr.isDefault ? 'checked' : ''}
                                                               required>
                                                        <div class="address-content">
                                                            <header class="address-header">
                                                                <strong class="recipient-name">
                                                                    <c:out value="${addr.recipientName != null ? addr.recipientName : user.fullName}"/>
                                                                </strong>
                                                                <c:if test="${addr.isDefault}">
                                                                    <span class="default-badge">Mặc định</span>
                                                                </c:if>
                                                            </header>
                                                            <div class="address-details">
                                                                <p class="phone-number">
                                                                    <i class="fas fa-phone"></i>
                                                                    <c:out value="${addr.phoneNumber != null ? addr.phoneNumber : user.phoneNumber}"/>
                                                                </p>
                                                                <p class="address-text">
                                                                    <i class="fas fa-map-marker-alt"></i>
                                                                    <c:out value="${addr.street}, ${addr.ward}, ${addr.district}, ${addr.province}"/>
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </label>
                                                </article>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="no-address-message">
                                                <i class="fas fa-map-marked-alt"></i>
                                                <p>Bạn chưa có địa chỉ nào. Vui lòng thêm địa chỉ giao hàng.</p>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <button type="button" class="btn-add-address" onclick="openAddressModal()">
                                    <i class="fas fa-plus-circle"></i> Thêm địa chỉ mới
                                </button>
                            </section>

                            <!-- Voucher Section -->
                            <section class="form-section">
                                <header class="form-section-title">
                                    <i class="fas fa-ticket-alt"></i>
                                    Mã giảm giá
                                </header>
                                
                                <div style="display: flex; gap: 10px; align-items: flex-start;">
                                    <div style="flex: 1;">
                                        <input type="text" id="voucherInput" placeholder="Nhập mã giảm giá" 
                                               style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; font-size: 14px;">
                                        <input type="hidden" name="voucherCode" id="voucherCodeHidden">
                                    </div>
                                    <button type="button" id="applyVoucherBtn" onclick="applyVoucher()"
                                            style="padding: 12px 20px; background: #0d6efd; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: 600; white-space: nowrap;">
                                        <i class="fas fa-check"></i> Áp dụng
                                    </button>
                                </div>
                                
                                <div id="voucherMessage" style="margin-top: 10px; font-size: 14px; display: none;"></div>
                            </section>

                            <!-- Payment Section -->
                            <section class="form-section">
                                <header class="form-section-title">
                                    <i class="fas fa-credit-card"></i>
                                    Phương thức thanh toán
                                </header>
                                
                                <select name="paymentMethod" required>
                                    <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                                    <option value="BANK_TRANSFER">Chuyển khoản ngân hàng (VietQR)</option>
                                </select>
                            </section>

                            <!-- Action Buttons -->
                            <footer class="action-buttons">
                                <a href="${pageContext.request.contextPath}/" class="btn-continue-shopping">
                                    <i class="fas fa-arrow-left"></i> Tiếp tục mua sắm
                                </a>
                                <button type="submit" class="btn-place-order">
                                    <i class="fas fa-check-circle"></i> Đặt hàng
                                </button>
                            </footer>
                        </form>
                    </section>

                    <!-- RIGHT COLUMN: Order Summary -->
                    <aside class="checkout-sidebar">
                        <article class="order-summary-card">
                            <header class="order-summary-title">
                                <i class="fas fa-shopping-bag"></i>
                                Đơn hàng của bạn
                            </header>

                            <!-- Order Items -->
                            <section class="order-items-list">
                                <c:forEach var="item" items="${cart.items}">
                                    <c:set var="book" value="${item.book}" />
                                    <c:set var="subtotal" value="${book.price * item.quantity}" />
                                    
                                    <article class="order-item">
                                        <img src="${not empty book.primaryImageUrl ? book.primaryImageUrl : pageContext.request.contextPath.concat('/images/no-image.jpg')}" 
                                             class="order-item-img"
                                             alt="${book.title}"
                                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.jpg';">
                                        
                                        <div class="order-item-details">
                                            <div class="order-item-title">${book.title}</div>
                                            <div class="order-item-author">
                                                <c:forEach var="author" items="${book.authors}" varStatus="auSt">
                                                    ${author.name}${!auSt.last ? ', ' : ''}
                                                </c:forEach>
                                            </div>
                                            <div class="order-item-quantity">
                                                Số lượng: ${item.quantity}
                                            </div>
                                        </div>
                                        
                                        <div class="order-item-price">
                                            <fmt:formatNumber value="${subtotal}" pattern="#,###"/>₫
                                        </div>
                                    </article>
                                </c:forEach>
                            </section>

                            <!-- Order Totals -->
                            <footer class="order-summary-totals">
                                <div class="summary-row">
                                    <span class="summary-label">Tạm tính</span>
                                    <span class="summary-value">
                                        <fmt:formatNumber value="${subtotal}" pattern="#,###"/>₫
                                    </span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">
                                        Phí vận chuyển
                                        <c:if test="${not empty shippingRegion}">
                                            <small style="color: #6c757d; font-weight: normal;">(${shippingRegion})</small>
                                        </c:if>
                                    </span>
                                    <span class="summary-value">
                                        <c:choose>
                                            <c:when test="${shippingFee == 0}">
                                                <span style="color: #28a745; font-weight: 600;">Miễn phí</span>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${shippingFee}" pattern="#,###"/>₫
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                
                                <!-- Free shipping progress -->
                                <c:if test="${freeShippingNeeded > 0}">
                                    <div style="background: #f8f9fa; border-radius: 8px; padding: 12px; margin: 12px 0;">
                                        <div style="font-size: 13px; color: #6c757d; margin-bottom: 8px;">
                                            <i class="fas fa-truck" style="color: #198754;"></i>
                                            Mua thêm <strong style="color: #198754;"><fmt:formatNumber value="${freeShippingNeeded}" pattern="#,###"/>₫</strong> để được <strong style="color: #198754;">MIỄN PHÍ VẬN CHUYỂN</strong>
                                        </div>
                                        <div style="background: #e9ecef; border-radius: 4px; height: 6px; overflow: hidden;">
                                            <c:set var="progress" value="${(subtotal / freeShippingThreshold) * 100}" />
                                            <div style="background: linear-gradient(90deg, #28a745, #20c997); height: 100%; width: ${progress > 100 ? 100 : progress}%; border-radius: 4px;"></div>
                                        </div>
                                    </div>
                                </c:if>
                                
                                <!-- Voucher Discount Row (hidden by default) -->
                                <div id="voucherDiscountRow" class="summary-row" style="display: none; color: #dc3545;">
                                    <span class="summary-label">
                                        <i class="fas fa-tag"></i> Giảm giá
                                        <span id="voucherCodeDisplay" style="font-weight: normal;"></span>
                                    </span>
                                    <span class="summary-value" id="voucherDiscountValue">-0₫</span>
                                </div>
                                
                                <div class="summary-row total" style="margin-top: 12px; padding-top: 12px; border-top: 2px solid #eee;">
                                    <span class="summary-label" style="font-size: 16px;">Tổng cộng</span>
                                    <span class="summary-value" id="grandTotalDisplay" style="font-size: 20px; color: #198754;">
                                        <fmt:formatNumber value="${grandTotal}" pattern="#,###"/>₫
                                    </span>
                                </div>
                            </footer>
                        </article>
                    </aside>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- Include Address Modal -->
    <jsp:include page="/customer/address-modal.jsp"/>

    <jsp:include page="/customer/footer_customer.jsp"/>

    <!-- Voucher JavaScript -->
    <script>
        // Store original values
        var originalSubtotal = ${subtotal != null ? subtotal : 0};
        var originalShippingFee = ${shippingFee != null ? shippingFee : 0};
        var currentDiscount = 0;
        var currentVoucherCode = '';
        
        function formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN').format(amount) + '₫';
        }
        
        function applyVoucher() {
            var code = document.getElementById('voucherInput').value.trim();
            var btn = document.getElementById('applyVoucherBtn');
            var msgDiv = document.getElementById('voucherMessage');
            
            if (!code) {
                showMessage('Vui lòng nhập mã giảm giá', false);
                return;
            }
            
            // Disable button during request
            btn.disabled = true;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang kiểm tra...';
            
            // AJAX request
            var xhr = new XMLHttpRequest();
            xhr.open('POST', '${pageContext.request.contextPath}/api/voucher/validate', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    btn.disabled = false;
                    btn.innerHTML = '<i class="fas fa-check"></i> Áp dụng';
                    
                    if (xhr.status === 200) {
                        try {
                            var response = JSON.parse(xhr.responseText);
                            
                            if (response.valid) {
                                // Success - apply discount
                                currentDiscount = response.discount;
                                currentVoucherCode = response.voucherCode;
                                
                                // Update hidden field
                                document.getElementById('voucherCodeHidden').value = currentVoucherCode;
                                
                                // Show discount row
                                document.getElementById('voucherDiscountRow').style.display = 'flex';
                                document.getElementById('voucherCodeDisplay').textContent = ' (' + currentVoucherCode + ')';
                                document.getElementById('voucherDiscountValue').textContent = '-' + formatCurrency(currentDiscount);
                                
                                // Update total
                                var newTotal = response.newTotal || (originalSubtotal + originalShippingFee - currentDiscount);
                                document.getElementById('grandTotalDisplay').textContent = formatCurrency(newTotal);
                                
                                // Disable input
                                document.getElementById('voucherInput').disabled = true;
                                document.getElementById('voucherInput').style.background = '#e9ecef';
                                btn.innerHTML = '<i class="fas fa-times"></i> Hủy';
                                btn.onclick = removeVoucher;
                                btn.style.background = '#dc3545';
                                
                                showMessage(response.message, true);
                            } else {
                                showMessage(response.message, false);
                            }
                        } catch (e) {
                            showMessage('Có lỗi xảy ra', false);
                        }
                    } else {
                        showMessage('Không thể kết nối server', false);
                    }
                }
            };
            
            xhr.send('code=' + encodeURIComponent(code) + 
                     '&subtotal=' + originalSubtotal + 
                     '&shippingFee=' + originalShippingFee);
        }
        
        function removeVoucher() {
            currentDiscount = 0;
            currentVoucherCode = '';
            
            document.getElementById('voucherCodeHidden').value = '';
            document.getElementById('voucherDiscountRow').style.display = 'none';
            document.getElementById('grandTotalDisplay').textContent = formatCurrency(originalSubtotal + originalShippingFee);
            
            // Re-enable input
            var input = document.getElementById('voucherInput');
            input.disabled = false;
            input.style.background = '';
            input.value = '';
            
            var btn = document.getElementById('applyVoucherBtn');
            btn.innerHTML = '<i class="fas fa-check"></i> Áp dụng';
            btn.onclick = applyVoucher;
            btn.style.background = '#0d6efd';
            
            document.getElementById('voucherMessage').style.display = 'none';
        }
        
        function showMessage(msg, isSuccess) {
            var div = document.getElementById('voucherMessage');
            div.style.display = 'block';
            div.style.color = isSuccess ? '#198754' : '#dc3545';
            div.style.background = isSuccess ? '#d1e7dd' : '#f8d7da';
            div.style.padding = '10px';
            div.style.borderRadius = '6px';
            div.innerHTML = (isSuccess ? '<i class="fas fa-check-circle"></i> ' : '<i class="fas fa-exclamation-circle"></i> ') + msg;
        }
    </script>

</body>
</html>

