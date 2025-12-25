<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán chuyển khoản - Bookify</title>
    
    <!-- Google Fonts - Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            --color-primary: #0D6EFD;
            --color-success: #198754;
            --color-warning: #FFC107;
            --text-main: #212529;
            --text-light: #6C757D;
            --bg-body: #F8F9FA;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .payment-container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            max-width: 500px;
            width: 100%;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            text-align: center;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .payment-header {
            margin-bottom: 30px;
        }

        .payment-header h1 {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
        }

        .payment-header p {
            color: var(--text-light);
            font-size: 14px;
        }

        .order-info {
            background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
            border: 2px solid #e8ecf1;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 24px;
        }

        .order-info .order-id {
            font-size: 14px;
            color: var(--text-light);
        }

        .order-info .order-amount {
            font-size: 32px;
            font-weight: 700;
            color: var(--color-primary);
            margin: 8px 0;
        }

        /* QR Code Section */
        .qr-section {
            margin-bottom: 24px;
            position: relative;
        }

        .qr-code-wrapper {
            position: relative;
            display: inline-block;
        }

        .qr-code {
            width: 220px;
            height: 220px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        }

        /* Loading Animation */
        .loading-overlay {
            position: absolute;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 220px;
            height: 220px;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.9);
            display: none;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }

        .loading-overlay.active {
            display: flex;
        }

        .loading-spinner {
            width: 50px;
            height: 50px;
            border: 4px solid #e0e0e0;
            border-top: 4px solid var(--color-primary);
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        /* Scanning Animation */
        .scan-line {
            position: absolute;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 200px;
            height: 3px;
            background: linear-gradient(90deg, transparent, #0D6EFD, transparent);
            animation: scanMove 2s ease-in-out infinite;
        }

        @keyframes scanMove {
            0%, 100% { top: 10px; }
            50% { top: 200px; }
        }

        .waiting-text {
            margin-top: 16px;
            font-size: 14px;
            color: var(--text-light);
            animation: pulse 2s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }

        /* Bank Info */
        .bank-info {
            background: #fff8e1;
            border: 2px solid #ffc107;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 24px;
            text-align: left;
        }

        .bank-info h3 {
            font-size: 14px;
            font-weight: 600;
            color: #856404;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #ffe082;
        }

        .info-row:last-child {
            border-bottom: none;
        }

        .info-label {
            font-size: 13px;
            color: #856404;
        }

        .info-value {
            font-weight: 600;
            font-size: 14px;
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .copy-btn {
            background: #ffc107;
            border: none;
            padding: 4px 8px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 11px;
            color: #856404;
            transition: all 0.2s;
        }

        .copy-btn:hover {
            background: #ffb300;
        }

        .transfer-content {
            font-family: monospace;
            font-size: 16px;
            font-weight: 700;
            color: #c62828;
        }

        /* Timer */
        .timer-section {
            margin-bottom: 24px;
        }

        .timer {
            font-size: 24px;
            font-weight: 700;
            color: var(--color-warning);
        }

        .timer-label {
            font-size: 13px;
            color: var(--text-light);
        }

        /* Actions */
        .actions {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            justify-content: center;
        }

        .btn {
            padding: 12px 24px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            border: none;
            font-family: 'Inter', sans-serif;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--color-primary) 0%, #0b5ed7 100%);
            color: white;
        }

        .btn-secondary {
            background: #f0f0f0;
            color: var(--text-main);
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        /* Success State */
        .success-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255, 255, 255, 0.95);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }

        .success-overlay.active {
            display: flex;
        }

        .success-content {
            text-align: center;
            animation: scaleIn 0.5s ease-out;
        }

        @keyframes scaleIn {
            from { transform: scale(0.5); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }

        .success-icon {
            width: 100px;
            height: 100px;
            background: var(--color-success);
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0 auto 24px;
        }

        .success-icon i {
            font-size: 50px;
            color: white;
        }

        .success-content h2 {
            font-size: 28px;
            color: var(--color-success);
            margin-bottom: 12px;
        }

        .success-content p {
            color: var(--text-light);
            margin-bottom: 24px;
        }

        /* Responsive */
        @media (max-width: 480px) {
            .payment-container {
                padding: 24px;
            }

            .qr-code {
                width: 180px;
                height: 180px;
            }

            .scan-line {
                width: 160px;
            }

            .loading-overlay {
                width: 180px;
                height: 180px;
            }
        }
    </style>
</head>
<body>

    <div class="payment-container">
        <div class="payment-header">
            <h1><i class="fas fa-qrcode"></i> Quét mã để thanh toán</h1>
            <p>Sử dụng ứng dụng ngân hàng để quét mã QR</p>
        </div>

        <!-- Order Info -->
        <div class="order-info">
            <div class="order-id">Đơn hàng #${order.orderId}</div>
            <div class="order-amount">
                <fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫
            </div>
        </div>

        <!-- QR Code -->
        <div class="qr-section">
            <div class="qr-code-wrapper">
                <img src="${vietQRUrl}" alt="QR Code" class="qr-code" id="qrCode">
                <div class="scan-line"></div>
            </div>
            <p class="waiting-text">
                <i class="fas fa-clock"></i> Đang chờ thanh toán...
            </p>
        </div>

        <!-- Bank Info -->
        <div class="bank-info">
            <h3><i class="fas fa-university"></i> Hoặc chuyển khoản thủ công</h3>
            
            <div class="info-row">
                <span class="info-label">Ngân hàng</span>
                <span class="info-value">${bankName}</span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Số tài khoản</span>
                <span class="info-value">
                    ${accountNumber}
                    <button class="copy-btn" onclick="copyToClipboard('${accountNumber}')">
                        <i class="fas fa-copy"></i>
                    </button>
                </span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Nội dung CK</span>
                <span class="info-value transfer-content">
                    ${transferContent}
                    <button class="copy-btn" onclick="copyToClipboard('${transferContent}')">
                        <i class="fas fa-copy"></i>
                    </button>
                </span>
            </div>
        </div>

        <!-- Timer -->
        <div class="timer-section">
            <div class="timer" id="timer">15:00</div>
            <div class="timer-label">Thời gian còn lại</div>
        </div>

        <!-- Actions -->
        <div class="actions">
            <a href="${pageContext.request.contextPath}/customer/order-confirmation?orderId=${order.orderId}" class="btn btn-secondary">
                <i class="fas fa-times"></i> Hủy
            </a>
            <button class="btn btn-primary" onclick="checkPaymentStatus()">
                <i class="fas fa-sync-alt"></i> Kiểm tra
            </button>
        </div>
    </div>

    <!-- Success Overlay -->
    <div class="success-overlay" id="successOverlay">
        <div class="success-content">
            <div class="success-icon">
                <i class="fas fa-check"></i>
            </div>
            <h2>Thanh toán thành công!</h2>
            <p>Đang chuyển hướng...</p>
        </div>
    </div>

    <script>
        const orderId = ${order.orderId};
        const contextPath = '${pageContext.request.contextPath}';
        let timeLeft = 15 * 60; // 15 minutes
        let pollingInterval;

        // Timer countdown
        function updateTimer() {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            document.getElementById('timer').textContent = 
                String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
            
            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                clearInterval(pollingInterval);
                alert('Hết thời gian thanh toán. Vui lòng thử lại.');
                window.location.href = contextPath + '/customer/order-confirmation?orderId=' + orderId;
            }
            
            timeLeft--;
        }

        const timerInterval = setInterval(updateTimer, 1000);
        updateTimer();

        // Poll for payment status every 5 seconds
        function checkPaymentStatus() {
            fetch(contextPath + '/api/payment/status?orderId=' + orderId)
                .then(response => response.json())
                .then(data => {
                    if (data.paid === true) {
                        showSuccess();
                    }
                })
                .catch(error => {
                    console.log('Status check error:', error);
                });
        }

        // Start polling
        pollingInterval = setInterval(checkPaymentStatus, 5000);

        // Show success and redirect
        function showSuccess() {
            clearInterval(timerInterval);
            clearInterval(pollingInterval);
            
            document.getElementById('successOverlay').classList.add('active');
            
            setTimeout(function() {
                window.location.href = contextPath + '/customer/order-confirmation?orderId=' + orderId;
            }, 2000);
        }

        // Copy to clipboard
        function copyToClipboard(text) {
            navigator.clipboard.writeText(text).then(function() {
                var toast = document.createElement('div');
                toast.textContent = 'Đã sao chép: ' + text;
                toast.style.cssText = 'position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background: #333; color: white; padding: 12px 24px; border-radius: 8px; z-index: 9999;';
                document.body.appendChild(toast);
                setTimeout(function() { toast.remove(); }, 2000);
            });
        }
    </script>

</body>
</html>
