<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa Voucher - Admin Bookify</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --color-primary: #0D6EFD;
            --color-primary-dark: #0b5ed7;
            --color-secondary: #6C757D;
            --color-accent: #FF9900;
            --bg-body: #F8F9FA;
            --bg-white: #FFFFFF;
            --text-main: #212529;
            --text-light: #6C757D;
            --color-success: #198754;
            --color-error: #DC3545;
            --color-warning: #FFC107;
            --shadow-sm: 0 2px 4px rgba(0,0,0,0.05);
            --shadow-md: 0 4px 12px rgba(0,0,0,0.1);
            --border-radius: 12px;
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        /* Admin Header Styles (Internal) */
        .admin-header {
            text-align: center;
            padding: 30px 0 20px 0;
            background-color: var(--bg-white);
            border-bottom: 1px solid #dee2e6;
        }
        .admin-logo h1 {
            margin: 0;
            font-size: 24px;
            font-style: italic;
            font-weight: bold;
            color: var(--color-primary);
            text-transform: uppercase;
        }
        .admin-logo a {
            text-decoration: none;
        }
        .admin-welcome {
            font-size: 16px;
            margin: 15px 0 25px 0;
            color: var(--text-main);
        }
        .admin-welcome .email {
            font-weight: bold;
            color: var(--color-primary);
        }
        .logout-btn {
            margin-left: 15px;
            text-decoration: underline;
            color: var(--color-secondary);
            cursor: pointer;
        }
        .logout-btn:hover {
            color: var(--color-error);
        }
        .admin-menu {
            display: flex;
            justify-content: center;
            gap: 40px;
            padding-bottom: 10px;
        }
        .admin-menu a {
            font-size: 18px;
            font-weight: bold;
            text-decoration: none;
            color: var(--text-main);
        }
        .admin-menu a:hover {
            color: var(--color-primary);
        }

        /* Footer Admin Styles */
        .footer-container,
        .site-footer {
            text-align: center;
            margin-top: 40px;
            padding: 30px 0;
            width: 100%;
            background-color: var(--bg-white);
            border-top: 1px solid var(--color-secondary);
            color: var(--text-light);
        }

        /* Reset fieldset to remove default borders */
        fieldset.form-section {
            border: none;
            padding: 0;
            margin: 0 0 32px 0;
        }
        fieldset.form-section:last-child {
            margin-bottom: 0;
        }
        legend.form-section-title {
            width: 100%;
            padding: 0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
            color: var(--text-main);
            min-height: 100vh;
        }

        .form-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 32px 24px;
        }

        /* Page Header */
        .page-header {
            display: flex;
            align-items: center;
            gap: 20px;
            margin-bottom: 32px;
            animation: slideDown 0.5s ease;
        }

        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .back-btn {
            width: 48px;
            height: 48px;
            background: var(--bg-white);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--text-light);
            text-decoration: none;
            box-shadow: var(--shadow-sm);
            transition: var(--transition);
        }

        .back-btn:hover {
            color: var(--color-primary);
            transform: translateX(-4px);
            box-shadow: var(--shadow-md);
        }

        .page-title-icon {
            width: 56px;
            height: 56px;
            background: linear-gradient(135deg, var(--color-warning) 0%, #d39e00 100%);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--text-main);
            font-size: 24px;
            box-shadow: 0 8px 16px rgba(255, 193, 7, 0.3);
        }

        .page-title h1 {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .voucher-code-badge {
            background: linear-gradient(135deg, #e7f1ff 0%, #cce0ff 100%);
            color: var(--color-primary);
            padding: 6px 14px;
            border-radius: 8px;
            font-family: 'SF Mono', 'Monaco', monospace;
            font-size: 14px;
            font-weight: 700;
        }

        .page-title p {
            font-size: 14px;
            color: var(--text-light);
            margin-top: 4px;
        }

        /* Stats Cards */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-bottom: 32px;
        }

        .stat-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            padding: 24px;
            box-shadow: var(--shadow-sm);
            display: flex;
            align-items: center;
            gap: 16px;
            transition: var(--transition);
            animation: fadeInUp 0.5s ease forwards;
            opacity: 0;
        }

        .stat-card:nth-child(1) { animation-delay: 0.1s; }
        .stat-card:nth-child(2) { animation-delay: 0.2s; }
        .stat-card:nth-child(3) { animation-delay: 0.3s; }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .stat-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-md);
        }

        .stat-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .stat-icon.primary { background: rgba(13, 110, 253, 0.1); color: var(--color-primary); }
        .stat-icon.success { background: rgba(25, 135, 84, 0.1); color: var(--color-success); }
        .stat-icon.warning { background: rgba(255, 193, 7, 0.15); color: #d39e00; }

        .stat-info h3 {
            font-size: 20px;
            font-weight: 700;
            color: var(--text-main);
        }

        .stat-info p {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 2px;
        }

        /* Form Card - Override external CSS */
        .form-card {
            background: var(--bg-white) !important;
            border-radius: var(--border-radius) !important;
            box-shadow: var(--shadow-md) !important;
            overflow: hidden !important;
            animation: fadeInUp 0.5s ease 0.3s forwards !important;
            opacity: 0;
            max-width: none !important;
            margin: 0 !important;
            padding: 0 !important;
            border: none !important;
            text-align: left !important;
        }
        .form-card label {
            display: block !important;
            width: auto !important;
        }
        .form-card input[type="text"] {
            width: 100% !important;
        }

        .form-card-header {
            background: linear-gradient(135deg, #fff8e6 0%, #fff3cd 100%);
            padding: 20px 32px;
            border-bottom: 1px solid #ffe69c;
        }

        .form-card-header h2 {
            font-size: 16px;
            font-weight: 600;
            color: #664d03;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-card-header h2 i {
            color: var(--color-warning);
        }

        .form-card-body {
            padding: 32px;
        }

        /* Form Elements */
        .form-section {
            margin-bottom: 32px;
        }

        .form-section:last-child {
            margin-bottom: 0;
        }

        .form-section-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-light);
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-section-title::after {
            content: '';
            flex: 1;
            height: 1px;
            background: #e9ecef;
        }

        .form-row {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 24px;
        }

        .form-group {
            margin-bottom: 24px;
        }

        .form-group:last-child {
            margin-bottom: 0;
        }

        .form-label {
            display: block;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 10px;
            font-size: 14px;
        }

        .form-label .required {
            color: var(--color-error);
            margin-left: 4px;
        }

        .form-input,
        .form-select,
        .form-textarea {
            width: 100%;
            padding: 14px 18px;
            border: 2px solid #e9ecef;
            border-radius: 10px;
            font-size: 14px;
            font-family: inherit;
            transition: var(--transition);
            background: var(--bg-white);
        }

        .form-input:focus,
        .form-select:focus,
        .form-textarea:focus {
            outline: none;
            border-color: var(--color-primary);
            box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.1);
        }

        .form-input:disabled {
            background: #e9ecef;
            color: var(--text-light);
            cursor: not-allowed;
        }

        .form-input::placeholder {
            color: #adb5bd;
        }

        .form-hint {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 8px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .form-hint i {
            font-size: 10px;
        }

        /* Toggle Switch - Override external CSS */
        .form-switch {
            display: flex !important;
            align-items: center !important;
            gap: 16px !important;
            padding: 16px 20px !important;
            background: #f8f9fa !important;
            border-radius: 10px !important;
        }

        .switch {
            position: relative !important;
            width: 52px !important;
            min-width: 52px !important;
            height: 28px !important;
            flex-shrink: 0 !important;
            display: inline-block !important;
        }

        .switch input {
            opacity: 0 !important;
            width: 0 !important;
            height: 0 !important;
            position: absolute !important;
        }

        .switch-slider {
            position: absolute !important;
            cursor: pointer !important;
            top: 0 !important;
            left: 0 !important;
            right: 0 !important;
            bottom: 0 !important;
            background: #dee2e6 !important;
            border-radius: 28px !important;
            transition: var(--transition) !important;
        }

        .switch-slider::before {
            content: '' !important;
            position: absolute !important;
            width: 22px !important;
            height: 22px !important;
            left: 3px !important;
            bottom: 3px !important;
            background: white !important;
            border-radius: 50% !important;
            transition: var(--transition) !important;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2) !important;
        }

        .switch input:checked + .switch-slider {
            background: var(--color-success) !important;
        }

        .switch input:checked + .switch-slider::before {
            transform: translateX(24px) !important;
        }

        .switch-label {
            font-size: 14px !important;
            font-weight: 500 !important;
            color: var(--text-main) !important;
        }

        /* Form Actions - Override external CSS */
        .form-actions {
            display: flex !important;
            gap: 16px !important;
            padding: 24px 32px !important;
            background: #f8f9fa !important;
            border-top: 1px solid #e9ecef !important;
            margin-top: 0 !important;
            text-align: left !important;
        }

        .btn {
            padding: 14px 28px !important;
            border-radius: 10px !important;
            font-size: 14px !important;
            font-weight: 600 !important;
            cursor: pointer !important;
            border: none !important;
            text-decoration: none !important;
            display: inline-flex !important;
            align-items: center !important;
            gap: 10px !important;
            transition: var(--transition) !important;
            font-family: inherit !important;
            background: transparent;
        }

        .btn-warning {
            background: linear-gradient(135deg, var(--color-warning) 0%, #d39e00 100%);
            color: var(--text-main);
            box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
        }

        .btn-warning:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
        }

        .btn-secondary {
            background: var(--bg-white);
            color: var(--text-main);
            border: 2px solid #dee2e6;
        }

        .btn-secondary:hover {
            background: #f8f9fa;
            border-color: #adb5bd;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .form-row { grid-template-columns: 1fr; }
            .stats-row { grid-template-columns: 1fr; }
            .form-actions { flex-direction: column; }
            .btn { width: 100%; justify-content: center; }
        }
    </style>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp"/>
    
    <main class="form-container">
        <!-- Page Header -->
        <header class="page-header">
            <a href="${pageContext.request.contextPath}/admin/vouchers" class="back-btn">
                <i class="fas fa-arrow-left"></i>
            </a>
            <div class="page-title-icon">
                <i class="fas fa-edit"></i>
            </div>
            <div class="page-title">
                <h1>
                    Chỉnh sửa Voucher
                    <span class="voucher-code-badge">${voucher.code}</span>
                </h1>
                <p>Cập nhật thông tin mã giảm giá</p>
            </div>
        </header>

        <!-- Stats Row -->
        <section class="stats-row">
            <article class="stat-card">
                <div class="stat-icon primary">
                    <i class="fas fa-chart-line"></i>
                </div>
                <div class="stat-info">
                    <h3>${voucher.currentUses} lượt</h3>
                    <p>Đã sử dụng</p>
                </div>
            </article>
            <article class="stat-card">
                <div class="stat-icon warning">
                    <i class="fas fa-bullseye"></i>
                </div>
                <div class="stat-info">
                    <h3>
                        <c:choose>
                            <c:when test="${not empty voucher.maxUses}">${voucher.maxUses} lượt</c:when>
                            <c:otherwise>Không giới hạn</c:otherwise>
                        </c:choose>
                    </h3>
                    <p>Giới hạn</p>
                </div>
            </article>
            <article class="stat-card">
                <div class="stat-icon ${voucher.active ? 'success' : 'warning'}">
                    <i class="fas ${voucher.active ? 'fa-check-circle' : 'fa-pause-circle'}"></i>
                </div>
                <div class="stat-info">
                    <h3 style="color: ${voucher.active ? 'var(--color-success)' : 'var(--color-warning)' };">
                        ${voucher.active ? 'Hoạt động' : 'Vô hiệu'}
                    </h3>
                    <p>Trạng thái</p>
                </div>
            </article>
        </section>

        <!-- Form Card -->
        <section class="form-card">
            <header class="form-card-header">
                <h2><i class="fas fa-pencil-alt"></i> Chỉnh sửa thông tin</h2>
            </header>
            
            <form action="${pageContext.request.contextPath}/admin/vouchers" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${voucher.voucherId}">
                <input type="hidden" name="code" value="${voucher.code}">

                <div class="form-card-body">
                    <!-- Basic Info -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-info-circle"></i> Thông tin cơ bản
                        </legend>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Mã voucher</label>
                                <input type="text" class="form-input" value="${voucher.code}" 
                                       disabled style="text-transform: uppercase; font-weight: 700;">
                                <div class="form-hint">
                                    <i class="fas fa-lock"></i>
                                    Không thể thay đổi mã voucher
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Mô tả</label>
                                <input type="text" name="description" class="form-input" 
                                       value="${voucher.description}">
                            </div>
                        </div>
                    </fieldset>

                    <!-- Discount Section -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-tag"></i> Loại giảm giá
                        </legend>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">
                                    Loại giảm giá<span class="required">*</span>
                                </label>
                                <select name="discountType" id="discountType" class="form-select" required 
                                        onchange="updateDiscountInfo()">
                                    <option value="PERCENTAGE" ${voucher.discountType == 'PERCENTAGE' ? 'selected' : ''}>
                                        Giảm theo phần trăm (%)
                                    </option>
                                    <option value="FIXED_AMOUNT" ${voucher.discountType == 'FIXED_AMOUNT' ? 'selected' : ''}>
                                        Giảm số tiền cố định
                                    </option>
                                    <option value="FREE_SHIPPING" ${voucher.discountType == 'FREE_SHIPPING' ? 'selected' : ''}>
                                        Miễn phí vận chuyển
                                    </option>
                                </select>
                            </div>
                            <div class="form-group" id="discountValueGroup">
                                <label class="form-label">
                                    Giá trị giảm<span class="required">*</span>
                                </label>
                                <input type="number" name="discountValue" id="discountValue" 
                                       class="form-input" required min="0" step="0.01" 
                                       value="${voucher.discountValue}">
                            </div>
                        </div>

                        <div class="form-row" id="maxDiscountRow">
                            <div class="form-group">
                                <label class="form-label">Giảm tối đa (₫)</label>
                                <input type="number" name="maxDiscount" class="form-input" 
                                       min="0" step="1000" value="${voucher.maxDiscount}">
                                <div class="form-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Để trống = không giới hạn
                                </div>
                            </div>
                            <div class="form-group"></div>
                        </div>
                    </fieldset>

                    <!-- Conditions -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-sliders-h"></i> Điều kiện áp dụng
                        </legend>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Đơn hàng tối thiểu (₫)</label>
                                <input type="number" name="minOrderAmount" class="form-input" 
                                       min="0" step="1000" value="${voucher.minOrderAmount}">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Số lượt sử dụng tối đa</label>
                                <input type="number" name="maxUses" class="form-input" 
                                       min="1" value="${voucher.maxUses}">
                                <div class="form-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Để trống = không giới hạn
                                </div>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Số lần/khách hàng</label>
                                <input type="number" name="maxUsesPerUser" class="form-input" 
                                       min="1" value="${voucher.maxUsesPerUser}">
                            </div>
                            <div class="form-group"></div>
                        </div>
                    </fieldset>

                    <!-- Time -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-calendar-alt"></i> Thời gian hiệu lực
                        </legend>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">
                                    Ngày bắt đầu<span class="required">*</span>
                                </label>
                                <input type="datetime-local" name="startDate" class="form-input" required
                                       value="${voucher.startDate.toString().substring(0, 16)}">
                            </div>
                            <div class="form-group">
                                <label class="form-label">
                                    Ngày kết thúc<span class="required">*</span>
                                </label>
                                <input type="datetime-local" name="endDate" class="form-input" required
                                       value="${voucher.endDate.toString().substring(0, 16)}">
                            </div>
                        </div>
                    </fieldset>

                    <!-- Status -->
                    <fieldset class="form-section">
                        <div class="form-switch">
                            <label class="switch">
                                <input type="checkbox" name="isActive" ${voucher.active ? 'checked' : ''}>
                                <span class="switch-slider"></span>
                            </label>
                            <span class="switch-label">Voucher đang hoạt động</span>
                        </div>
                    </fieldset>
                </div>

                <!-- Form Actions -->
                <footer class="form-actions">
                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-save"></i> Lưu thay đổi
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Hủy bỏ
                    </a>
                </footer>
            </form>
        </section>
    </main>
    
    <jsp:include page="/admin/footer_admin.jsp"/>

    <script>
        function updateDiscountInfo() {
            const type = document.getElementById('discountType').value;
            const maxDiscountRow = document.getElementById('maxDiscountRow');
            const discountValueGroup = document.getElementById('discountValueGroup');

            if (type === 'PERCENTAGE') {
                maxDiscountRow.style.display = 'grid';
                discountValueGroup.style.display = 'block';
            } else if (type === 'FIXED_AMOUNT') {
                maxDiscountRow.style.display = 'none';
                discountValueGroup.style.display = 'block';
            } else if (type === 'FREE_SHIPPING') {
                maxDiscountRow.style.display = 'none';
                discountValueGroup.style.display = 'none';
            }
        }

        // Initialize on load
        document.addEventListener('DOMContentLoaded', updateDiscountInfo);
    </script>
</body>
</html>
