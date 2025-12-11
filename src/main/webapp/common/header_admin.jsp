<%@ page contentType="text/html; charset=UTF-8" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">

<header class="admin-header">

    <div class="admin-logo">
        <h1>LOGO BOOK STORE</h1>
    </div>

    <div class="admin-welcome">
        Welcome,
        <span class="email">
            <%= session.getAttribute("userEmail") != null ? session.getAttribute("userEmail") : "Guest" %>
        </span>
        <a href="logout" class="logout-btn">Logout</a>
    </div>

    <nav class="admin-menu">
        <a href="users">Users</a>
        <a href="categories">Categories</a>
        <a href="books">Books</a>
        <a href="customers">Customers</a>
        <a href="reviews">Reviews</a>
        <a href="orders">Orders</a>
    </nav>

</header>
