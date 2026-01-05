<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử đơn hàng - Bookify</title>
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
        .orders-container {
            max-width: 1000px;
            margin: 40px auto;
            padding: 0 20px;
        }

        /* ==================== PAGE HEADER ==================== */
        .page-header {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 32px;
            margin-bottom: 24px;
            box-shadow: var(--shadow-md);
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .page-title i {
            color: var(--color-primary);
        }

        .page-subtitle {
            font-size: 15px;
            color: var(--text-light);
        }

        /* ==================== ORDER CARD ==================== */
        .order-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 24px;
            margin-bottom: 16px;
            box-shadow: var(--shadow-sm);
            transition: var(--transition);
            border: 1px solid transparent;
        }

        .order-card:hover {
            box-shadow: var(--shadow-md);
            border-color: var(--color-primary);
            transform: translateY(-2px);
        }

        .order-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 16px;
            padding-bottom: 16px;
            border-bottom: 1px solid var(--input-border);
            flex-wrap: wrap;
            gap: 12px;
        }

        .order-id {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-main);
        }

        .order-date {
            font-size: 13px;
            color: var(--text-light);
            margin-top: 4px;
        }

        /* ==================== STATUS BADGES ==================== */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.3px;
        }

        .status-pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-processing {
            background: #cce5ff;
            color: #004085;
        }

        .status-shipped {
            background: #d1ecf1;
            color: #0c5460;
        }

        .status-delivered {
            background: #d4edda;
            color: #155724;
        }

        .status-cancelled {
            background: #f8d7da;
            color: #721c24;
        }

        /* ==================== ORDER INFO ==================== */
        .order-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 16px;
            margin-bottom: 16px;
        }

        .info-item {
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .info-label {
            font-size: 12px;
            color: var(--text-light);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .info-value {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-main);
        }

        .info-value.price {
            color: var(--color-primary);
        }


        .btn-detail {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 20px;
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            transition: var(--transition);
            cursor: pointer;
        }

        .btn-detail:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.4);
        }

        .btn-delivered {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 10px 20px;
            background: linear-gradient(135deg, var(--color-success) 0%, #146c43 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            transition: var(--transition);
            cursor: pointer;
        }

        .btn-delivered:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(25, 135, 84, 0.4);
        }

        .order-footer {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            padding-top: 16px;
            border-top: 1px solid var(--input-border);
            flex-wrap: wrap;
        }

        /* ==================== EMPTY STATE ==================== */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            background: var(--bg-white);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-md);
        }

        .empty-icon {
            font-size: 80px;
            color: var(--input-border);
            margin-bottom: 24px;
        }

        .empty-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 12px;
        }

        .empty-message {
            font-size: 16px;
            color: var(--text-light);
            margin-bottom: 32px;
        }

        .btn-shop {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            padding: 14px 32px;
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            transition: var(--transition);
        }

        .btn-shop:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4);
        }

        /* ==================== RESPONSIVE ==================== */
        @media (max-width: 768px) {
            .orders-container {
                margin: 20px auto;
            }

            .page-header {
                padding: 24px;
            }

            .page-title {
                font-size: 22px;
            }

            .order-card {
                padding: 20px;
            }

            .order-header {
                flex-direction: column;
            }

            .order-info {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 480px) {
            .order-info {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

<jsp:include page="/customer/header_customer.jsp" />

<main class="orders-container">
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

    <!-- Page Header -->
    <header class="page-header">
        <h1 class="page-title">
            <i class="fas fa-box-open"></i>
            Lịch sử đơn hàng
        </h1>
        <p class="page-subtitle">Theo dõi và quản lý các đơn hàng của bạn</p>
    </header>

    <c:choose>
        <c:when test="${empty orders}">
            <!-- Empty State -->
            <section class="empty-state">
                <i class="fas fa-shopping-bag empty-icon"></i>
                <h2 class="empty-title">Chưa có đơn hàng nào</h2>
                <p class="empty-message">Bạn chưa đặt đơn hàng nào. Hãy khám phá các sách hay tại Bookify!</p>
                <a href="${pageContext.request.contextPath}/" class="btn-shop">
                    <i class="fas fa-book"></i>
                    Mua sắm ngay
                </a>
            </section>
        </c:when>
        <c:otherwise>
            <!-- Order List -->
            <c:forEach items="${orders}" var="o" varStatus="st">
                <article class="order-card">
                    <header class="order-header">
                        <div>
                            <div class="order-id">Đơn hàng #${o.orderId}</div>
                            <div class="order-date">
                                <i class="far fa-calendar-alt"></i>
                                <c:set var="orderDateTime" value="${o.orderDate.toString()}"/>
                                ${orderDateTime.substring(8, 10)}/${orderDateTime.substring(5, 7)}/${orderDateTime.substring(0, 4)} 
                                ${orderDateTime.substring(11, 16)}
                            </div>
                        </div>
                        <c:choose>
                            <c:when test="${o.orderStatus == 'PENDING'}">
                                <span class="status-badge status-pending">
                                    <i class="fas fa-clock"></i> Chờ xử lý
                                </span>
                            </c:when>
                            <c:when test="${o.orderStatus == 'PROCESSING'}">
                                <span class="status-badge status-processing">
                                    <i class="fas fa-cog"></i> Đang xử lý
                                </span>
                            </c:when>
                            <c:when test="${o.orderStatus == 'SHIPPED'}">
                                <span class="status-badge status-shipped">
                                    <i class="fas fa-truck"></i> Đang giao
                                </span>
                            </c:when>
                            <c:when test="${o.orderStatus == 'DELIVERED'}">
                                <span class="status-badge status-delivered">
                                    <i class="fas fa-check-circle"></i> Đã giao
                                </span>
                            </c:when>
                            <c:when test="${o.orderStatus == 'CANCELLED'}">
                                <span class="status-badge status-cancelled">
                                    <i class="fas fa-times-circle"></i> Đã hủy
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge">${o.orderStatus}</span>
                            </c:otherwise>
                        </c:choose>
                    </header>

                    <div class="order-info">
                        <div class="info-item">
                            <span class="info-label">Số lượng sách</span>
                            <span class="info-value">${o.totalQuantity} cuốn</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Tổng tiền</span>
                            <span class="info-value price">
                                <fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Thanh toán</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${o.paymentMethod == 'COD'}">Khi nhận hàng</c:when>
                                    <c:when test="${o.paymentMethod == 'BANK_TRANSFER'}">Chuyển khoản</c:when>
                                    <c:otherwise>${o.paymentMethod}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Người nhận</span>
                            <span class="info-value">${o.recipientName}</span>
                        </div>
                    </div>

                    <footer class="order-footer">
                        <c:if test="${o.orderStatus == 'SHIPPED'}">
                            <form method="post" action="${pageContext.request.contextPath}/customer/orders" style="display: inline;">
                                <input type="hidden" name="action" value="markDelivered">
                                <input type="hidden" name="orderId" value="${o.orderId}">
                                <button type="submit" class="btn-delivered" onclick="return confirm('Bạn có chắc chắn đã nhận được hàng?');">
                                    <i class="fas fa-check-circle"></i>
                                    Đã nhận được hàng
                                </button>
                            </form>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/customer/orders?action=detail&id=${o.orderId}" class="btn-detail">
                            <i class="fas fa-eye"></i>
                            Xem chi tiết
                        </a>
                    </footer>
                </article>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</main>

<jsp:include page="/customer/footer_customer.jsp" />

</body>
</html>
