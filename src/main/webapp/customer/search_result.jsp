<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Kết quả tìm kiếm - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .btn-add-cart {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 10px 16px;
            background: #ffc107;
            color: #333;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            transition: all 0.2s;
        }
        .btn-add-cart:hover {
            background: #e0a800;
            transform: translateY(-1px);
        }
        .btn-add-cart:disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }
        .cart-toast {
            animation: slideIn 0.3s ease;
        }
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    </style>
</head>
<body>
    <%-- Hiển thị header phù hợp với trạng thái đăng nhập --%>
    <c:choose>
        <c:when test="${isLoggedIn}">
            <jsp:include page="/customer/header_customer.jsp"></jsp:include>
        </c:when>
        <c:otherwise>
            <jsp:include page="/customer/header_sign_in.jsp"></jsp:include>
        </c:otherwise>
    </c:choose>

    <div class="container">
        
        <div class="search-header">
            <c:if test="${not empty keyword}">
                <h2>Kết quả tìm kiếm cho <span class="highlight">"${keyword}"</span></h2>
            </c:if>
            <c:if test="${empty keyword}">
                <h2>Kết quả tìm kiếm</h2>
            </c:if>
        </div>

        <div class="search-list">
            <c:forEach items="${listResult}" var="book">
                <div class="search-item">
                    <div class="search-item-img">
                        <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">
                            <c:choose>
                                <%-- Trường hợp 1: Có đường dẫn ảnh --%>
                                <c:when test="${not empty book.primaryImageUrl}">
                                    <img src="${book.primaryImageUrl}" alt="${book.title}"
                                         style="width: 120px; height: 180px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;"
                                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/book_icon.png';" />
                                </c:when>
                                
                                <%-- Trường hợp 2: Không có ảnh -> Dùng ảnh mặc định --%>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/book_icon.png" alt="${book.title}"
                                         style="width: 120px; height: 180px; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;" />
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </div>

                    <div class="search-item-info">
                        <div class="item-title">
                            <a href="${pageContext.request.contextPath}/view_book?id=${book.bookId}">${book.title}</a>
                        </div>
                        
                        <div class="item-rating">★★★★☆</div> 
                        
                        <div class="item-author">
                            Tác giả: 
                            <c:forEach items="${book.authors}" var="author" varStatus="status">
                                <c:out value="${author.name}" /><c:out value="${!status.last ? ',' : ''}" />
                            </c:forEach>
                        </div>
                        
                        <div class="item-desc">
                            <c:if test="${not empty book.description}">
                                <c:choose>
                                    <c:when test="${book.description.length() > 150}">
                                        <c:out value="${book.description.substring(0, 150)}..." />
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${book.description}" />
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            
                            <c:if test="${empty book.description}">
                                <span style="font-style: italic; color: #999;">Chưa có mô tả cho sách này.</span>
                            </c:if>
                        </div>
                    </div>

                    <div class="search-item-action">
                        <div class="item-price">
                            <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
                        </div>
                        <button class="btn-add-cart" onclick="addToCart(${book.bookId}, this)">
                            <i class="fas fa-cart-plus"></i> Thêm vào giỏ
                        </button>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty listResult}">
                <div class="empty-msg">
                    <p>Không tìm thấy sách phù hợp với tiêu chí của bạn.</p>
                    <a href="${pageContext.request.contextPath}/" style="color: var(--color-primary);">Quay về trang chủ</a>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="/customer/footer_customer.jsp"></jsp:include>

<script>
const contextPath = '${pageContext.request.contextPath}';

function addToCart(bookId, btn) {
    // Disable button during request
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang thêm...';
    }
    
    // AJAX POST to CartApiServlet
    fetch(contextPath + '/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: 'bookId=' + bookId + '&quantity=1'
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            // Show success toast
            showToast(data.message, 'success');
            
            // Update cart badge in header
            if (typeof refreshMiniCart === 'function') {
                refreshMiniCart();
            }
            if (typeof updateCartBadge === 'function') {
                updateCartBadge();
            }
        } else {
            showToast(data.error || 'Không thể thêm vào giỏ hàng', 'error');
        }
    })
    .catch(err => {
        console.error('Add to cart error:', err);
        showToast('Đã xảy ra lỗi', 'error');
    })
    .finally(() => {
        // Re-enable button
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = '<i class="fas fa-cart-plus"></i> Thêm vào giỏ';
        }
    });
}

// Toast notification
function showToast(message, type) {
    // Remove existing toast
    const existingToast = document.querySelector('.cart-toast');
    if (existingToast) existingToast.remove();
    
    const toast = document.createElement('div');
    toast.className = 'cart-toast ' + type;
    toast.innerHTML = (type === 'success' ? '<i class="fas fa-check-circle"></i> ' : '<i class="fas fa-exclamation-circle"></i> ') + message;
    toast.style.cssText = 'position: fixed; top: 80px; right: 20px; padding: 14px 20px; border-radius: 8px; z-index: 10000; color: white; font-weight: 500; box-shadow: 0 4px 20px rgba(0,0,0,0.2); animation: slideIn 0.3s ease;';
    toast.style.background = type === 'success' ? '#28a745' : '#dc3545';
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
</script>
</body>
</html>