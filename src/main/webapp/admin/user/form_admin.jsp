<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${isEdit}">Sửa Admin</c:when>
            <c:otherwise>Thêm Admin</c:otherwise>
        </c:choose>
        - Bookify
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css" type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css" type="text/css"/>
</head>
<body>
    <%@ include file="../header_admin.jsp" %>
    
    <main class="admin-content-narrow">
        <header>
            <h2 class="page-title-lg"><c:choose><c:when test="${isEdit}">Sửa thông tin Admin</c:when><c:otherwise>Thêm Admin mới</c:otherwise></c:choose></h2>
        </header>
        
        <c:if test="${not empty error}">
            <aside class="alert alert-danger" role="alert">${error}</aside>
        </c:if>
        
        <form id="adminForm" method="post" action="${pageContext.request.contextPath}/admin/user" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}">
                <input type="hidden" name="id" value="${admin.userId}">
            </c:if>
            
            <fieldset>
                <legend>Thông tin đăng nhập</legend>
                <section class="form-group">
                    <label for="email">Email <span class="required">*</span></label>
                    <input type="email" id="email" name="email" 
                           value="${not empty email ? email : (not empty admin ? admin.email : '')}"
                           placeholder="Nhập địa chỉ email" required>
                    <span class="error-text" id="emailError"></span>
                </section>
                
                <section class="form-group">
                    <label for="password">Mật khẩu <c:if test="${!isEdit}"><span class="required">*</span></c:if></label>
                    <input type="password" id="password" name="password" 
                           placeholder="${isEdit ? 'Để trống nếu không đổi mật khẩu' : 'Nhập mật khẩu (ít nhất 6 ký tự)'}"
                           <c:if test="${!isEdit}">required</c:if>>
                    <small class="help-text">
                        <c:choose>
                            <c:when test="${isEdit}">Để trống nếu không muốn thay đổi mật khẩu</c:when>
                            <c:otherwise>Mật khẩu phải có ít nhất 6 ký tự</c:otherwise>
                        </c:choose>
                    </small>
                    <span class="error-text" id="passwordError"></span>
                </section>

                <c:if test="${!isEdit}">
                    <section class="form-group">
                        <label for="confirmPassword">Xác nhận mật khẩu <span class="required">*</span></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" 
                               placeholder="Nhập lại mật khẩu" required>
                        <span class="error-text" id="confirmPasswordError"></span>
                    </section>
                </c:if>
            </fieldset>
            
            <fieldset>
                <legend>Thông tin cá nhân</legend>
                <section class="form-group">
                    <label for="fullName">Họ và Tên <span class="required">*</span></label>
                    <input type="text" id="fullName" name="fullName" 
                           value="${not empty fullName ? fullName : (not empty admin ? admin.fullName : '')}"
                           placeholder="Nhập họ và tên" required>
                    <span class="error-text" id="fullNameError"></span>
                </section>
            </fieldset>
            
            <footer class="form-actions">
                <button type="submit" class="btn btn-success btn-lg">
                    <c:choose>
                        <c:when test="${isEdit}">Cập nhật</c:when>
                        <c:otherwise>Thêm Admin</c:otherwise>
                    </c:choose>
                </button>
                <a href="${pageContext.request.contextPath}/admin/user?action=list" class="btn btn-secondary btn-lg">Hủy</a>
            </footer>
        </form>
    </main>
    
    <%@ include file="../footer_admin.jsp" %>
    
    <script>
        // Client-side validation
        function validateForm() {
            var isValid = true;
            var isEditMode = document.querySelector('input[name="action"]').value === 'update';
            
            // Clear previous errors
            clearErrors();
            
            // Validate email
            var email = document.getElementById('email');
            var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!email.value.trim()) {
                showError('email', 'emailError', 'Vui lòng nhập email');
                isValid = false;
            } else if (!emailPattern.test(email.value.trim())) {
                showError('email', 'emailError', 'Email không đúng định dạng');
                isValid = false;
            }
            
            // Validate password
            var password = document.getElementById('password');
            
            if (!isEditMode) {
                // Create mode: password required
                if (!password.value) {
                    showError('password', 'passwordError', 'Vui lòng nhập mật khẩu');
                    isValid = false;
                } else if (password.value.length < 6) {
                    showError('password', 'passwordError', 'Mật khẩu phải có ít nhất 6 ký tự');
                    isValid = false;
                }
                
                // Validate confirm password
                var confirmPassword = document.getElementById('confirmPassword');
                if (confirmPassword) {
                    if (!confirmPassword.value) {
                        showError('confirmPassword', 'confirmPasswordError', 'Vui lòng xác nhận mật khẩu');
                        isValid = false;
                    } else if (password.value !== confirmPassword.value) {
                        showError('confirmPassword', 'confirmPasswordError', 'Mật khẩu xác nhận không khớp');
                        isValid = false;
                    }
                }
            } else {
                // Edit mode: password optional but if provided must be >= 6 chars
                if (password.value && password.value.length < 6) {
                    showError('password', 'passwordError', 'Mật khẩu phải có ít nhất 6 ký tự');
                    isValid = false;
                }
            }
            
            // Validate full name
            var fullName = document.getElementById('fullName');
            if (!fullName.value.trim()) {
                showError('fullName', 'fullNameError', 'Vui lòng nhập họ và tên');
                isValid = false;
            }
            
            return isValid;
        }
        
        function showError(inputId, errorId, message) {
            var input = document.getElementById(inputId);
            var errorDiv = document.getElementById(errorId);
            if (input) {
                input.classList.add('error');
            }
            if (errorDiv) {
                errorDiv.textContent = message;
                errorDiv.style.display = 'block';
            }
        }
        
        function clearErrors() {
            // Clear all error classes
            var inputs = document.querySelectorAll('input');
            inputs.forEach(function(input) { input.classList.remove('error'); });
            
            // Hide all error messages
            var errorDivs = document.querySelectorAll('.error-text');
            errorDivs.forEach(function(div) {
                div.style.display = 'none';
                div.textContent = '';
            });
        }
        
        // Clear error on input focus
        document.querySelectorAll('input').forEach(function(input) {
            input.addEventListener('focus', function() {
                this.classList.remove('error');
                var errorDiv = document.getElementById(this.id + 'Error');
                if (errorDiv) {
                    errorDiv.style.display = 'none';
                }
            });
        });
    </script>
</body>
</html>
