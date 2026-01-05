<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<div class="header-container">
    <div class="header-top">
        <div class="logo" style="display: flex; align-items: center; justify-content: center; padding: 8px 0;">
            <a href="${pageContext.request.contextPath}/" style="display: inline-block;">
                <img src="https://res.cloudinary.com/dbqaczv3a/image/upload/v1765890230/Screenshot_2025-12-16_200154_yclv14.png"
                     alt="Bookify"
                     style="height: 64px; object-fit: contain; display: block;">
            </a>
        </div>

        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/search_book" method="get">
                <input type="text" name="keyword" placeholder="Tìm kiếm sách..." required />
                <button type="submit">Tìm kiếm</button>
            </form>
        </div>

        <div class="user-links">
            <span>Xin chào, <strong><c:out value="${userName}" default="Khách"/></strong></span>
            <span>|</span>
            <a href="${pageContext.request.contextPath}/customer/orders">Đơn hàng</a> |
            <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a> |
            
            <!-- Cart with Mini-Cart Dropdown -->
            <div class="cart-wrapper">
                <a href="${pageContext.request.contextPath}/customer/cart" class="cart-link">
                    <i class="fas fa-shopping-cart"></i> Giỏ hàng
                    <span id="cartBadge" class="cart-badge" style="display: none;">0</span>
                </a>
                <div class="mini-cart-dropdown" id="miniCartDropdown">
                    <div class="mini-cart-header">Giỏ hàng của bạn</div>
                    <div class="mini-cart-items" id="miniCartItems">
                        <div class="mini-cart-loading">
                            <i class="fas fa-spinner fa-spin"></i> Đang tải...
                        </div>
                    </div>
                    <div class="mini-cart-footer" id="miniCartFooter" style="display: none;">
                        <div class="mini-cart-total">
                            Tổng: <strong id="miniCartTotal">0₫</strong>
                        </div>
                        <a href="${pageContext.request.contextPath}/customer/cart" class="btn-view-cart">
                            Xem giỏ hàng
                        </a>
                        <a href="${pageContext.request.contextPath}/customer/checkout" class="btn-checkout">
                            Thanh toán
                        </a>
                    </div>
                </div>
            </div> |
            
            <a href="${pageContext.request.contextPath}/customer/profile" class="user-profile-link">
                <span class="avatar">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                    </svg>
                </span>
                <c:out value="${userName}" default="Khách"/>
            </a>
        </div>
        <style>
            .user-profile-link {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 4px 8px;
                border-radius: 16px;
                transition: background-color 0.3s, color 0.3s;
            }
            .user-profile-link:hover {
                background-color: #0D6EFD;
                color: #fff !important;
            }
            .user-profile-link .avatar {
                width: 24px;
                height: 24px;
                border-radius: 50%;
                background-color: #6C757D;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                color: #fff;
                transition: background-color 0.3s;
            }
            .user-profile-link:hover .avatar {
                background-color: #fff;
                color: #0D6EFD;
            }
            
            /* Cart Wrapper & Mini-Cart */
            .cart-wrapper {
                position: relative;
                display: inline-block;
            }
            .cart-link {
                position: relative;
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 8px 4px;
            }
            .cart-badge {
                position: absolute;
                top: -2px;
                right: -10px;
                background: #dc3545;
                color: white;
                font-size: 11px;
                font-weight: 600;
                min-width: 18px;
                height: 18px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 0 4px;
            }
            .mini-cart-dropdown {
                position: absolute;
                top: 100%;
                right: 0;
                width: 340px;
                background: white;
                border-radius: 8px;
                box-shadow: 0 8px 30px rgba(0,0,0,0.15);
                z-index: 1000;
                opacity: 0;
                visibility: hidden;
                transform: translateY(10px);
                transition: all 0.2s ease;
                pointer-events: none;
            }
            /* Show dropdown on hover with smooth transition */
            .cart-wrapper:hover .mini-cart-dropdown {
                opacity: 1;
                visibility: visible;
                transform: translateY(0);
                pointer-events: auto;
            }
            /* Invisible bridge to prevent hover gap */
            .cart-wrapper::after {
                content: '';
                position: absolute;
                top: 100%;
                left: 0;
                right: -20px;
                height: 15px;
                background: transparent;
            }
            .mini-cart-header {
                padding: 12px 16px;
                font-weight: 600;
                border-bottom: 1px solid #eee;
                color: #333;
            }
            .mini-cart-items {
                max-height: 280px;
                overflow-y: auto;
            }
            .mini-cart-item {
                display: flex;
                gap: 12px;
                padding: 12px 16px;
                border-bottom: 1px solid #f5f5f5;
            }
            .mini-cart-item img {
                width: 50px;
                height: 70px;
                object-fit: cover;
                border-radius: 4px;
            }
            .mini-cart-item-info {
                flex: 1;
            }
            .mini-cart-item-title {
                font-size: 13px;
                color: #333;
                margin-bottom: 4px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
            }
            .mini-cart-item-price {
                font-size: 13px;
                color: #dc3545;
                font-weight: 600;
            }
            .mini-cart-item-qty {
                font-size: 12px;
                color: #666;
            }
            .mini-cart-empty {
                padding: 30px;
                text-align: center;
                color: #999;
            }
            .mini-cart-empty i {
                font-size: 40px;
                margin-bottom: 10px;
                opacity: 0.3;
            }
            .mini-cart-loading {
                padding: 30px;
                text-align: center;
                color: #666;
            }
            .mini-cart-footer {
                padding: 12px 16px;
                border-top: 1px solid #eee;
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                align-items: center;
            }
            .mini-cart-total {
                width: 100%;
                text-align: right;
                font-size: 15px;
                margin-bottom: 8px;
            }
            .mini-cart-total strong {
                color: #dc3545;
                font-size: 16px;
            }
            .btn-view-cart, .btn-checkout {
                flex: 1;
                text-align: center;
                padding: 8px 12px;
                border-radius: 6px;
                font-size: 13px;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.2s;
            }
            .btn-view-cart {
                background: #f8f9fa;
                color: #333;
                border: 1px solid #ddd;
            }
            .btn-view-cart:hover {
                background: #e9ecef;
            }
            .btn-checkout {
                background: #0D6EFD;
                color: white;
            }
            .btn-checkout:hover {
                background: #0b5ed7;
            }
        </style>
    </div>

    <div class="categories">
        <c:choose>
            <c:when test="${not empty listCategories}">
                <c:forEach items="${listCategories}" var="category" varStatus="status">
                    
                    <a href="${pageContext.request.contextPath}/view_category?id=${category.categoryId}">
                        <c:out value="${category.name}"/>
                    </a>
                
                    <c:if test="${!status.last}"> | </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/">Trang chủ</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Mini-Cart JavaScript -->
<script>
const headerContextPath = '${pageContext.request.contextPath}';
let miniCartLoaded = false;

// Load cart count on page load
document.addEventListener('DOMContentLoaded', function() {
    updateCartBadge();
});

// Update cart badge with count
function updateCartBadge() {
    fetch(headerContextPath + '/api/cart/count')
        .then(res => res.json())
        .then(data => {
            const badge = document.getElementById('cartBadge');
            if (badge && data.success) {
                if (data.count > 0) {
                    badge.textContent = data.count > 99 ? '99+' : data.count;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(err => console.log('Failed to load cart count'));
}

// Load mini-cart items on hover
const cartWrapper = document.querySelector('.cart-wrapper');
if (cartWrapper) {
    cartWrapper.addEventListener('mouseenter', function() {
        if (!miniCartLoaded) {
            loadMiniCartItems();
        }
    });
}

function loadMiniCartItems() {
    const itemsContainer = document.getElementById('miniCartItems');
    const footer = document.getElementById('miniCartFooter');
    
    fetch(headerContextPath + '/api/cart/items')
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                if (data.items && data.items.length > 0) {
                    // Clear container
                    itemsContainer.innerHTML = '';
                    
                    // Build DOM elements safely
                    data.items.forEach(function(item) {
                        // Create main item container
                        const itemDiv = document.createElement('div');
                        itemDiv.className = 'mini-cart-item';
                        
                        // Create and configure image
                        const img = document.createElement('img');
                        const fallbackImg = 'https://placehold.co/50x70/e9ecef/6c757d?text=No+Image';
                        let imgUrl = item.imageUrl;
                        
                        // Check if imageUrl is valid
                        if (!imgUrl || imgUrl === 'null' || imgUrl.trim() === '') {
                            imgUrl = fallbackImg;
                        } else if (imgUrl.startsWith('/')) {
                            imgUrl = headerContextPath + imgUrl;
                        } else if (!imgUrl.match(/^https?:\/\//i)) {
                            imgUrl = fallbackImg;
                        }
                        
                        img.setAttribute('src', imgUrl);
                        img.setAttribute('alt', item.title || '');
                        img.onerror = function() {
                            if (this.src !== fallbackImg) {
                                this.src = fallbackImg;
                            }
                        };
                        
                        // Create info container
                        const infoDiv = document.createElement('div');
                        infoDiv.className = 'mini-cart-item-info';
                        
                        // Create title
                        const titleDiv = document.createElement('div');
                        titleDiv.className = 'mini-cart-item-title';
                        titleDiv.textContent = item.title; // Safe: textContent escapes HTML
                        
                        // Create price
                        const priceDiv = document.createElement('div');
                        priceDiv.className = 'mini-cart-item-price';
                        priceDiv.textContent = formatCartCurrency(item.price);
                        
                        // Create quantity
                        const qtyDiv = document.createElement('div');
                        qtyDiv.className = 'mini-cart-item-qty';
                        qtyDiv.textContent = 'Số lượng: ' + item.quantity;
                        
                        // Assemble the DOM tree
                        infoDiv.appendChild(titleDiv);
                        infoDiv.appendChild(priceDiv);
                        infoDiv.appendChild(qtyDiv);
                        
                        itemDiv.appendChild(img);
                        itemDiv.appendChild(infoDiv);
                        
                        itemsContainer.appendChild(itemDiv);
                    });
                    
                    document.getElementById('miniCartTotal').textContent = formatCartCurrency(data.subtotal);
                    footer.style.display = 'flex';
                } else {
                    itemsContainer.innerHTML = '<div class="mini-cart-empty"><i class="fas fa-shopping-cart"></i><p>Giỏ hàng trống</p></div>';
                    footer.style.display = 'none';
                }
                miniCartLoaded = true;
            }
        })
        .catch(err => {
            itemsContainer.innerHTML = '<div class="mini-cart-empty">Không thể tải giỏ hàng</div>';
        });
}

function formatCartCurrency(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + '₫';
}

// Global function to refresh mini-cart after adding items
function refreshMiniCart() {
    miniCartLoaded = false;
    updateCartBadge();
}
</script>
