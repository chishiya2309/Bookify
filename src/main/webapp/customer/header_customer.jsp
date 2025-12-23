<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<div class="header-container">

    <div class="header-top">

        <div class="logo">
            <a href="${pageContext.request.contextPath}/">
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
            <span>Xin chào, <strong><c:out value="${userEmail}" default="Khách"/></strong></span>
            <span>|</span>
            <a href="${pageContext.request.contextPath}/customer/orders.jsp">Đơn hàng</a>
            <span>|</span>
            <a href="${pageContext.request.contextPath}/auth/logout">Đăng xuất</a>
            <span>|</span>
            <a href="${pageContext.request.contextPath}/customer/cart">Giỏ hàng</a>
        </div>
    </div>

    <div class="categories">
        <c:choose>
            <c:when test="${not empty listCategories}">
                <c:forEach items="${listCategories}" var="category" varStatus="status">
                    
                    <a href="${pageContext.request.contextPath}/view_category?id=${category.categoryId}">
                        ${category.name}
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