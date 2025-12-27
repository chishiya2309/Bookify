<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<div class="header-container">
    <div class="logo-box" style="display: flex; align-items: center; justify-content: center; padding: 8px 0;">
        <a href="${pageContext.request.contextPath}/" style="display: inline-block;">
            <img src="https://res.cloudinary.com/dbqaczv3a/image/upload/v1765890230/Screenshot_2025-12-16_200154_yclv14.png"
                 alt="Bookify"
                 style="height: 64px; object-fit: contain; display: block;">
        </a>
    </div>

    <div class="search-bar">
        <input type="text" class="search-input" placeholder="Tìm kiếm sách..." />
        <button class="search-btn">Tìm kiếm</button>
    </div>

    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/customer/login.jsp" id="loginLink">Đăng nhập</a> |
        <a href="${pageContext.request.contextPath}/customer/register.jsp">Đăng ký</a> |
        
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
                    <a href="${pageContext.request.contextPath}/customer/login.jsp?redirect=checkout" class="btn-checkout">
                        Thanh toán
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <style>
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
        /* Show dropdown on hover with better hitbox */
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

    <div class="category-bar">
        <c:choose>
            <c:when test="${not empty listCategories}">
                <c:forEach items="${listCategories}" var="category" varStatus="status">
                    <a href="${pageContext.request.contextPath}/books?category=${category.categoryId}">${category.name}</a>
                    <c:if test="${!status.last}"> | </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/books">Tất cả sách</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Mini-Cart JavaScript -->
<script>
var headerContextPath = '${pageContext.request.contextPath}';
var miniCartLoaded = false;

// Load cart count on page load
document.addEventListener('DOMContentLoaded', function() {
    updateCartBadge();
    
    // Login link redirect
    var loginLink = document.getElementById('loginLink');
    if (loginLink) {
        var currentPath = window.location.pathname + window.location.search;
        var contextPath = headerContextPath;
        if (currentPath !== contextPath + '/' &&
            currentPath !== contextPath &&
            currentPath.indexOf('/login') === -1 &&
            currentPath.indexOf('/register') === -1) {
            loginLink.href = contextPath + '/customer/login.jsp?redirect=' + encodeURIComponent(currentPath);
        }
    }
});

// Update cart badge with count
function updateCartBadge() {
    fetch(headerContextPath + '/api/cart/count')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var badge = document.getElementById('cartBadge');
            if (badge && data.success) {
                if (data.count > 0) {
                    badge.textContent = data.count > 99 ? '99+' : data.count;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }
        })
        .catch(function(err) { console.log('Failed to load cart count'); });
}

// Load mini-cart items on hover
var cartWrapper = document.querySelector('.cart-wrapper');
if (cartWrapper) {
    cartWrapper.addEventListener('mouseenter', function() {
        if (!miniCartLoaded) {
            loadMiniCartItems();
        }
    });
}

function loadMiniCartItems() {
    var itemsContainer = document.getElementById('miniCartItems');
    var footer = document.getElementById('miniCartFooter');
    
    fetch(headerContextPath + '/api/cart/items')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                if (data.items && data.items.length > 0) {
                    // Clear container
                    itemsContainer.innerHTML = '';
                    
                    // Build DOM elements safely
                    data.items.forEach(function(item) {
                        // Create main item container
                        var itemDiv = document.createElement('div');
                        itemDiv.className = 'mini-cart-item';
                        
                        // Create and configure image
                        var img = document.createElement('img');
                        var imgUrl = item.imageUrl.startsWith('/') ? headerContextPath + item.imageUrl : item.imageUrl;
                        img.src = imgUrl;
                        img.alt = item.title; // textContent not needed for alt attribute - browser handles it
                        img.onerror = function() {
                            this.src = headerContextPath + '/images/no-image.jpg';
                        };
                        
                        // Create info container
                        var infoDiv = document.createElement('div');
                        infoDiv.className = 'mini-cart-item-info';
                        
                        // Create title
                        var titleDiv = document.createElement('div');
                        titleDiv.className = 'mini-cart-item-title';
                        titleDiv.textContent = item.title; // Safe: textContent escapes HTML
                        
                        // Create price
                        var priceDiv = document.createElement('div');
                        priceDiv.className = 'mini-cart-item-price';
                        priceDiv.textContent = formatCartCurrency(item.price);
                        
                        // Create quantity
                        var qtyDiv = document.createElement('div');
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
        .catch(function(err) {
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
