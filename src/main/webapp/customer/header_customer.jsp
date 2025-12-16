<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<div class="header-container">

    <!-- TOP AREA -->
    <div class="header-top">

        <!-- LOGO -->
        <div class="logo" style="display: flex; align-items: center; justify-content: center; padding: 8px 0;">
            <a href="${pageContext.request.contextPath}/" style="display: inline-block;">
                <img src="https://res.cloudinary.com/dbqaczv3a/image/upload/v1765890230/Screenshot_2025-12-16_200154_yclv14.png"
                     alt="Bookify"
                     style="height: 64px; object-fit: contain; display: block;">
            </a>
        </div>

        <!-- SEARCH BAR -->
        <div class="search-bar">
            <input type="text" placeholder="Tìm kiếm sách...">
            <button>Tìm kiếm</button>
        </div>

        <!-- USER MENU -->
        <div class="user-links">
            Xin chào, <strong><c:out value="${userEmail}" default="Khách"/></strong> |
            <a href="${pageContext.request.contextPath}/customer/orders.jsp">Đơn hàng</a> |
            <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a> |
            <a href="${pageContext.request.contextPath}/customer/cart">Giỏ hàng</a>
        </div>
    </div>

    <!-- BOTTOM CATEGORIES -->
    <div class="categories">
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
