
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">

<div class="header-container">

    <!-- TOP AREA -->
    <div class="header-top">

        <!-- LOGO -->
        <div class="logo">LOGO</div>

        <!-- SEARCH BAR -->
        <div class="search-bar">
            <input type="text" placeholder="Search books...">
            <button>Search</button>
        </div>

        <!-- USER MENU -->
        <div class="user-links">
            Welcome, <strong><%= session.getAttribute("customerName") %></strong> |
            <a href="/orders">My Orders</a> |
            <a href="/logout">Sign Out</a> |
            <a href="/cart">Cart</a>
        </div>
    </div>

    <!-- BOTTOM CATEGORIES -->
    <div class="categories">
        <a href="#">Business</a> |
        <a href="#">Health</a> |
        <a href="#">Marketing</a> |
        <a href="#">Programming</a> |
        <a href="#">Technology</a> |
        <a href="#">Lifestyle</a> |
        <a href="#">History</a>
    </div>

</div>
