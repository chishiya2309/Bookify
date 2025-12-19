<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - Bookify</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            --color-primary: #0D6EFD;
            --color-secondary: #6C757D;
            --color-accent: #FF9900;
            
            /* Màu nền */
            --bg-body: #F8F9FA;
            --bg-white: #FFFFFF;
            
            /* Màu chữ */
            --text-main: #212529;
            --text-light: #6C757D;
            
            /* Màu trạng thái (Function Colors) */
            --color-success: #198754;
            --color-error: #DC3545;
            --color-warning: #FFC107;
            --input-border: #ced4da;
            --input-focus: #86b7fe;
        }

        body {
            background-color: var(--bg-body);
        }

        .checkout-container {
            max-width: 1000px;
            margin: 30px auto;
            background: var(--bg-white);
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .section-title {
            text-align: center;
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 25px;
            position: relative;
        }

        /* Order Review Table */
        .order-review-box {
            border: 1px solid var(--color-secondary);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            background: #fff;
            position: relative;
        }

        .edit-cart-link {
            position: absolute;
            top: -12px;
            right: 20px;
            background: var(--bg-white);
            padding: 0 10px;
            font-weight: bold;
            color: var(--text-main);
            text-decoration: underline;
        }

        .checkout-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .checkout-table th, .checkout-table td {
            border: 2px solid #000; /* Bold borders as per design */
            padding: 12px;
            text-align: center;
            vertical-align: middle;
        }

        .checkout-table th {
            background-color: transparent;
            font-weight: 800;
            text-transform: capitalize;
            border-bottom-width: 2px;
        }

        .book-cell {
            display: flex;
            align-items: center;
            gap: 15px;
            text-align: left;
        }

        .book-img-checkout {
            width: 50px;
            height: 70px;
            object-fit: cover;
            border: 1px solid #ddd;
        }

        .total-row td {
            border-top: 2px solid #000;
            font-weight: bold;
        }
        
        .total-label {
            font-style: italic;
            text-align: right;
            padding-right: 20px;
        }

        /* Shipping Form */
        .shipping-section {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-bottom: 30px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 150px 350px;
            gap: 15px;
            align-items: center;
        }

        .form-label {
            text-align: right;
            font-weight: 500;
            color: var(--text-main);
        }

        .form-input {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid var(--input-border);
            border-radius: 4px;
            font-size: 14px;
            transition: border-color 0.2s;
        }

        .form-input:focus {
            outline: none;
            border-color: var(--color-primary);
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.25);
        }
        
        .info-note {
            margin-left: 15px;
            font-size: 13px;
            color: #ccc;
            font-style: italic;
            grid-column: 2;
            margin-top: -10px;
            margin-bottom: 10px;
        }

        /* Payment Section */
        .payment-section {
            text-align: center;
            margin-bottom: 40px;
        }

        .payment-select {
            padding: 8px 12px;
            font-size: 16px;
            border: 2px solid #000;
            border-radius: 4px;
            font-weight: 600;
            width: 200px;
        }

        /* Actions */
        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
            align-items: center;
        }

        .btn-place-order {
            background-color: var(--bg-white);
            color: var(--text-main);
            border: 2px solid #000;
            padding: 10px 30px;
            font-size: 18px;
            font-weight: bold;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-place-order:hover {
            background-color: #000;
            color: #fff;
        }

        .btn-continue {
            color: var(--text-main);
            text-decoration: underline;
            font-size: 16px;
            font-weight: 500;
        }
        
        .empty-cart-msg {
            text-align: center;
            padding: 50px;
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

    <div class="checkout-container">
        <c:choose>
            <c:when test="${empty cart or empty cart.items}">
                <div class="empty-cart-msg">
                    <h2>Giỏ hàng của bạn đang trống</h2>
                    <p>Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-place-order" style="display:inline-block; margin-top:20px; text-decoration:none;">Mua sắm ngay</a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- 1. Order Details -->
                <div class="section-title">Chi tiết đơn hàng</div>
                
                <div class="order-review-box">
                    <a href="${pageContext.request.contextPath}/customer/cart" class="edit-cart-link">Chỉnh sửa</a>
                    
                    <table class="checkout-table">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Sách</th>
                                <th>Tác giả</th>
                                <th>Giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cart.items}" varStatus="status">
                                <c:set var="book" value="${item.book}" />
                                <c:set var="subtotal" value="${book.price * item.quantity}" />
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td>
                                        <div class="book-cell">
                                            <img src="${not empty book.primaryImageUrl ? book.primaryImageUrl : pageContext.request.contextPath.concat('/images/no-image.jpg')}" 
                                                 class="book-img-checkout"
                                                 alt="${book.title}"
                                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/no-image.jpg';">
                                            <span>${book.title}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <c:forEach var="author" items="${book.authors}" varStatus="auSt">
                                            ${author.name}${!auSt.last ? ', ' : ''}
                                        </c:forEach>
                                    </td>
                                    <td><fmt:formatNumber value="${book.price}" pattern="#,###"/>₫</td>
                                    <td>${item.quantity}</td>
                                    <td><fmt:formatNumber value="${subtotal}" pattern="#,###"/>₫</td>
                                </tr>
                            </c:forEach>
                            <tr class="total-row">
                                <td colspan="4" style="border:none;"></td>
                                <td class="total-label">TỔNG:</td>
                                <td style="font-size: 1.2em;">${cart.totalItems}</td>
                                <td style="font-size: 1.2em; color: var(--color-main);">
                                    <fmt:formatNumber value="${cart.totalAmount}" pattern="#,###"/>₫
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- 2. Shipping Info -->
                <form action="checkout" method="POST" id="checkoutForm">
                    <div class="section-title">Thông tin giao hàng</div>
                    
                    <div class="shipping-section">
                        <div class="form-grid">
                            <label class="form-label">Người nhận:</label>
                            <input type="text" name="fullName" class="form-input" value="${user.fullName}" required>
                            
                            <label class="form-label">Số điện thoại:</label>
                            <input type="tel" name="phoneNumber" class="form-input" value="${user.phoneNumber}" required>
                            
                            <label class="form-label">Địa chỉ:</label>
                            <input type="text" name="streetLine" class="form-input" value="${user.address}" required>
                            
                            <label class="form-label">Xã/Phường:</label>
                            <input type="text" name="ward" class="form-input" required>

                            <label class="form-label">Quận/Huyện:</label>
                            <input type="text" name="district" class="form-input" required>
                            
                            <label class="form-label">Tỉnh/Thành phố:</label>
                            <input type="text" name="province" class="form-input" required>
                            
                            <label class="form-label">Mã bưu điện:</label>
                            <input type="text" name="zipCode" class="form-input">
                            
                            <label class="form-label">Quốc gia:</label>
                            <input type="text" name="country" class="form-input" value="Vietnam">
                        </div>
                        <div class="form-grid" style="margin-top: 10px;">
                            <div></div>
                            <div class="info-note">Mặc định tải từ thông tin hồ sơ khách hàng</div>
                        </div>
                    </div>

                    <!-- 3. Payment -->
                    <div class="section-title">Thanh toán</div>
                    <div class="payment-section">
                        <label style="font-weight: 500; margin-right: 10px;">Phương thức thanh toán:</label>
                        <select name="paymentMethod" class="payment-select">
                            <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                            <option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>
                            <option value="CREDIT_CARD">Thẻ tín dụng</option>
                            <option value="MOMO">Momo</option>
                            <option value="SEPAY">SEPAY</option>
                            <option value="VNPAY">VNPAY</option>
                        </select>
                    </div>

                    <!-- 4. Submit -->
                    <div class="action-buttons">
                        <button type="submit" class="btn-place-order">Đặt hàng</button>
                        <a href="${pageContext.request.contextPath}/" class="btn-continue">Tiếp tục mua sắm</a>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="/customer/footer_customer.jsp"/>

</body>
</html>
