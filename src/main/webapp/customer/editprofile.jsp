<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa thông tin - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customer-profile.css" type="text/css"/>
</head>
<body>
    <nav style="padding: 20px; text-align: center; background: #f8f9fa;">
        <a href="${pageContext.request.contextPath}/customer/profile" style="color: #0D6EFD; text-decoration: none;">← Quay lại thông tin cá nhân</a>
    </nav>
    
    <main class="profile-container">
        <c:if test="${not empty error}">
            <aside class="alert alert-danger">${error}</aside>
        </c:if>
        
        <article class="profile-card">
            <header class="profile-header">
                <figure class="profile-avatar">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
                        <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                    </svg>
                </figure>
                <h1 class="profile-name">Chỉnh sửa thông tin</h1>
            </header>
            
            <section class="profile-body">
                <form method="post" action="${pageContext.request.contextPath}/customer/profile">
                    <input type="hidden" name="action" value="update">
                    
                    <fieldset>
                        <legend class="section-title">Thông tin cơ bản</legend>
                        
                        <label for="email">Email</label>
                        <input type="email" id="email" value="${customer.email}" readonly>
                        <small>Email không thể thay đổi</small>
                        
                        <label for="fullName">Họ và tên</label>
                        <input type="text" id="fullName" name="fullName" value="<c:out value='${customer.fullName}'/>" required>
                        
                        <label for="phoneNumber">Số điện thoại</label>
                        <input type="tel" id="phoneNumber" name="phoneNumber" value="${customer.phoneNumber}" required pattern="^(\+84|0)[0-9]{9}$">
                        <small>Bắt đầu bằng 0 hoặc +84, theo sau là 9 chữ số</small>
                    </fieldset>
                    
                    <fieldset>
                        <legend class="section-title">Đổi mật khẩu (tùy chọn)</legend>
                        
                        <label for="password">Mật khẩu mới</label>
                        <input type="password" id="password" name="password" placeholder="Để trống nếu không đổi" minlength="6">
                        <small>Ít nhất 6 ký tự</small>
                        
                        <label for="confirmPassword">Xác nhận mật khẩu</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu mới">
                    </fieldset>
                    
                    <footer class="profile-actions">
                        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        <a href="${pageContext.request.contextPath}/customer/profile" class="btn btn-secondary">Hủy</a>
                    </footer>
                </form>
            </section>
        </article>
    </main>
</body>
</html>
