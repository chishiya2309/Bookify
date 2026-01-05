<%@ page pageEncoding="UTF-8" %>
<%-- Header Admin - included file --%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<header class="admin-header">

    <div class="admin-logo">
        <a href="${pageContext.request.contextPath}/admin/">
            <h1>LOGO BOOK STORE</h1>
        </a>
    </div>

    <div class="admin-welcome">
        Xin chào,
        <span class="email">
            <%= session.getAttribute("userEmail") != null ? session.getAttribute("userEmail") : "Admin" %>
        </span>
        <a href="${pageContext.request.contextPath}/auth/logout" class="logout-btn">Đăng xuất</a>
    </div>

    <nav class="admin-menu">
        <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
        <a href="${pageContext.request.contextPath}/admin/books">Books</a>
        <a href="${pageContext.request.contextPath}/admin/customers">Customers</a>
        <a href="${pageContext.request.contextPath}/admin/reviews">Reviews</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/publishers">Publishers</a>
        <a href="${pageContext.request.contextPath}/admin/authors">Authors</a>
        <a href="${pageContext.request.contextPath}/admin/vouchers">Vouchers</a>
        <a href="${pageContext.request.contextPath}/admin/metrics">Thống kê</a>
        <a href="${pageContext.request.contextPath}/admin/user">Admin</a>
    </nav>
</header>
