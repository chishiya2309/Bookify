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
            <a href="${pageContext.request.contextPath}/customer/orders.jsp">Đơn hàng</a> |
            <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a> |
            <a href="${pageContext.request.contextPath}/customer/cart">Giỏ hàng</a> |
            <a href="${pageContext.request.contextPath}/customer/profile" class="user-profile-link">
                <span class="avatar">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                    </svg>
                </span>
                <c:out value="${userEmail}" default="Khách"/>
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
        </style>
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
