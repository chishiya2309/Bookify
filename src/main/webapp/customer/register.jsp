<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký Tài khoản | Bookstore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <main class="login-wrapper register-wrapper">
        <header class="login-header">
            <i class="fas fa-user-plus fa-3x" style="color: var(--color-primary); margin-bottom: 15px;"></i>
            <h2>Tạo tài khoản mới</h2>
            <p>Điền thông tin bên dưới để đăng ký thành viên</p>
        </header>

        <div id="messageContainer"></div>

        <form id="registerForm">
            <p class="form-group">
                <label for="email">Địa chỉ Email (*)</label>
                <input type="email" id="email" name="email" class="form-control" 
                       placeholder="vidu@email.com" required>
            </p>

            <p class="form-group">
                <label for="fullName">Họ và Tên (*)</label>
                <input type="text" id="fullName" name="fullName" class="form-control" 
                       placeholder="Nguyễn Văn A" required>
            </p>

            <p class="form-group">
                <label for="phoneNumber">Số điện thoại (*)</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" 
                       placeholder="0912345678" required>
            </p>

            <p class="form-group">
                <label for="password">Mật khẩu (*)</label>
                <input type="password" id="password" name="password" class="form-control" 
                       placeholder="Tối thiểu 6 ký tự" required>
            </p>

            <p class="form-group">
                <label for="confirmPassword">Xác nhận mật khẩu (*)</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                       placeholder="Nhập lại mật khẩu" required>
            </p>

            <p class="button-group">
                <button type="submit" class="btn-login" id="registerButton">
                    <span id="buttonText">Đăng ký</span>
                    <span class="spinner" id="spinner" style="display: none;"></span>
                </button>
            </p>
        </form>

        <p class="register-prompt">
            Đã có tài khoản? 
            <a href="${pageContext.request.contextPath}/customer/login.jsp">Đăng nhập ngay</a>
        </p>

        <footer class="login-footer">
            &copy; 2025 Bookstore Customer Portal
        </footer>
    </main>

    <script>
        var registerForm = document.getElementById('registerForm');
        var messageContainer = document.getElementById('messageContainer');
        var registerButton = document.getElementById('registerButton');
        var buttonText = document.getElementById('buttonText');
        var spinner = document.getElementById('spinner');
        var contextPath = '${pageContext.request.contextPath}';
        
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            var email = document.getElementById('email').value.trim();
            var fullName = document.getElementById('fullName').value.trim();
            var phoneNumber = document.getElementById('phoneNumber').value.trim();
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            // Validation
            if (!email || !fullName || !phoneNumber || !password) {
                showMessage('error', 'Vui lòng điền đầy đủ thông tin bắt buộc!');
                return;
            }
            
            var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showMessage('error', 'Email không hợp lệ!');
                return;
            }
            
            if (password.length < 6) {
                showMessage('error', 'Mật khẩu phải có ít nhất 6 ký tự!');
                return;
            }
            
            if (password !== confirmPassword) {
                showMessage('error', 'Mật khẩu xác nhận không khớp!');
                return;
            }
            
            var phoneRegex = /^(0|\+84)[0-9]{9}$/;
            if (!phoneRegex.test(phoneNumber)) {
                showMessage('error', 'Số điện thoại không hợp lệ! (VD: 0912345678)');
                return;
            }
            
            setLoadingState(true);
            
            fetch(contextPath + '/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    email: email,
                    fullName: fullName,
                    phoneNumber: phoneNumber,
                    password: password
                })
            })
            .then(function(response) {
                return response.json();
            })
            .then(function(data) {
                if (data.success) {
                    // ✅ SUCCESS - Show message
                    showMessage('success', 'Đăng ký thành công! Đang chuyển sang trang đăng nhập...');
                    
                    // ❌ REMOVED: Save tokens to localStorage
                    // ❌ REMOVED: Auto-login logic
                    
                    // ✅ Redirect to login page with email parameter
                    setTimeout(function() {
                        // Store email in sessionStorage to pre-fill form on login page
                        sessionStorage.setItem('registeredEmail', data.email);
                        window.location.href = contextPath + '/customer/login.jsp?registered=true&email=' + encodeURIComponent(data.email);
                    }, 2000);
                    
                } else {
                    setLoadingState(false);
                    showMessage('error', data.message || 'Đăng ký thất bại!');
                }
            })
            .catch(function(error) {
                setLoadingState(false);
                showMessage('error', 'Đã xảy ra lỗi kết nối. Vui lòng thử lại!');
                console.error('Register error:', error);
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
            registerButton.disabled = isLoading;
            buttonText.textContent = isLoading ? 'Đang xử lý...' : 'Đăng ký';
            spinner.style.display = isLoading ? 'inline-block' : 'none';
            
            document.querySelectorAll('input').forEach(function(input) {
                input.disabled = isLoading;
            });
        }
        
        document.querySelectorAll('input').forEach(function(input) {
            input.addEventListener('input', function() {
                if (messageContainer.querySelector('.message.error')) {
                    messageContainer.innerHTML = '';
                }
            });
        });
    </script>
</body>
</html>