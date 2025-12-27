<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - Bookify</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
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
        }
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: var(--bg-body);
            min-height: 100vh;
            padding: 20px;
        }
        main {
            max-width: 1200px;
            margin: 0 auto;
        }
        article {
            background: var(--bg-white);
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        header {
            background: var(--color-primary);
            color: white;
            padding: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }
        h1 {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 28px;
            font-weight: bold;
            margin: 0;
        }
        .cart-page-badge {
            background: rgba(255,255,255,0.2);
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 14px;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s;
            text-decoration: none;
        }
        .btn-clear {
            background: rgba(255,255,255,0.2);
            color: white;
            border: 2px solid white;
        }
        .btn-clear:hover {
            background: white;
            color: var(--color-error);
        }
        .btn-primary {
            background: var(--color-primary);
            color: white;
        }
        .btn-primary:hover {
            background: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(13, 110, 253, 0.3);
        }
        .btn-success {
            background: var(--color-success);
            color: white;
        }
        .btn-success:hover {
            background: #157347;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(25, 135, 84, 0.3);
        }
        .btn-outline {
            background: white;
            color: var(--color-primary);
            border: 2px solid var(--color-primary);
        }
        .btn-outline:hover {
            background: var(--color-primary);
            color: white;
        }
        section.empty-cart {
            text-align: center;
            padding: 100px 20px;
            color: var(--text-light);
        }
        section.empty-cart i {
            font-size: 80px;
            opacity: 0.3;
            margin-bottom: 24px;
            color: var(--color-primary);
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        thead {
            background: var(--bg-body);
        }
        th {
            padding: 16px 12px;
            text-align: left;
            font-weight: 600;
            color: var(--text-main);
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        th.text-center, td.text-center {
            text-align: center;
        }
        th.text-right, td.text-right {
            text-align: right;
        }
        tbody tr {
            border-bottom: 1px solid #dee2e6;
            transition: all 0.3s;
        }
        tbody tr:hover {
            background: var(--bg-body);
            transform: scale(1.005);
        }
        td {
            padding: 20px 12px;
        }
        figure.book-info {
            display: flex;
            align-items: center;
            gap: 16px;
            margin: 0;
        }
        figure.book-info img {
            width: 70px;
            height: 90px;
            object-fit: cover;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            transition: transform 0.3s;
        }
        figure.book-info img:hover {
            transform: scale(1.1);
        }
        figcaption {
            font-weight: 500;
            color: var(--text-main);
        }
        .book-meta {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 4px;
        }
        .quantity-input {
            width: 80px;
            padding: 8px;
            text-align: center;
            border: 2px solid #dee2e6;
            border-radius: 6px;
            font-size: 16px;
            transition: border 0.3s;
        }
        .quantity-input:focus {
            outline: none;
            border-color: var(--color-primary);
        }
        .btn-remove {
            background: none;
            color: var(--color-error);
            border: none;
            cursor: pointer;
            padding: 8px 16px;
            border-radius: 6px;
            transition: all 0.3s;
        }
        .btn-remove:hover {
            background: #f8d7da;
            transform: scale(1.05);
        }
        tfoot {
            background: var(--bg-body);
            font-weight: bold;
        }
        tfoot td {
            padding: 20px 12px;
        }
        .total-badge {
            display: inline-block;
            padding: 8px 20px;
            background: var(--color-primary);
            color: white;
            border-radius: 25px;
            font-size: 14px;
            font-weight: 600;
        }
        .total-amount {
            font-size: 24px;
            color: var(--color-success);
            font-weight: 700;
        }
        footer.cart-actions {
            padding: 24px;
            background: var(--bg-body);
            border-top: 2px solid #dee2e6;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }
        nav.action-buttons {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }
        .price-tag {
            font-weight: 600;
            color: var(--text-main);
        }
        .stock-badge {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
        }
        .stock-badge.in-stock {
            background: #d1e7dd;
            color: var(--color-success);
        }
        .stock-badge.low-stock {
            background: #fff3cd;
            color: #997404;
        }
        /* ===== RESPONSIVE STYLES ===== */
        @media (max-width: 992px) {
            body {
                padding: 12px;
            }
            .cart-page-badge {
                font-size: 12px;
                padding: 3px 10px;
            }
        }

        @media (max-width: 768px) {
            body {
                padding: 8px;
            }
            header {
                flex-direction: column;
                text-align: center;
                padding: 16px;
            }
            h1 {
                font-size: 22px;
                flex-wrap: wrap;
                justify-content: center;
            }

            /* Table responsive - chuyển thành card layout */
            table, thead, tbody, th, td, tr {
                display: block;
            }
            thead {
                display: none; /* Ẩn header trên mobile */
            }
            tbody tr {
                margin-bottom: 16px;
                border: 1px solid #dee2e6;
                border-radius: 12px;
                padding: 16px;
                background: var(--bg-white);
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            }
            tbody tr:hover {
                transform: none;
            }
            td {
                padding: 8px 0;
                text-align: left !important;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #f0f0f0;
            }
            td:last-child {
                border-bottom: none;
            }
            td::before {
                content: attr(data-label);
                font-weight: 600;
                color: var(--text-light);
                font-size: 12px;
                text-transform: uppercase;
                min-width: 100px;
            }
            td:first-child {
                display: none; /* Ẩn số thứ tự */
            }

            /* Book info trên mobile */
            figure.book-info {
                flex-direction: row;
                text-align: left;
                width: 100%;
            }
            figure.book-info img {
                width: 60px;
                height: 80px;
            }
            figcaption {
                font-size: 14px;
            }

            /* Quantity input */
            .quantity-input {
                width: 70px;
                padding: 10px;
                font-size: 16px; /* Prevent zoom on iOS */
            }

            /* Footer buttons */
            footer.cart-actions {
                flex-direction: column;
                padding: 16px;
                gap: 12px;
            }
            footer.cart-actions > .btn {
                width: 100%;
                justify-content: center;
            }
            nav.action-buttons {
                width: 100%;
                flex-direction: column;
            }
            nav.action-buttons .btn {
                width: 100%;
                justify-content: center;
                padding: 14px 20px;
            }

            /* Tfoot - mobile layout */
            tfoot {
                display: block;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                border-radius: 12px;
                margin: 16px;
                padding: 20px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            }
            tfoot tr {
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
                align-items: center;
                gap: 16px;
            }
            tfoot td {
                display: none !important;
                padding: 0;
                border: none;
            }
            /* Hiển thị các cell cần thiết */
            tfoot td.tfoot-badge,
            tfoot td.tfoot-label,
            tfoot td.tfoot-amount {
                display: block !important;
            }
            /* Badge - full width ở trên */
            tfoot td.tfoot-badge {
                width: 100%;
                text-align: center !important;
                order: 1;
                margin-bottom: 8px;
            }
            /* Label TỔNG */
            tfoot td.tfoot-label {
                order: 2;
                font-size: 18px;
                text-align: left !important;
            }
            /* Số tiền */
            tfoot td.tfoot-amount {
                order: 3;
                text-align: right !important;
                margin-left: auto;
            }
            .total-badge {
                font-size: 13px;
                padding: 8px 16px;
            }
            .total-amount {
                font-size: 26px;
                color: var(--color-success);
            }

            /* Notifications */
            article[style*="margin-bottom: 20px"] {
                margin-left: -8px;
                margin-right: -8px;
                border-radius: 0;
                border-left: none !important;
                border-top: 4px solid;
            }
        }

        @media (max-width: 480px) {
            h1 {
                font-size: 18px;
            }
            .btn {
                padding: 10px 16px;
                font-size: 13px;
            }
            figure.book-info img {
                width: 50px;
                height: 65px;
            }
            figcaption {
                font-size: 13px;
            }
            .book-meta {
                font-size: 11px;
            }
            .price-tag {
                font-size: 14px;
            }
            .total-amount {
                font-size: 18px;
            }

            /* Toast trên mobile nhỏ */
            .toast {
                margin: 0 -8px 16px -8px !important;
                border-radius: 0 !important;
            }
        }

        /* ===== Toast Notification Styles ===== */
        .toast {
            position: relative;
            animation: slideIn 0.4s ease-out;
            overflow: hidden;
        }
        .toast.hiding {
            animation: slideOut 0.3s ease-in forwards;
        }
        .toast-progress {
            position: absolute;
            bottom: 0;
            left: 0;
            height: 3px;
            background: rgba(0,0,0,0.15);
            animation: progress 5s linear forwards;
        }
        .toast-close {
            background: none;
            border: none;
            cursor: pointer;
            padding: 4px 8px;
            margin-left: auto;
            opacity: 0.6;
            transition: opacity 0.2s, transform 0.2s;
            font-size: 18px;
        }
        .toast-close:hover {
            opacity: 1;
            transform: scale(1.1);
        }
        @keyframes slideIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes slideOut {
            from { opacity: 1; transform: translateY(0); }
            to { opacity: 0; transform: translateY(-20px); }
        }
        @keyframes progress {
            from { width: 100%; }
            to { width: 0%; }
        }
    </style>
</head>
<body>
    <%-- Header: Hiển thị theo trạng thái đăng nhập --%>
    <c:choose>
        <c:when test="${isGuest}">
            <jsp:include page="/customer/header_sign_in.jsp"/>
        </c:when>
        <c:otherwise>
            <jsp:include page="/customer/header_customer.jsp"/>
        </c:otherwise>
    </c:choose>

    <main>    
        <!-- Thông báo thành công -->
        <c:if test="${not empty successMessage}">
            <article id="successToast" class="toast" style="margin-bottom: 20px; background: #d1e7dd; border-left: 4px solid var(--color-success); border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: center; gap: 12px;">
                    <i class="fas fa-check-circle" style="color: var(--color-success); font-size: 20px;"></i>
                    <span style="font-size: 14px; font-weight: 500; color: var(--color-success); flex: 1;">
                        <c:out value="${successMessage}"/>
                    </span>
                    <button type="button" class="toast-close" onclick="closeToast('successToast')" title="Đóng">
                        <i class="fas fa-times" style="color: var(--color-success);"></i>
                    </button>
                </div>
                <div class="toast-progress" style="background: var(--color-success);"></div>
            </article>
        </c:if>
        
        <!-- Thông báo gộp giỏ hàng -->
        <c:if test="${not empty mergeMessage}">
            <article id="mergeToast" class="toast" style="margin-bottom: 20px; background: #fff3cd; border-left: 4px solid var(--color-warning); border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: start; gap: 12px;">
                    <i class="fas fa-info-circle" style="color: var(--color-warning); font-size: 20px; margin-top: 2px;"></i>
                    <section style="flex: 1;">
                        <h2 style="font-size: 16px; font-weight: 600; color: var(--text-main); margin-bottom: 8px;">
                            Gộp giỏ hàng thành công
                        </h2>
                        <div style="font-size: 14px; color: var(--text-main); line-height: 1.6; white-space: pre-line;">
                            <c:out value="${mergeMessage}"/>
                        </div>
                    </section>
                    <button type="button" class="toast-close" onclick="closeToast('mergeToast')" title="Đóng">
                        <i class="fas fa-times" style="color: var(--color-warning);"></i>
                    </button>
                </div>
                <div class="toast-progress" style="background: var(--color-warning);"></div>
            </article>
        </c:if>
        
        <!-- Thông báo cho khách hàng chưa đăng nhập -->
        <c:if test="${isGuest and not empty cart and not empty cart.items}">
            <article style="margin-bottom: 20px; background: #cfe2ff; border-left: 4px solid var(--color-primary); border-radius: 8px; padding: 16px;">
                <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px;">
                    <section style="display: flex; align-items: start; gap: 12px;">
                        <i class="fas fa-user-clock" style="color: var(--color-primary); font-size: 20px; margin-top: 2px;"></i>
                        <div>
                            <h2 style="font-size: 16px; font-weight: 600; color: var(--text-main); margin-bottom: 4px;">
                                Đang mua với tư cách khách
                            </h2>
                            <p style="font-size: 14px; color: var(--text-main); margin: 0;">
                                Giỏ hàng của bạn được lưu tạm thời. Vui lòng đăng nhập hoặc đăng ký để lưu vĩnh viễn.
                            </p>
                        </div>
                    </section>
                    <a href="#" onclick="goToLogin()" class="btn btn-primary" style="white-space: nowrap;">
                        <i class="fas fa-sign-in-alt"></i>
                        Đăng nhập / Đăng ký
                    </a>
                    <script>
                        function goToLogin() {
                            var currentUrl = window.location.pathname + window.location.search;
                            window.location.href = '${pageContext.request.contextPath}/customer/login.jsp?redirect=' + encodeURIComponent(currentUrl);
                        }
                    </script>
                </div>
            </article>
        </c:if>
        
        <article>
            <header>
                <h1>
                    <i class="fas fa-shopping-cart"></i>
                    Giỏ hàng của bạn
                    <c:if test="${not empty cart and not empty cart.items}">
                        <span class="cart-page-badge">${cart.totalItems} sản phẩm</span>
                    </c:if>
                </h1>
                <c:if test="${not empty cart and not empty cart.items}">
                    <form action="cart" method="post">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="btn btn-clear" 
                                onclick="return confirm('Bạn có chắc là muốn xoá giỏ hàng của bạn?')">
                            <i class="fas fa-trash-alt"></i>
                            Xoá giỏ hàng
                        </button>
                    </form>
                </c:if>
            </header>

            <c:choose>
                <c:when test="${empty cart or empty cart.items}">
                    <section class="empty-cart">
                        <i class="fas fa-shopping-cart"></i>
                        <p style="font-size: 20px; margin-bottom: 8px; font-weight: 600; color: var(--text-main);">Giỏ hàng trống</p>
                        <p style="margin-bottom: 24px; color: var(--text-light);">Hãy thêm sách vào giỏ hàng để bắt đầu!</p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                            <i class="fas fa-book"></i>
                            Xem sách
                        </a>
                    </section>
                </c:when>
                <c:otherwise>
                    <form action="cart" method="post" id="cartForm">
                        <input type="hidden" name="action" value="update">
                        <table>
                            <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th>Thông tin sách</th>
                                    <th class="text-center">Số lượng</th>
                                    <th class="text-right">Đơn giá</th>
                                    <th class="text-right">Thành tiền</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${cart.items}" varStatus="status">
                                    <c:set var="book" value="${item.book}" />
                                    <c:set var="subtotal" value="${book.price * item.quantity}" />
                                    <tr>
                                        <td class="text-center" data-label="#">
                                            <strong>${status.index + 1}</strong>
                                        </td>
                                        <td data-label="">
                                            <figure class="book-info">
                                                <img src="${not empty book.primaryImageUrl ? book.primaryImageUrl : pageContext.request.contextPath.concat('/images/no-image.jpg')}" 
                                                     alt="<c:out value='${book.title}'/>"
                                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.jpg';">
                                                <div>
                                                    <figcaption><c:out value="${book.title}"/></figcaption>
                                                    <div class="book-meta">
                                                        <c:if test="${not empty book.authors}">
                                                            <div>
                                                                <i class="fas fa-user-edit"></i>
                                                                <c:forEach var="author" items="${book.authors}" varStatus="authorStatus">
                                                                    <c:out value="${author.name}"/><c:if test="${!authorStatus.last}">, </c:if>
                                                                </c:forEach>
                                                            </div>
                                                        </c:if>
                                                        <c:choose>
                                                            <c:when test="${book.quantityInStock > 10}">
                                                                <span class="stock-badge in-stock">Còn hàng</span>
                                                            </c:when>
                                                            <c:when test="${book.quantityInStock > 0}">
                                                                <span class="stock-badge low-stock">Còn ${book.quantityInStock}</span>
                                                            </c:when>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </figure>
                                        </td>
                                        <td class="text-center" data-label="Số lượng">
                                            <c:choose>
                                                <c:when test="${not empty item.cartItemId}">
                                                    <input type="number" 
                                                           name="quantity_${item.cartItemId}" 
                                                           value="${item.quantity}" 
                                                           min="1" 
                                                           max="${book.quantityInStock}"
                                                           class="quantity-input"
                                                           inputmode="numeric">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="number" 
                                                           name="quantity_book_${book.bookId}" 
                                                           value="${item.quantity}" 
                                                           min="1" 
                                                           max="${book.quantityInStock}"
                                                           class="quantity-input"
                                                           inputmode="numeric">
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-right" data-label="Đơn giá">
                                            <data value="${book.price}" class="price-tag">
                                                <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
                                            </data>
                                        </td>
                                        <td class="text-right" data-label="Thành tiền">
                                            <strong>
                                                <data value="${subtotal}" class="price-tag">
                                                    <fmt:formatNumber value="${subtotal}" pattern="#,###"/>₫
                                                </data>
                                            </strong>
                                        </td>
                                        <td class="text-center" data-label="">
                                            <button type="button" class="btn-remove" 
                                                    onclick="removeItem(${not empty item.cartItemId ? item.cartItemId : 0}, ${book.bookId})"
                                                    title="Xoá khỏi giỏ hàng">
                                                <i class="fas fa-times"></i> Xoá
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="2"></td>
                                    <td class="text-center tfoot-badge">
                                        <output class="total-badge">
                                            <i class="fas fa-box"></i>
                                            ${cart.totalItems} sản phẩm
                                        </output>
                                    </td>
                                    <td class="text-right tfoot-label">
                                        <strong>TỔNG:</strong>
                                    </td>
                                    <td class="text-right tfoot-amount">
                                        <output class="total-amount">
                                            <data value="${cart.totalAmount}">
                                                <fmt:formatNumber value="${cart.totalAmount}" pattern="#,###"/>₫
                                            </data>
                                        </output>
                                    </td>
                                    <td></td>
                                </tr>
                            </tfoot>
                        </table>

                        <footer class="cart-actions">
                            <button type="submit" class="btn btn-outline">
                                <i class="fas fa-sync-alt"></i>
                                Cập nhật giỏ hàng
                            </button>
                            <nav class="action-buttons">
                                <a href="${pageContext.request.contextPath}/" class="btn btn-outline">
                                    <i class="fas fa-arrow-left"></i>
                                    Tiếp tục mua sắm
                                </a>
                                <a href="checkout" class="btn btn-success">
                                    <i class="fas fa-lock"></i>
                                    Tiến hành thanh toán
                                    <i class="fas fa-arrow-right"></i>
                                </a>
                            </nav>
                        </footer>
                    </form>
                </c:otherwise>
            </c:choose>
        </article>
    </main>

    <script>
        // Toast notification functions
        function closeToast(toastId) {
            const toast = document.getElementById(toastId);
            if (toast) {
                toast.classList.add('hiding');
                setTimeout(() => toast.remove(), 300);
            }
        }
        
        // Auto-dismiss success toast after 5 seconds
        (function() {
            const successToast = document.getElementById('successToast');
            if (successToast) {
                // Pause animation on hover
                successToast.addEventListener('mouseenter', function() {
                    const progress = this.querySelector('.toast-progress');
                    if (progress) progress.style.animationPlayState = 'paused';
                });
                successToast.addEventListener('mouseleave', function() {
                    const progress = this.querySelector('.toast-progress');
                    if (progress) progress.style.animationPlayState = 'running';
                });
                
                // Auto close after 5 seconds
                setTimeout(() => closeToast('successToast'), 5000);
            }
        })();
        
        function removeItem(itemId, bookId) {
            if (confirm('Bạn có chắc muốn xoá sản phẩm này khỏi giỏ hàng?')) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'cart';
                
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'remove';
                
                // Gửi cả itemId và bookId để server có thể xử lý đúng
                // Guest cart sẽ dùng bookId, user cart sẽ dùng itemId
                const itemIdInput = document.createElement('input');
                itemIdInput.type = 'hidden';
                itemIdInput.name = 'itemId';
                itemIdInput.value = itemId;
                
                const bookIdInput = document.createElement('input');
                bookIdInput.type = 'hidden';
                bookIdInput.name = 'bookId';
                bookIdInput.value = bookId;
                
                form.appendChild(actionInput);
                form.appendChild(itemIdInput);
                form.appendChild(bookIdInput);
                document.body.appendChild(form);
                form.submit();
            }
        }

        // Validate quantity before submit
        document.getElementById('cartForm')?.addEventListener('submit', function(e) {
            const inputs = this.querySelectorAll('.quantity-input');
            let valid = true;
            
            inputs.forEach(input => {
                const value = parseInt(input.value);
                const max = parseInt(input.max);
                
                if (value < 1 || value > max) {
                    valid = false;
                    input.style.borderColor = '#dc3545';
                }
            });
            
            if (!valid) {
                e.preventDefault();
                alert('Vui lòng kiểm tra số lượng. Một số sản phẩm vượt quá số lượng trong kho.');
                return false;
            }
        });
        
        // Highlight invalid inputs
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('input', function() {
                const value = parseInt(this.value);
                const max = parseInt(this.max);
                
                if (value < 1 || value > max) {
                    this.style.borderColor = 'var(--color-error)';
                } else {
                    this.style.borderColor = '#dee2e6';
                }
            });
        });
    </script>

    <%-- Footer --%>
    <jsp:include page="/customer/footer_customer.jsp"/>
</body>
</html>