<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lỗi - Bookify</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                /* Màu thương hiệu */
                --color-primary: #0D6EFD;
                --color-secondary: #6C757D;
                --color-accent: #FF9900;
                
                /* Màu nền */
                --bg-body: #F8F9FA;
                --bg-white: #FFFFFF;
                
                /* Màu chữ */
                --text-main: #212529;
                --text-light: #6C757D;
                
                /* Màu trạng thái */
                --color-success: #198754;
                --color-error: #DC3545;
                --color-warning: #FFC107;
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background-color: var(--bg-body);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
            }

            .error-container {
                max-width: 600px;
                width: 100%;
                background: var(--bg-white);
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                overflow: hidden;
                text-align: center;
            }

            .error-header {
                background: linear-gradient(135deg, var(--color-error) 0%, #c82333 100%);
                color: white;
                padding: 40px 24px;
            }

            .error-icon {
                font-size: 80px;
                margin-bottom: 20px;
                opacity: 0.9;
                animation: pulse 2s ease-in-out infinite;
            }

            @keyframes pulse {
                0%, 100% {
                    transform: scale(1);
                }
                50% {
                    transform: scale(1.05);
                }
            }

            .error-header h1 {
                font-size: 32px;
                font-weight: 700;
                margin-bottom: 8px;
            }

            .error-header p {
                font-size: 16px;
                opacity: 0.95;
            }

            .error-content {
                padding: 40px 24px;
            }

            .error-message {
                background: #fff5f5;
                border-left: 4px solid var(--color-error);
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 32px;
                text-align: left;
            }

            .error-message-title {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 12px;
                color: var(--color-error);
                font-weight: 600;
                font-size: 16px;
            }

            .error-message-text {
                color: var(--text-main);
                font-size: 14px;
                line-height: 1.6;
                margin: 0;
                word-break: break-word;
            }

            .error-actions {
                display: flex;
                gap: 12px;
                justify-content: center;
                flex-wrap: wrap;
            }

            .btn {
                padding: 12px 24px;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 500;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                transition: all 0.3s;
                text-decoration: none;
            }

            .btn-primary {
                background: var(--color-primary);
                color: white;
            }

            .btn-primary:hover {
                background: #0b5ed7;
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(13, 110, 253, 0.3);
            }

            .btn-secondary {
                background: var(--color-secondary);
                color: white;
            }

            .btn-secondary:hover {
                background: #5a6268;
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(108, 117, 125, 0.3);
            }

            .btn-outline {
                background: transparent;
                color: var(--color-primary);
                border: 2px solid var(--color-primary);
            }

            .btn-outline:hover {
                background: var(--color-primary);
                color: white;
            }

            @media (max-width: 768px) {
                .error-icon {
                    font-size: 60px;
                }

                .error-header h1 {
                    font-size: 24px;
                }

                .error-content {
                    padding: 32px 20px;
                }

                .error-actions {
                    flex-direction: column;
                }

                .btn {
                    width: 100%;
                    justify-content: center;
                }
            }
        </style>
    </head>
    <body>
        <div class="error-container">
            <div class="error-header">
                <i class="fas fa-exclamation-triangle error-icon"></i>
                <h1>Đã xảy ra lỗi</h1>
                <p>Rất tiếc, có vấn đề xảy ra khi xử lý yêu cầu của bạn</p>
            </div>
            
            <div class="error-content">
                <c:if test="${not empty error}">
                    <div class="error-message">
                        <div class="error-message-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Chi tiết lỗi:</span>
                        </div>
                        <p class="error-message-text">${error}</p>
                    </div>
                </c:if>

                <c:if test="${empty error}">
                    <div class="error-message">
                        <div class="error-message-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Thông báo:</span>
                        </div>
                        <p class="error-message-text">Đã xảy ra lỗi không xác định. Vui lòng thử lại sau.</p>
                    </div>
                </c:if>

                <div class="error-actions">
                    <a href="javascript:history.back()" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>
                        Quay lại
                    </a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                        <i class="fas fa-home"></i>
                        Về trang chủ
                    </a>
                    <a href="cart" class="btn btn-outline">
                        <i class="fas fa-shopping-cart"></i>
                        Giỏ hàng
                    </a>
                </div>
            </div>
        </div>
    </body>
</html>