<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Voucher mới - Admin Bookify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/DuyHung.css">
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
            background: linear-gradient(135deg, var(--color-success) 0%, #146c43 100%);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            box-shadow: 0 8px 16px rgba(25, 135, 84, 0.3);
        }

        .page-title h1 {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
        }

        .page-title p {
            font-size: 14px;
            color: var(--text-light);
            margin-top: 4px;
        }

        /* Form Card */
        .form-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-md);
            overflow: hidden;
            animation: fadeInUp 0.5s ease 0.2s forwards;
            opacity: 0;
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .form-card-header {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 20px 32px;
            border-bottom: 1px solid #e9ecef;
        }

        .form-card-header h2 {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-card-header h2 i {
            color: var(--color-primary);
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

        .form-row.single {
            grid-template-columns: 1fr;
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

        /* Discount Type Cards */
        .discount-type-cards {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-bottom: 24px;
        }

        .discount-type-card {
            position: relative;
            padding: 20px;
            border: 2px solid #e9ecef;
            border-radius: 12px;
            cursor: pointer;
            transition: var(--transition);
            text-align: center;
        }

        .discount-type-card:hover {
            border-color: var(--color-primary);
            background: #f8f9ff;
        }

        .discount-type-card.active {
            border-color: var(--color-primary);
            background: linear-gradient(135deg, #e7f1ff 0%, #cce0ff 100%);
        }

        .discount-type-card input {
            position: absolute;
            opacity: 0;
        }

        .discount-type-card .card-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 12px;
            font-size: 20px;
        }

        .discount-type-card.percentage .card-icon {
            background: rgba(111, 66, 193, 0.1);
            color: #6f42c1;
        }

        .discount-type-card.fixed .card-icon {
            background: rgba(8, 66, 152, 0.1);
            color: #084298;
        }

        .discount-type-card.freeship .card-icon {
            background: rgba(25, 135, 84, 0.1);
            color: var(--color-success);
        }

        .discount-type-card .card-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 4px;
        }

        .discount-type-card .card-desc {
            font-size: 12px;
            color: var(--text-light);
        }

        /* Toggle Switch */
        .form-switch {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px 20px;
            background: #f8f9fa;
            border-radius: 10px;
        }

        .switch {
            position: relative;
            width: 52px;
            height: 28px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .switch-slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: #dee2e6;
            border-radius: 28px;
            transition: var(--transition);
        }

        .switch-slider::before {
            content: '';
            position: absolute;
            width: 22px;
            height: 22px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: var(--transition);
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }

        .switch input:checked + .switch-slider {
            background: var(--color-success);
        }

        .switch input:checked + .switch-slider::before {
            transform: translateX(24px);
        }

        .switch-label {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-main);
        }

        /* Form Actions */
        .form-actions {
            display: flex;
            gap: 16px;
            padding: 24px 32px;
            background: #f8f9fa;
            border-top: 1px solid #e9ecef;
        }

        .btn {
            padding: 14px 28px;
            border-radius: 10px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            transition: var(--transition);
            font-family: inherit;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--color-success) 0%, #146c43 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(25, 135, 84, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(25, 135, 84, 0.4);
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
            .discount-type-cards { grid-template-columns: 1fr; }
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
                <i class="fas fa-plus"></i>
            </div>
            <div class="page-title">
                <h1>Tạo Voucher mới</h1>
                <p>Thêm mã giảm giá mới cho cửa hàng</p>
            </div>
        </header>

        <!-- Form Card -->
        <section class="form-card">
            <form action="${pageContext.request.contextPath}/admin/vouchers" method="post" id="voucherForm">
                <input type="hidden" name="action" value="create">

                <div class="form-card-body">
                    <!-- Basic Info Section -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-info-circle"></i> Thông tin cơ bản
                        </legend>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">
                                    Mã voucher<span class="required">*</span>
                                </label>
                                <input type="text" name="code" class="form-input" required 
                                       placeholder="VD: SALE20, FREESHIP..." 
                                       style="text-transform: uppercase;">
                                <div class="form-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Mã sẽ tự động chuyển thành chữ in hoa
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Mô tả</label>
                                <input type="text" name="description" class="form-input" 
                                       placeholder="VD: Giảm 20% cho đơn hàng từ 300k">
                            </div>
                        </div>
                    </fieldset>

                    <!-- Discount Type Section -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-tag"></i> Loại giảm giá
                        </legend>

                        <div class="discount-type-cards">
                            <label class="discount-type-card percentage active" onclick="selectDiscountType('PERCENTAGE')">
                                <input type="radio" name="discountType" value="PERCENTAGE" checked>
                                <div class="card-icon"><i class="fas fa-percent"></i></div>
                                <div class="card-title">Phần trăm</div>
                                <div class="card-desc">Giảm theo % đơn hàng</div>
                            </label>
                            <label class="discount-type-card fixed" onclick="selectDiscountType('FIXED_AMOUNT')">
                                <input type="radio" name="discountType" value="FIXED_AMOUNT">
                                <div class="card-icon"><i class="fas fa-money-bill"></i></div>
                                <div class="card-title">Số tiền cố định</div>
                                <div class="card-desc">Giảm số tiền nhất định</div>
                            </label>
                            <label class="discount-type-card freeship" onclick="selectDiscountType('FREE_SHIPPING')">
                                <input type="radio" name="discountType" value="FREE_SHIPPING">
                                <div class="card-icon"><i class="fas fa-truck"></i></div>
                                <div class="card-title">Miễn phí ship</div>
                                <div class="card-desc">Miễn phí vận chuyển</div>
                            </label>
                        </div>

                        <div class="form-row" id="discountValueRow">
                            <div class="form-group">
                                <label class="form-label">
                                    Giá trị giảm<span class="required">*</span>
                                </label>
                                <input type="number" name="discountValue" id="discountValue" 
                                       class="form-input" required min="0" step="0.01" 
                                       placeholder="VD: 10 (cho 10%)">
                                <div class="form-hint" id="discountValueHint">
                                    <i class="fas fa-info-circle"></i>
                                    Nhập số phần trăm (VD: 10 = 10%)
                                </div>
                            </div>
                            <div class="form-group" id="maxDiscountGroup">
                                <label class="form-label">Giảm tối đa (₫)</label>
                                <input type="number" name="maxDiscount" class="form-input" 
                                       min="0" step="1000" placeholder="VD: 50000">
                                <div class="form-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Để trống = không giới hạn
                                </div>
                            </div>
                        </div>
                    </fieldset>

                    <!-- Conditions Section -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-sliders-h"></i> Điều kiện áp dụng
                        </legend>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Đơn hàng tối thiểu (₫)</label>
                                <input type="number" name="minOrderAmount" class="form-input" 
                                       min="0" step="1000" value="0" placeholder="VD: 200000">
                                <div class="form-hint">
                                    <i class="fas fa-info-circle"></i>
                                    Nhập 0 = áp dụng mọi đơn
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Số lượt sử dụng tối đa</label>
                                <input type="number" name="maxUses" class="form-input" 
                                       min="1" placeholder="VD: 100">
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
                                       min="1" value="1" placeholder="VD: 1">
                            </div>
                            <div class="form-group"></div>
                        </div>
                    </fieldset>

                    <!-- Time Section -->
                    <fieldset class="form-section">
                        <legend class="form-section-title">
                            <i class="fas fa-calendar-alt"></i> Thời gian hiệu lực
                        </legend>

                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">
                                    Ngày bắt đầu<span class="required">*</span>
                                </label>
                                <input type="datetime-local" name="startDate" class="form-input" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">
                                    Ngày kết thúc<span class="required">*</span>
                                </label>
                                <input type="datetime-local" name="endDate" class="form-input" required>
                            </div>
                        </div>
                    </fieldset>

                    <!-- Status Section -->
                    <fieldset class="form-section">
                        <div class="form-switch">
                            <label class="switch">
                                <input type="checkbox" name="isActive" checked>
                                <span class="switch-slider"></span>
                            </label>
                            <span class="switch-label">Kích hoạt voucher ngay sau khi tạo</span>
                        </div>
                    </fieldset>
                </div>

                <!-- Form Actions -->
                <footer class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check"></i> Tạo Voucher
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
        function selectDiscountType(type) {
            // Update active state
            document.querySelectorAll('.discount-type-card').forEach(card => {
                card.classList.remove('active');
            });
            event.currentTarget.classList.add('active');

            // Update form fields
            const valueInput = document.getElementById('discountValue');
            const valueHint = document.getElementById('discountValueHint');
            const maxDiscountGroup = document.getElementById('maxDiscountGroup');

            if (type === 'PERCENTAGE') {
                valueInput.placeholder = 'VD: 10 (cho 10%)';
                valueHint.innerHTML = '<i class="fas fa-info-circle"></i> Nhập số phần trăm (VD: 10 = 10%)';
                maxDiscountGroup.style.display = 'block';
                document.getElementById('discountValueRow').style.display = 'grid';
            } else if (type === 'FIXED_AMOUNT') {
                valueInput.placeholder = 'VD: 50000';
                valueHint.innerHTML = '<i class="fas fa-info-circle"></i> Nhập số tiền giảm (VNĐ)';
                maxDiscountGroup.style.display = 'none';
                document.getElementById('discountValueRow').style.display = 'grid';
            } else if (type === 'FREE_SHIPPING') {
                valueInput.value = '0';
                document.getElementById('discountValueRow').style.display = 'none';
            }
        }

        // Set default dates
        document.addEventListener('DOMContentLoaded', function() {
            const now = new Date();
            const startInput = document.querySelector('input[name="startDate"]');
            const endInput = document.querySelector('input[name="endDate"]');

            const formatDate = (date) => date.toISOString().slice(0, 16);

            startInput.value = formatDate(now);

            const endDate = new Date(now);
            endDate.setMonth(endDate.getMonth() + 1);
            endInput.value = formatDate(endDate);
        });
    </script>
</body>
</html>
