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
        <a href="${pageContext.request.contextPath}/customer/cart">Giỏ hàng</a>
    </div>

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

<script>
// Thêm redirect parameter vào link đăng nhập với URL hiện tại từ browser
(function() {
    var loginLink = document.getElementById('loginLink');
    if (loginLink) {
        var currentPath = window.location.pathname + window.location.search;
        var contextPath = '${pageContext.request.contextPath}';
        var redirectPath = currentPath;

        // Đảm bảo redirectPath bao gồm contextPath khi cần, để tương thích với cấu hình không ở root context
        if (contextPath && redirectPath.indexOf(contextPath) !== 0) {
            redirectPath = contextPath + (redirectPath.charAt(0) === '/' ? '' : '/') + redirectPath;
        }
        
        // Chỉ thêm redirect nếu không phải trang chủ hoặc trang login/register
        if (redirectPath !== contextPath + '/' && 
            redirectPath !== contextPath && 
            redirectPath.indexOf('/login') === -1 && 
            redirectPath.indexOf('/register') === -1) {
            loginLink.href = contextPath + '/customer/login.jsp?redirect=' + encodeURIComponent(redirectPath);
        }
    }
})();
</script>
