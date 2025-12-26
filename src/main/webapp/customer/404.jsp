<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>404 - Không tìm thấy trang | Bookify</title>
        <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
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
                background: var(--color-primary);
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
                border-radius: 16px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                overflow: hidden;
                text-align: center;
            }

            .error-header {
                background: var(--color-primary);
                color: white;
                padding: 50px 24px;
                position: relative;
                overflow: hidden;
            }

            .error-header::before {
                content: '';
                position: absolute;
                top: -50%;
                left: -50%;
                width: 200%;
                height: 200%;
                background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
                animation: rotate 20s linear infinite;
            }

            @keyframes rotate {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }

            .error-code {
                font-size: 120px;
                font-weight: 800;
                margin-bottom: 10px;
                position: relative;
                text-shadow: 4px 4px 0 rgba(0,0,0,0.1);
                letter-spacing: -5px;
            }

            .error-icon {
                position: absolute;
                font-size: 40px;
                animation: float 3s ease-in-out infinite;
            }

            .icon-1 { top: 20px; left: 20%; animation-delay: 0s; }
            .icon-2 { top: 30px; right: 20%; animation-delay: 1s; }
            .icon-3 { bottom: 20px; left: 30%; animation-delay: 2s; }

            @keyframes float {
                0%, 100% { transform: translateY(0); opacity: 0.6; }
                50% { transform: translateY(-10px); opacity: 1; }
            }

            .error-header h1 {
                font-size: 24px;
                font-weight: 600;
                margin-bottom: 8px;
                position: relative;
            }

            .error-header p {
                font-size: 16px;
                opacity: 0.9;
                position: relative;
            }

            .error-content {
                padding: 40px 24px;
            }

            .error-illustration {
                margin-bottom: 24px;
            }

            .error-illustration i {
                font-size: 80px;
                color: var(--color-secondary);
                opacity: 0.5;
            }

            .error-message {
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                border-radius: 12px;
                padding: 24px;
                margin-bottom: 32px;
            }

            .error-message h2 {
                color: var(--text-main);
                font-size: 18px;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
            }

            .error-message p {
                color: var(--text-light);
                font-size: 14px;
                line-height: 1.6;
                margin: 0;
            }

            .suggestions {
                text-align: left;
                margin-top: 16px;
                padding-top: 16px;
                border-top: 1px dashed #dee2e6;
            }

            .suggestions h3 {
                font-size: 14px;
                color: var(--text-main);
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .suggestions ul {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .suggestions li {
                font-size: 13px;
                color: var(--text-light);
                padding: 6px 0;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .suggestions li i {
                color: var(--color-primary);
                font-size: 10px;
            }

            .error-actions {
                display: flex;
                gap: 12px;
                justify-content: center;
                flex-wrap: wrap;
            }

            .btn {
                padding: 14px 28px;
                border: none;
                border-radius: 10px;
                cursor: pointer;
                font-size: 14px;
                font-weight: 600;
                display: inline-flex;
                align-items: center;
                gap: 10px;
                transition: all 0.3s;
                text-decoration: none;
            }

            .btn-primary {
                background: var(--color-primary);
                color: white;
            }

            .btn-primary:hover {
                transform: translateY(-3px);
                box-shadow: 0 10px 30px rgba(13, 110, 253, 0.4);
            }

            .btn-secondary {
                background: var(--color-secondary);
                color: white;
            }

            .btn-secondary:hover {
                background: #5a6268;
                transform: translateY(-3px);
                box-shadow: 0 10px 30px rgba(108, 117, 125, 0.3);
            }

            .btn-outline {
                background: transparent;
                color: var(--color-primary);
                border: 2px solid var(--color-primary);
            }

            .btn-outline:hover {
                background: var(--color-primary);
                color: white;
                border-color: transparent;
            }

            .search-box {
                margin-top: 24px;
                padding-top: 24px;
                border-top: 1px solid #dee2e6;
            }

            .search-box p {
                font-size: 14px;
                color: var(--text-light);
                margin-bottom: 12px;
            }

            .search-form {
                display: flex;
                gap: 8px;
                max-width: 400px;
                margin: 0 auto;
            }

            .search-input {
                flex: 1;
                padding: 12px 16px;
                border: 2px solid #dee2e6;
                border-radius: 8px;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            .search-input:focus {
                outline: none;
                border-color: var(--color-primary);
            }

            .search-btn {
                padding: 12px 20px;
                background: var(--color-primary);
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.3s;
            }

            .search-btn:hover {
                transform: scale(1.05);
            }

            @media (max-width: 768px) {
                .error-code {
                    font-size: 80px;
                }

                .error-header h1 {
                    font-size: 20px;
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

                .search-form {
                    flex-direction: column;
                }
            }
        </style>
    </head>
    <body>
        <main class="error-container">
            <header class="error-header">
                <i class="fas fa-book error-icon icon-1"></i>
                <i class="fas fa-search error-icon icon-2"></i>
                <i class="fas fa-question error-icon icon-3"></i>
                <span class="error-code">404</span>
                <h1>Trang không tìm thấy</h1>
                <p>Oops! Trang bạn đang tìm kiếm không tồn tại</p>
            </header>
            
            <section class="error-content">
                <article class="error-message">
                    <h2>
                        <i class="fas fa-map-signs"></i>
                        Có vẻ bạn đã lạc đường!
                    </h2>
                    <p>
                        Trang bạn yêu cầu có thể đã bị xóa, đổi tên, hoặc tạm thời không khả dụng.
                    </p>
                    
                    <aside class="suggestions">
                        <h3>
                            <i class="fas fa-lightbulb"></i>
                            Gợi ý cho bạn:
                        </h3>
                        <ul>
                            <li><i class="fas fa-circle"></i> Kiểm tra lại đường dẫn URL</li>
                            <li><i class="fas fa-circle"></i> Quay về trang chủ và điều hướng lại</li>
                            <li><i class="fas fa-circle"></i> Sử dụng thanh tìm kiếm để tìm sách</li>
                            <li><i class="fas fa-circle"></i> Liên hệ hỗ trợ nếu cần giúp đỡ</li>
                        </ul>
                    </aside>
                </article>

                <nav class="error-actions">
                    <a href="javascript:history.back()" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i>
                        Quay lại
                    </a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                        <i class="fas fa-home"></i>
                        Về trang chủ
                    </a>
                    <a href="${pageContext.request.contextPath}/customer/books" class="btn btn-outline">
                        <i class="fas fa-book-open"></i>
                        Xem sách
                    </a>
                </nav>

                <section class="search-box">
                    <p>Hoặc tìm kiếm sách bạn cần:</p>
                    <form action="${pageContext.request.contextPath}/customer/books" method="get" class="search-form">
                        <input type="text" name="search" placeholder="Nhập tên sách, tác giả..." class="search-input">
                        <button type="submit" class="search-btn">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </section>
            </section>
        </main>
    </body>
</html>

