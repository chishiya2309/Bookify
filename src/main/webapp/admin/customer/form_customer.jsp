<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${isEdit}">Sửa Khách hàng</c:when><c:otherwise>Thêm Khách hàng</c:otherwise></c:choose> - Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" type="text/css"/>
</head>
<body>
    <%@ include file="../header_admin.jsp" %>
    
    <main class="admin-content-narrow">
        <header>
            <h2 class="page-title-lg"><c:choose><c:when test="${isEdit}">Sửa thông tin Khách hàng</c:when><c:otherwise>Thêm Khách hàng mới</c:otherwise></c:choose></h2>
        </header>
        
        <c:if test="${not empty error}">
            <aside class="alert alert-danger" role="alert">${error}</aside>
        </c:if>
        
        <form id="customerForm" method="post" action="${pageContext.request.contextPath}/admin/customers" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${customer.userId}">
            </c:if>
            
            <fieldset>
                <legend>Thông tin đăng nhập</legend>
                <section class="form-group">
                    <label for="email">Email <span class="required">*</span></label>
                    <c:choose>
                        <c:when test="${isEdit}">
                            <input type="email" id="email" name="email" value="${customer.email}" readonly>
                            <small class="help-text">Email không thể thay đổi</small>
                        </c:when>
                        <c:otherwise>
                            <input type="email" id="email" name="email" value="${not empty email ? email : ''}" placeholder="Nhập địa chỉ email" required>
                        </c:otherwise>
                    </c:choose>
                    <span class="error-text" id="emailError"></span>
                </section>
                
                <section class="form-row">
                    <article class="form-group">
                        <label for="password">Mật khẩu <c:if test="${!isEdit}"><span class="required">*</span></c:if></label>
                        <input type="password" id="password" name="password" placeholder="${isEdit ? 'Để trống nếu không đổi' : 'Ít nhất 6 ký tự'}" <c:if test="${!isEdit}">required</c:if>>
                        <span class="error-text" id="passwordError"></span>
                    </article>
                    <article class="form-group">
                        <label for="confirmPassword">Xác nhận mật khẩu <c:if test="${!isEdit}"><span class="required">*</span></c:if></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu" <c:if test="${!isEdit}">required</c:if>>
                        <span class="error-text" id="confirmPasswordError"></span>
                    </article>
                </section>
            </fieldset>
            
            <fieldset>
                <legend>Thông tin cá nhân</legend>
                <section class="form-row">
                    <article class="form-group">
                        <label for="fullName">Họ và Tên <span class="required">*</span></label>
                        <c:set var="fullNameValue" value="${not empty fullName ? fullName : (not empty customer ? customer.fullName : '')}"/>
                        <input type="text" id="fullName" name="fullName" value="<c:out value='${fullNameValue}'/>" placeholder="Nhập họ và tên" required>
                        <span class="error-text" id="fullNameError"></span>
                    </article>
                    <article class="form-group">
                        <label for="phoneNumber">Số điện thoại <span class="required">*</span></label>
                        <input type="tel" id="phoneNumber" name="phoneNumber" value="${not empty phoneNumber ? phoneNumber : (not empty customer ? customer.phoneNumber : '')}" placeholder="VD: 0912345678" required>
                        <small class="help-text">Bắt đầu bằng 0 hoặc +84, theo sau là 9 chữ số</small>
                        <span class="error-text" id="phoneNumberError"></span>
                    </article>
                </section>
            </fieldset>
            
            <footer class="form-actions">
                <button type="submit" class="btn btn-success btn-lg"><c:choose><c:when test="${isEdit}">Cập nhật</c:when><c:otherwise>Thêm Khách hàng</c:otherwise></c:choose></button>
                <a href="${pageContext.request.contextPath}/admin/customers?action=list" class="btn btn-secondary btn-lg">Hủy</a>
            </footer>
        </form>
    </main>
    
    <%@ include file="../footer_admin.jsp" %>
    
    <script>
        function validateForm() {
            var isValid = true;
            var isEditMode = document.querySelector('input[name="action"]').value === 'update';
            
            document.querySelectorAll('.error-text').forEach(function(el) { el.style.display = 'none'; });
            document.querySelectorAll('input.error').forEach(function(el) { el.classList.remove('error'); });
            
            if (!isEditMode) {
                var email = document.getElementById('email');
                if (!email.value.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
                    document.getElementById('emailError').textContent = 'Email không hợp lệ';
                    document.getElementById('emailError').style.display = 'block';
                    email.classList.add('error');
                    isValid = false;
                }
            }
            
            var password = document.getElementById('password');
            var confirmPassword = document.getElementById('confirmPassword');
            
            if ((!isEditMode && !password.value) || (password.value && password.value.length < 6)) {
                document.getElementById('passwordError').textContent = 'Mật khẩu phải có ít nhất 6 ký tự';
                document.getElementById('passwordError').style.display = 'block';
                password.classList.add('error');
                isValid = false;
            }
            
            if (password.value && confirmPassword && password.value !== confirmPassword.value) {
                document.getElementById('confirmPasswordError').textContent = 'Mật khẩu không khớp';
                document.getElementById('confirmPasswordError').style.display = 'block';
                confirmPassword.classList.add('error');
                isValid = false;
            }
            
            var fullName = document.getElementById('fullName');
            if (!fullName.value.trim()) {
                document.getElementById('fullNameError').textContent = 'Vui lòng nhập họ tên';
                document.getElementById('fullNameError').style.display = 'block';
                fullName.classList.add('error');
                isValid = false;
            }
            
            var phoneNumber = document.getElementById('phoneNumber');
            if (!phoneNumber.value.trim() || !/^(\+84|0)[0-9]{9}$/.test(phoneNumber.value.trim())) {
                document.getElementById('phoneNumberError').textContent = 'Số điện thoại không hợp lệ';
                document.getElementById('phoneNumberError').style.display = 'block';
                phoneNumber.classList.add('error');
                isValid = false;
            }
            
            return isValid;
        }
    </script>
</body>
</html>
