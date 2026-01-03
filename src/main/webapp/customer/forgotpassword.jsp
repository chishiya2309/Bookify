<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu | Bookstore</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .step-indicator {
            display: flex;
            justify-content: center;
            margin-bottom: 30px;
            gap: 10px;
        }
        .step {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background: #e9ecef;
            color: #6c757d;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 14px;
        }
        .step.active {
            background: var(--color-primary);
            color: white;
        }
        .step.completed {
            background: var(--color-success);
            color: white;
        }
        .step-line {
            width: 50px;
            height: 2px;
            background: #e9ecef;
            align-self: center;
        }
        .step-line.completed {
            background: var(--color-success);
        }
        .otp-inputs {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin: 20px 0;
        }
        .otp-input {
            width: 45px;
            height: 50px;
            text-align: center;
            font-size: 20px;
            font-weight: 600;
            border: 2px solid #ced4da;
            border-radius: 8px;
            outline: none;
            transition: all 0.3s;
        }
        .otp-input:focus {
            border-color: var(--color-primary);
            box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
        }
        .resend-link {
            text-align: center;
            margin-top: 15px;
            font-size: 14px;
        }
        .resend-link a {
            color: var(--color-primary);
            text-decoration: none;
            cursor: pointer;
        }
        .resend-link a:hover {
            text-decoration: underline;
        }
        .resend-link a.disabled {
            color: #6c757d;
            pointer-events: none;
        }
        .hidden { display: none !important; }
        .password-toggle {
            position: relative;
        }
        .password-toggle .toggle-btn {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #6c757d;
            cursor: pointer;
        }
        .password-toggle input {
            padding-right: 40px;
        }
        /* Ẩn icon mắt mặc định của trình duyệt Edge/Chrome */
        input[type="password"]::-ms-reveal,
        input[type="password"]::-ms-clear {
            display: none;
        }
        input::-webkit-credentials-auto-fill-button {
            display: none !important;
        }
    </style>
</head>
<body>
    <main class="login-wrapper">
        <header class="login-header">
            <i class="fas fa-key fa-3x" style="color: var(--color-primary); margin-bottom: 15px;"></i>
            <h2>Quên mật khẩu</h2>
            <p id="stepDescription">Nhập email để nhận mã xác nhận</p>
        </header>
        
        <!-- Step Indicator -->
        <div class="step-indicator">
            <div class="step active" id="step1">1</div>
            <div class="step-line" id="line1"></div>
            <div class="step" id="step2">2</div>
            <div class="step-line" id="line2"></div>
            <div class="step" id="step3">3</div>
        </div>
        
        <div id="messageContainer"></div>
        
        <!-- Step 1: Enter Email -->
        <form id="emailForm">
            <p class="form-group">
                <label for="email">Email đã đăng ký</label>
                <input type="email" id="email" name="email" class="form-control" 
                       placeholder="example@email.com" required autofocus>
            </p>
            <button type="submit" class="btn-login" id="sendOtpBtn">
                <span id="sendOtpText">Gửi mã xác nhận</span>
                <span class="spinner" id="sendOtpSpinner" style="display: none;"></span>
            </button>
        </form>
        
        <!-- Step 2: Enter OTP -->
        <form id="otpForm" class="hidden">
            <p style="text-align: center; color: var(--text-light); margin-bottom: 10px;">
                Mã xác nhận đã được gửi đến<br>
                <strong id="maskedEmail"></strong>
            </p>
            <div class="otp-inputs">
                <input type="text" class="otp-input" maxlength="1" data-index="0">
                <input type="text" class="otp-input" maxlength="1" data-index="1">
                <input type="text" class="otp-input" maxlength="1" data-index="2">
                <input type="text" class="otp-input" maxlength="1" data-index="3">
                <input type="text" class="otp-input" maxlength="1" data-index="4">
                <input type="text" class="otp-input" maxlength="1" data-index="5">
            </div>
            <button type="submit" class="btn-login" id="verifyOtpBtn">
                <span id="verifyOtpText">Xác nhận</span>
                <span class="spinner" id="verifyOtpSpinner" style="display: none;"></span>
            </button>
            <p class="resend-link">
                <span id="resendTimer">Gửi lại mã sau <strong id="countdown">60</strong>s</span>
                <a id="resendLink" class="hidden" onclick="resendOtp()">Gửi lại mã</a>
            </p>
        </form>
        
        <!-- Step 3: New Password -->
        <form id="passwordForm" class="hidden">
            <p class="form-group password-toggle">
                <label for="newPassword">Mật khẩu mới</label>
                <input type="password" id="newPassword" name="newPassword" class="form-control" 
                       placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)" required>
                <button type="button" class="toggle-btn" onclick="togglePassword('newPassword')">
                    <i class="fas fa-eye"></i>
                </button>
            </p>
            <p class="form-group password-toggle">
                <label for="confirmPassword">Xác nhận mật khẩu</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                       placeholder="Nhập lại mật khẩu mới" required>
                <button type="button" class="toggle-btn" onclick="togglePassword('confirmPassword')">
                    <i class="fas fa-eye"></i>
                </button>
            </p>
            <button type="submit" class="btn-login" id="resetBtn">
                <span id="resetText">Đặt lại mật khẩu</span>
                <span class="spinner" id="resetSpinner" style="display: none;"></span>
            </button>
        </form>
        
        <p class="register-prompt">
            <a href="${pageContext.request.contextPath}/customer/login.jsp">
                <i class="fas fa-arrow-left"></i> Quay lại đăng nhập
            </a>
        </p>
        
        <footer class="login-footer">
            &copy; 2025 Bookify
        </footer>
    </main>

    <script>
        var contextPath = '${pageContext.request.contextPath}';
        var userEmail = '';
        var countdownInterval;
        
        // Step 1: Send OTP
        document.getElementById('emailForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var email = document.getElementById('email').value.trim();
            
            if (!email) {
                showMessage('error', 'Vui lòng nhập email!');
                return;
            }
            
            setLoading('sendOtp', true);
            
            fetch(contextPath + '/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ action: 'sendOtp', email: email })
            })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                setLoading('sendOtp', false);
                if (data.success) {
                    userEmail = email;
                    document.getElementById('maskedEmail').textContent = maskEmail(email);
                    goToStep(2);
                    startCountdown();
                    showMessage('success', data.message);
                } else {
                    showMessage('error', data.message);
                }
            })
            .catch(function() {
                setLoading('sendOtp', false);
                showMessage('error', 'Lỗi kết nối. Vui lòng thử lại!');
            });
        });
        
        // Step 2: Verify OTP
        document.getElementById('otpForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var otp = getOtpValue();
            
            if (otp.length !== 6) {
                showMessage('error', 'Vui lòng nhập đủ 6 số!');
                return;
            }
            
            setLoading('verifyOtp', true);
            
            fetch(contextPath + '/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ action: 'verifyOtp', email: userEmail, otp: otp })
            })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                setLoading('verifyOtp', false);
                if (data.success) {
                    goToStep(3);
                    showMessage('success', 'Xác nhận thành công!');
                } else {
                    showMessage('error', data.message);
                }
            })
            .catch(function() {
                setLoading('verifyOtp', false);
                showMessage('error', 'Lỗi kết nối. Vui lòng thử lại!');
            });
        });
        
        // Step 3: Reset Password
        document.getElementById('passwordForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var newPass = document.getElementById('newPassword').value;
            var confirmPass = document.getElementById('confirmPassword').value;
            
            if (newPass.length < 6) {
                showMessage('error', 'Mật khẩu phải có ít nhất 6 ký tự!');
                return;
            }
            if (newPass !== confirmPass) {
                showMessage('error', 'Mật khẩu xác nhận không khớp!');
                return;
            }
            
            setLoading('reset', true);
            
            fetch(contextPath + '/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ 
                    action: 'resetPassword', 
                    email: userEmail, 
                    otp: getOtpValue(),
                    newPassword: newPass 
                })
            })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                setLoading('reset', false);
                if (data.success) {
                    showMessage('success', data.message);
                    setTimeout(function() {
                        window.location.href = contextPath + '/customer/login.jsp?reset=success';
                    }, 2000);
                } else {
                    showMessage('error', data.message);
                }
            })
            .catch(function() {
                setLoading('reset', false);
                showMessage('error', 'Lỗi kết nối. Vui lòng thử lại!');
            });
        });
        
        // OTP Input handling
        var otpInputs = document.querySelectorAll('.otp-input');
        otpInputs.forEach(function(input, index) {
            input.addEventListener('input', function(e) {
                var value = e.target.value.replace(/[^0-9]/g, '');
                e.target.value = value;
                if (value && index < 5) {
                    otpInputs[index + 1].focus();
                }
            });
            input.addEventListener('keydown', function(e) {
                if (e.key === 'Backspace' && !e.target.value && index > 0) {
                    otpInputs[index - 1].focus();
                }
            });
            input.addEventListener('paste', function(e) {
                e.preventDefault();
                var paste = (e.clipboardData || window.clipboardData).getData('text');
                var digits = paste.replace(/[^0-9]/g, '').split('');
                digits.forEach(function(digit, i) {
                    if (otpInputs[i]) otpInputs[i].value = digit;
                });
                if (digits.length >= 6) otpInputs[5].focus();
            });
        });
        
        function getOtpValue() {
            var otp = '';
            otpInputs.forEach(function(input) { otp += input.value; });
            return otp;
        }
        
        function goToStep(step) {
            document.getElementById('emailForm').classList.toggle('hidden', step !== 1);
            document.getElementById('otpForm').classList.toggle('hidden', step !== 2);
            document.getElementById('passwordForm').classList.toggle('hidden', step !== 3);
            
            for (var i = 1; i <= 3; i++) {
                var stepEl = document.getElementById('step' + i);
                stepEl.classList.remove('active', 'completed');
                if (i < step) stepEl.classList.add('completed');
                else if (i === step) stepEl.classList.add('active');
            }
            document.getElementById('line1').classList.toggle('completed', step > 1);
            document.getElementById('line2').classList.toggle('completed', step > 2);
            
            var descriptions = ['Nhập email để nhận mã xác nhận', 'Nhập mã 6 số đã gửi đến email', 'Tạo mật khẩu mới'];
            document.getElementById('stepDescription').textContent = descriptions[step - 1];
            document.getElementById('messageContainer').innerHTML = '';
        }
        
        function startCountdown() {
            var seconds = 60;
            document.getElementById('resendTimer').classList.remove('hidden');
            document.getElementById('resendLink').classList.add('hidden');
            
            countdownInterval = setInterval(function() {
                seconds--;
                document.getElementById('countdown').textContent = seconds;
                if (seconds <= 0) {
                    clearInterval(countdownInterval);
                    document.getElementById('resendTimer').classList.add('hidden');
                    document.getElementById('resendLink').classList.remove('hidden');
                }
            }, 1000);
        }
        
        function resendOtp() {
            fetch(contextPath + '/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ action: 'sendOtp', email: userEmail })
            })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (data.success) {
                    showMessage('success', 'Đã gửi lại mã xác nhận!');
                    startCountdown();
                    otpInputs.forEach(function(input) { input.value = ''; });
                    otpInputs[0].focus();
                } else {
                    showMessage('error', data.message);
                }
            });
        }
        
        function maskEmail(email) {
            var parts = email.split('@');
            var name = parts[0];
            var masked = name.charAt(0) + '***' + name.charAt(name.length - 1);
            return masked + '@' + parts[1];
        }
        
        function togglePassword(id) {
            var input = document.getElementById(id);
            var icon = input.nextElementSibling.querySelector('i');
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        }
        
        function setLoading(btn, loading) {
            var button = document.getElementById(btn + 'Btn');
            var text = document.getElementById(btn + 'Text');
            var spinner = document.getElementById(btn + 'Spinner');
            button.disabled = loading;
            spinner.style.display = loading ? 'inline-block' : 'none';
        }
        
        function showMessage(type, text) {
            var icon = type === 'success' ? 'check-circle' : 'exclamation-circle';
            document.getElementById('messageContainer').innerHTML = 
                '<div class="message ' + type + '"><i class="fas fa-' + icon + '"></i><span>' + text + '</span></div>';
        }
    </script>
</body>
</html>
