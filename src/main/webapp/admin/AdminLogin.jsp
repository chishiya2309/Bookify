<<<<<<< HEAD
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập Quản trị | Bookstore System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <main class="login-wrapper admin-login">
        <header class="login-header">
            <i class="fas fa-user-shield fa-3x" style="color: var(--color-admin); margin-bottom: 15px;"></i>
            <h2>Admin Portal</h2>
            <p>Vui lòng đăng nhập để quản lý hệ thống</p>
        </header>
        
        <div id="messageContainer"></div>
        
        <c:if test="${not empty param.logout}">
            <div class="alert-success" style="padding: 10px; background: #d1e7dd; color: #0f5132; margin-bottom: 15px; border-radius: 4px;">
                <i class="fas fa-check-circle"></i>
                <span>Đã đăng xuất thành công.</span>
            </div>
        </c:if>
        
        <form id="adminLoginForm">
            <p class="form-group">
                <label for="email">Email quản trị viên</label>
                <input type="email" id="email" name="email" class="form-control" 
                       placeholder="admin@bookstore.com" required autofocus>
            </p>
            
            <p class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" 
                       placeholder="Nhập mật khẩu" required>
            </p>
            
            <p class="forgot-password" style="text-align: right; margin-bottom: 15px;">
                <a href="${pageContext.request.contextPath}/admin/forgot_password.jsp">Quên mật khẩu?</a>
            </p>
            
            <button type="submit" class="btn-login" id="loginButton" style="width: 100%; padding: 10px; cursor: pointer;">
                <span id="buttonText">Đăng nhập</span>
                <span class="spinner" id="spinner" style="display: none; margin-left: 5px;"><i class="fas fa-spinner fa-spin"></i></span>
            </button>
        </form>
        
        <footer class="login-footer">
            &copy; 2025 Bookify Admin System
        </footer>
    </main>

    <script>
        // Lấy các element từ DOM
        var adminLoginForm = document.getElementById('adminLoginForm');
        var emailInput = document.getElementById('email');
        var passwordInput = document.getElementById('password');
        var loginButton = document.getElementById('loginButton');
        var buttonText = document.getElementById('buttonText');
        var spinner = document.getElementById('spinner');
        var messageContainer = document.getElementById('messageContainer');
        
        // Lấy context path từ JSP
        var contextPath = '${pageContext.request.contextPath}';
        
        // Bắt sự kiện submit form
        adminLoginForm.addEventListener('submit', function(e) {
            e.preventDefault(); // <--- CHỐT CHẶN: Ngăn reload trang (GET request)
            
            var email = emailInput.value.trim();
            var password = passwordInput.value;
            
            // Validate sơ bộ
            if (!email || !password) {
                showMessage('error', 'Vui lòng điền đầy đủ thông tin!');
                return;
            }
            
            // Validate định dạng email
            var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showMessage('error', 'Email không hợp lệ!');
                return;
            }
            
            // Bắt đầu gửi request
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
                // Kiểm tra xem server có trả về JSON không
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") === -1) {
                    // Nếu trả về HTML (lỗi 404/500), ném lỗi để xuống catch
                    throw new Error("Server Error: Không nhận được JSON. Có thể lỗi 500 hoặc 404.");
                }
                return response.json();
            })
            .then(function(data) {
                if (data.success) {
                    // Kiểm tra quyền Admin
                    if (data.userType === 'ADMIN') {
                        showMessage('success', 'Đăng nhập thành công! Đang chuyển trang...');
                        
                        // Chuyển hướng sau 1s
                        setTimeout(function() {
                            window.location.href = contextPath + '/admin/dashboard.jsp';
                        }, 1000);
                    } else {
                        // Là CUSTOMER - không cho đăng nhập ở trang Admin
                        // Gọi logout để xóa HttpOnly cookie
                        fetch(contextPath + '/auth/logout', { method: 'POST' })
                            .finally(function() {
                                setLoadingState(false);
                                showMessage('error', 'Tài khoản khách hàng không có quyền truy cập Admin!');
                            });
                    }
                } else {
                    // Server báo lỗi logic (sai pass, v.v.)
                    setLoadingState(false);
                    showMessage('error', data.message || 'Email hoặc mật khẩu không chính xác!');
                }
            })
            .catch(function(error) {
                setLoadingState(false);
                console.error('Login error:', error);
                showMessage('error', 'Lỗi kết nối hoặc lỗi server (500). Vui lòng kiểm tra log!');
            });
        });
        
        // Hàm hiển thị thông báo lỗi/thành công
        function showMessage(type, text) {
            var iconClass = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
            // Style inline để đảm bảo hiển thị kể cả khi thiếu CSS
            var bgColor = type === 'success' ? '#d1e7dd' : '#f8d7da';
            var textColor = type === 'success' ? '#0f5132' : '#842029';
            
            messageContainer.innerHTML = 
                '<div style="padding: 10px; margin-bottom: 15px; border-radius: 4px; display: flex; align-items: center; gap: 10px; background-color: ' + bgColor + '; color: ' + textColor + ';">' +
                    '<i class="fas ' + iconClass + '"></i>' +
                    '<span>' + text + '</span>' +
                '</div>';
        }
        
        // Hàm bật/tắt trạng thái loading
        function setLoadingState(isLoading) {
            loginButton.disabled = isLoading;
            buttonText.textContent = isLoading ? 'Đang xử lý...' : 'Đăng nhập';
            spinner.style.display = isLoading ? 'inline-block' : 'none';
            emailInput.disabled = isLoading;
            passwordInput.disabled = isLoading;
        }
        
        // Xóa thông báo khi người dùng nhập lại
        emailInput.addEventListener('input', function() { messageContainer.innerHTML = ''; });
        passwordInput.addEventListener('input', function() { messageContainer.innerHTML = ''; });
    </script>
</body>
</html>
