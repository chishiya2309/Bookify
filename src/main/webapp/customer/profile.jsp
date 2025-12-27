<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin cá nhân - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customer-profile.css" type="text/css"/>
</head>
<body>
    <nav style="padding: 20px; text-align: center; background: #f8f9fa;">
        <a href="${pageContext.request.contextPath}/" style="color: #0D6EFD; text-decoration: none;">← Quay lại trang chủ</a>
    </nav>
    
    <main class="profile-container">
        <c:if test="${not empty sessionScope.success}">
            <aside class="alert alert-success">${sessionScope.success}</aside>
            <c:remove var="success" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.error}">
            <aside class="alert alert-danger">${sessionScope.error}</aside>
            <c:remove var="error" scope="session"/>
        </c:if>
        
        <article class="profile-card">
            <header class="profile-header">
                <figure class="profile-avatar">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
                        <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                    </svg>
                </figure>
                <h1 class="profile-name"><c:out value="${customer.fullName}"/></h1>
                <p class="profile-email">${customer.email}</p>
            </header>
            
            <section class="profile-body">
                <h2 class="section-title">Thông tin cá nhân</h2>
                
                <dl class="info-list">
                    <dt>Email</dt>
                    <dd>${customer.email}</dd>
                    
                    <dt>Họ và tên</dt>
                    <dd><c:out value="${customer.fullName}"/></dd>
                    
                    <dt>Số điện thoại</dt>
                    <dd>${customer.phoneNumber}</dd>
                    
                    <dt>Ngày đăng ký</dt>
                    <dd>${customer.registerDate}</dd>
                </dl>
                
                <footer class="profile-actions">
                    <a href="${pageContext.request.contextPath}/customer/editprofile.jsp" class="btn btn-primary">Chỉnh sửa thông tin</a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Quay lại trang chủ</a>
                </footer>
            </section>
        </article>
    </main>
</body>
</html>
