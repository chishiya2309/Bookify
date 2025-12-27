<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">

<header class="admin-header">

    <div class="admin-logo">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">
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
        <a href="${pageContext.request.contextPath}/admin/users.jsp">Users</a>
        <a href="${pageContext.request.contextPath}/admin/categories.jsp">Categories</a>
        <a href="${pageContext.request.contextPath}/admin/books">Books</a>
        <a href="${pageContext.request.contextPath}/admin/customers.jsp">Customers</a>
        <a href="${pageContext.request.contextPath}/admin/reviews.jsp">Reviews</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/publishers">Publishers</a>
    </nav>

</header>
