<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập Khách hàng | Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <main class="login-wrapper">
        <header class="login-header">
            <i class="fas fa-book-reader fa-3x" style="color: var(--color-primary); margin-bottom: 15px;"></i>
            <h2>Welcome Bookstore</h2>
            <p>Đăng nhập để tiếp tục mua sắm</p>
        </header>
        
        <div id="messageContainer"></div>
        
        <!-- ✅ Show success message when redirected from register -->
        <c:if test="${not empty param.registered}">
            <section class="alert-success">
                <i class="fas fa-check-circle"></i>
                <span>Đăng ký thành công! Vui lòng đăng nhập.</span>
            </section>
        </c:if>
        
        <form id="loginForm">
            <p class="form-group">
                <label for="email">Email khách hàng</label>
                <!-- ✅ Pre-fill email from register -->
                <input type="email" id="email" name="email" class="form-control" 
                       placeholder="khachhang@example.com" 
                       value="<c:out value='${param.email}'/>" 
                       required autofocus>
            </p>
            
            <p class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" 
                       placeholder="••••••••" required>
            </p>
            
            <p class="forgot-password">
                <a href="${pageContext.request.contextPath}/forgot_password.jsp">Quên mật khẩu?</a>
            </p>
            
            <button type="submit" class="btn-login" id="loginButton">
                <span id="buttonText">Đăng nhập ngay</span>
                <span class="spinner" id="spinner" style="display: none;"></span>
            </button>
        </form>
        
        <p class="register-prompt">
            Chưa có tài khoản? 
            <a href="${pageContext.request.contextPath}/customer/register.jsp">Đăng ký miễn phí</a>
        </p>
        
        <footer class="login-footer">
            &copy; 2025 Bookify
        </footer>
    </main>

    <script>
        var loginForm = document.getElementById('loginForm');
        var emailInput = document.getElementById('email');
        var passwordInput = document.getElementById('password');
        var loginButton = document.getElementById('loginButton');
        var buttonText = document.getElementById('buttonText');
        var spinner = document.getElementById('spinner');
        var messageContainer = document.getElementById('messageContainer');
        var contextPath = '${pageContext.request.contextPath}';
        
        // Lấy redirect URL từ query parameter (nếu có)
        var urlParams = new URLSearchParams(window.location.search);
        var redirectUrl = urlParams.get('redirect');
        
        // Validate redirectUrl to prevent open redirect vulnerability
        function isValidRedirectUrl(url) {
            if (!url) return false;
            
            // Must be a relative URL (starts with / but not //)
            if (!url.startsWith('/') || url.startsWith('//')) {
                return false;
            }
            
            // Must not contain protocol (http:, https:, javascript:, etc.)
            if (url.includes(':')) {
                return false;
            }
            
            // Ensure contextPath is defined and handle edge cases
            var appContextPath = contextPath || '';
            
            // Must start with context path or be root
            // When contextPath is empty, any URL starting with '/' is allowed
            // When contextPath is set, URL must be root or start with contextPath
            if (appContextPath && url !== '/' && !url.startsWith(appContextPath + '/')) {
                return false;
            }
            
            return true;
        }
        
        // ✅ Auto-focus password field if email is pre-filled
        window.addEventListener('DOMContentLoaded', function() {
            if (emailInput.value) {
                passwordInput.focus();
            }
        });
        
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            var email = emailInput.value.trim();
            var password = passwordInput.value;
            
            if (!email || !password) {
                showMessage('error', 'Vui lòng điền đầy đủ thông tin!');
                return;
            }
            
            var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showMessage('error', 'Email không hợp lệ!');
                return;
            }
            
            setLoadingState(true);
            
            fetch(contextPath + '/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    email: email,
                    password: password
                })
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(data) {
                if (data.success) {
                    // Kiểm tra phải là CUSTOMER mới cho đăng nhập ở trang này
                    if (data.userType === 'CUSTOMER') {
                        showMessage('success', 'Đăng nhập thành công! Đang chuyển trang...');
                        
                        setTimeout(function() {
                            // Redirect về trang trước đó nếu có, không thì về trang chủ
                            if (redirectUrl && isValidRedirectUrl(redirectUrl)) {
                                window.location.href = redirectUrl;
                            } else {
                                window.location.href = contextPath + '/';
                            }
                        }, 1000);
                    } else {
                        // Là ADMIN - không cho đăng nhập ở trang Customer
                        // Gọi logout để xóa HttpOnly cookie
                        fetch(contextPath + '/auth/logout', { method: 'POST' })
                            .finally(function() {
                                setLoadingState(false);
                                showMessage('error', 'Tài khoản Admin vui lòng đăng nhập tại trang quản trị!');
                            });
                    }
                } else {
                    setLoadingState(false);
                    showMessage('error', data.message || 'Đăng nhập thất bại!');
                }
            })
            .catch(function(error) {
                setLoadingState(false);
                showMessage('error', 'Đã xảy ra lỗi kết nối. Vui lòng thử lại!');
                console.error('Login error:', error);
            });
        });
        
        function showMessage(type, text) {
            var iconClass = type === 'success' ? 'check-circle' : 'exclamation-circle';
            messageContainer.innerHTML = 
                '<div class="message ' + type + '">' +
                    '<i class="fas fa-' + iconClass + '"></i>' +
                    '<span>' + text + '</span>' +
                '</div>';
            
            if (type === 'error') {
                setTimeout(function() {
                    var messageDiv = messageContainer.querySelector('.message');
                    if (messageDiv) {
                        messageDiv.style.animation = 'slideDown 0.3s ease-out reverse';
                        setTimeout(function() {
                            messageContainer.innerHTML = '';
                        }, 300);
                    }
                }, 5000);
            }
        }
        
        function setLoadingState(isLoading) {
            loginButton.disabled = isLoading;
            buttonText.textContent = isLoading ? 'Đang đăng nhập...' : 'Đăng nhập ngay';
            spinner.style.display = isLoading ? 'inline-block' : 'none';
            emailInput.disabled = isLoading;
            passwordInput.disabled = isLoading;
        }
        
        emailInput.addEventListener('input', function() {
            if (messageContainer.querySelector('.message.error')) {
                messageContainer.innerHTML = '';
            }
        });
        
        passwordInput.addEventListener('input', function() {
            if (messageContainer.querySelector('.message.error')) {
                messageContainer.innerHTML = '';
            }
        });
    </script>
</body>
</html>