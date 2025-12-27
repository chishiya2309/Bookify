<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Voucher - Admin Bookify</title>
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
            --shadow-lg: 0 8px 24px rgba(0,0,0,0.12);
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

        .voucher-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 32px 24px;
        }

        /* Page Header */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
            animation: slideDown 0.5s ease;
        }

        @keyframes slideDown {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .page-title {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .page-title-icon {
            width: 56px;
            height: 56px;
            background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            box-shadow: 0 8px 16px rgba(13, 110, 253, 0.3);
        }

        .page-title h1 {
            font-size: 28px;
            font-weight: 700;
            color: var(--text-main);
        }

        .page-title p {
            font-size: 14px;
            color: var(--text-light);
            margin-top: 4px;
        }

        /* Buttons */
        .btn {
            padding: 12px 24px;
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
            background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(13, 110, 253, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(13, 110, 253, 0.4);
        }

        .btn-success { background: var(--color-success); color: white; }
        .btn-warning { background: var(--color-warning); color: var(--text-main); }
        .btn-danger { background: var(--color-error); color: white; }
        .btn-secondary { background: var(--color-secondary); color: white; }

        .btn-sm {
            padding: 8px 14px;
            font-size: 12px;
            border-radius: 8px;
        }

        .btn:hover {
            transform: translateY(-2px);
            filter: brightness(1.1);
        }

        /* Stats Cards */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
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
        .stat-card:nth-child(4) { animation-delay: 0.4s; }

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
        .stat-icon.danger { background: rgba(220, 53, 69, 0.1); color: var(--color-error); }

        .stat-info h3 {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
        }

        .stat-info p {
            font-size: 13px;
            color: var(--text-light);
            margin-top: 2px;
        }

        /* Messages */
        .alert {
            padding: 16px 20px;
            border-radius: 10px;
            margin-bottom: 24px;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 500;
            animation: slideIn 0.4s ease;
        }

        @keyframes slideIn {
            from { opacity: 0; transform: translateX(-20px); }
            to { opacity: 1; transform: translateX(0); }
        }

        .alert-success {
            background: linear-gradient(135deg, #d1e7dd 0%, #badbcc 100%);
            color: #0f5132;
            border-left: 4px solid var(--color-success);
        }

        .alert-error {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c2c7 100%);
            color: #842029;
            border-left: 4px solid var(--color-error);
        }

        /* Table Card */
        .table-card {
            background: var(--bg-white);
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-md);
            overflow: hidden;
            animation: fadeInUp 0.5s ease 0.3s forwards;
            opacity: 0;
        }

        .table-header {
            padding: 20px 24px;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-header h2 {
            font-size: 18px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .table-header h2 i {
            color: var(--color-primary);
        }

        /* Voucher Table */
        .voucher-table {
            width: 100%;
            border-collapse: collapse;
        }

        .voucher-table th {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 16px 20px;
            text-align: left;
            font-weight: 600;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--text-light);
            border-bottom: 2px solid #dee2e6;
        }

        .voucher-table td {
            padding: 18px 20px;
            border-bottom: 1px solid #f0f0f0;
            vertical-align: middle;
        }

        .voucher-table tbody tr {
            transition: var(--transition);
        }

        .voucher-table tbody tr:hover {
            background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
        }

        .voucher-table tbody tr:last-child td {
            border-bottom: none;
        }

        /* Voucher Code Badge */
        .voucher-code {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: linear-gradient(135deg, #e7f1ff 0%, #cce0ff 100%);
            color: var(--color-primary);
            padding: 8px 14px;
            border-radius: 8px;
            font-family: 'SF Mono', 'Monaco', monospace;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 0.5px;
        }

        .voucher-code i {
            font-size: 12px;
            opacity: 0.7;
        }

        /* Discount Type Badges */
        .badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.3px;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .badge i { font-size: 10px; }

        .badge-percentage {
            background: linear-gradient(135deg, #e0cffc 0%, #d0bfff 100%);
            color: #6f42c1;
        }

        .badge-fixed {
            background: linear-gradient(135deg, #cfe2ff 0%, #b6d4fe 100%);
            color: #084298;
        }

        .badge-freeship {
            background: linear-gradient(135deg, #d1e7dd 0%, #badbcc 100%);
            color: #0f5132;
        }

        .badge-active {
            background: linear-gradient(135deg, #d1e7dd 0%, #a3d4b5 100%);
            color: var(--color-success);
        }

        .badge-inactive {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c2c7 100%);
            color: var(--color-error);
        }

        .badge-expired {
            background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
            color: var(--text-light);
        }

        /* Discount Value Display */
        .discount-value {
            font-weight: 700;
            color: var(--text-main);
            font-size: 15px;
        }

        .discount-value small {
            display: block;
            font-weight: 400;
            font-size: 11px;
            color: var(--text-light);
            margin-top: 2px;
        }

        /* Usage Progress */
        .usage-display {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .usage-text {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-main);
        }

        .usage-bar {
            width: 80px;
            height: 6px;
            background: #e9ecef;
            border-radius: 3px;
            overflow: hidden;
        }

        .usage-bar-fill {
            height: 100%;
            background: linear-gradient(90deg, var(--color-primary) 0%, var(--color-success) 100%);
            border-radius: 3px;
            transition: width 0.5s ease;
        }

        /* Date Display */
        .date-display {
            font-size: 12px;
            color: var(--text-light);
        }

        .date-display div {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 3px 0;
        }

        .date-display i {
            width: 14px;
        }

        .date-display .start { color: var(--color-success); }
        .date-display .end { color: var(--color-error); }

        /* Actions */
        .actions {
            display: flex;
            gap: 8px;
        }

        .action-btn {
            width: 36px;
            height: 36px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: none;
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            font-size: 14px;
        }

        .action-btn:hover {
            transform: scale(1.1);
        }

        .action-btn.edit {
            background: rgba(255, 193, 7, 0.15);
            color: #d39e00;
        }

        .action-btn.toggle {
            background: rgba(108, 117, 125, 0.15);
            color: var(--color-secondary);
        }

        .action-btn.toggle.activate {
            background: rgba(25, 135, 84, 0.15);
            color: var(--color-success);
        }

        .action-btn.delete {
            background: rgba(220, 53, 69, 0.15);
            color: var(--color-error);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 80px 40px;
        }

        .empty-state-icon {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #f0f4ff 0%, #e0e8ff 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px;
        }

        .empty-state-icon i {
            font-size: 40px;
            color: var(--color-primary);
            opacity: 0.6;
        }

        .empty-state h3 {
            font-size: 20px;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 8px;
        }

        .empty-state p {
            color: var(--text-light);
            margin-bottom: 24px;
        }

        /* Responsive */
        @media (max-width: 1200px) {
            .stats-row { grid-template-columns: repeat(2, 1fr); }
        }

        @media (max-width: 768px) {
            .stats-row { grid-template-columns: 1fr; }
            .page-header { flex-direction: column; gap: 16px; align-items: flex-start; }
            .voucher-table { font-size: 13px; }
            .voucher-table th, .voucher-table td { padding: 12px; }
        }

        /* Tooltip */
        [data-tooltip] {
            position: relative;
        }

        [data-tooltip]:hover::after {
            content: attr(data-tooltip);
            position: absolute;
            bottom: 100%;
            left: 50%;
            transform: translateX(-50%);
            background: var(--text-main);
            color: white;
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 12px;
            white-space: nowrap;
            z-index: 100;
            margin-bottom: 8px;
        }
    </style>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp"/>
    
    <main class="voucher-container">
        <!-- Page Header -->
        <header class="page-header">
            <div class="page-title">
                <div class="page-title-icon">
                    <i class="fas fa-ticket-alt"></i>
                </div>
                <div>
                    <h1>Quản lý Voucher</h1>
                    <p>Tạo và quản lý mã giảm giá cho cửa hàng</p>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/admin/vouchers?action=showCreate" class="btn btn-primary">
                <i class="fas fa-plus"></i> Tạo Voucher mới
            </a>
        </header>

        <!-- Stats Row -->
        <section class="stats-row">
            <article class="stat-card">
                <div class="stat-icon primary">
                    <i class="fas fa-tags"></i>
                </div>
                <div class="stat-info">
                    <h3>${vouchers.size()}</h3>
                    <p>Tổng voucher</p>
                </div>
            </article>
            <article class="stat-card">
                <div class="stat-icon success">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-info">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="v" items="${vouchers}">
                        <c:if test="${v.active && v.currentlyValid}">
                            <c:set var="activeCount" value="${activeCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    <h3>${activeCount}</h3>
                    <p>Đang hoạt động</p>
                </div>
            </article>
            <article class="stat-card">
                <div class="stat-icon warning">
                    <i class="fas fa-clock"></i>
                </div>
                <div class="stat-info">
                    <c:set var="expiredCount" value="0"/>
                    <c:forEach var="v" items="${vouchers}">
                        <c:if test="${v.expired}">
                            <c:set var="expiredCount" value="${expiredCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    <h3>${expiredCount}</h3>
                    <p>Đã hết hạn</p>
                </div>
            </article>
            <article class="stat-card">
                <div class="stat-icon danger">
                    <i class="fas fa-ban"></i>
                </div>
                <div class="stat-info">
                    <c:set var="inactiveCount" value="0"/>
                    <c:forEach var="v" items="${vouchers}">
                        <c:if test="${!v.active}">
                            <c:set var="inactiveCount" value="${inactiveCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    <h3>${inactiveCount}</h3>
                    <p>Vô hiệu hóa</p>
                </div>
            </article>
        </section>

        <!-- Alerts -->
        <c:if test="${not empty message}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <c:out value="${message}"/>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <c:out value="${errorMessage}"/>
            </div>
        </c:if>

        <!-- Table Card -->
        <section class="table-card">
            <header class="table-header">
                <h2><i class="fas fa-list"></i> Danh sách Voucher</h2>
            </header>

            <c:choose>
                <c:when test="${empty vouchers}">
                    <article class="empty-state">
                        <div class="empty-state-icon">
                            <i class="fas fa-ticket-alt"></i>
                        </div>
                        <h3>Chưa có voucher nào</h3>
                        <p>Bắt đầu tạo voucher để thu hút khách hàng!</p>
                        <a href="${pageContext.request.contextPath}/admin/vouchers?action=showCreate" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Tạo Voucher đầu tiên
                        </a>
                    </article>
                </c:when>
                <c:otherwise>
                    <table class="voucher-table">
                        <thead>
                            <tr>
                                <th>Mã Voucher</th>
                                <th>Mô tả</th>
                                <th>Loại</th>
                                <th>Giá trị</th>
                                <th>Điều kiện</th>
                                <th>Sử dụng</th>
                                <th>Thời gian</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="voucher" items="${vouchers}">
                                <tr>
                                    <td>
                                        <span class="voucher-code">
                                            <i class="fas fa-ticket-alt"></i>
                                            <c:out value="${voucher.code}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${voucher.description}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${voucher.discountType == 'PERCENTAGE'}">
                                                <span class="badge badge-percentage">
                                                    <i class="fas fa-percent"></i> Phần trăm
                                                </span>
                                            </c:when>
                                            <c:when test="${voucher.discountType == 'FIXED_AMOUNT'}">
                                                <span class="badge badge-fixed">
                                                    <i class="fas fa-money-bill"></i> Số tiền
                                                </span>
                                            </c:when>
                                            <c:when test="${voucher.discountType == 'FREE_SHIPPING'}">
                                                <span class="badge badge-freeship">
                                                    <i class="fas fa-truck"></i> Free Ship
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="discount-value">
                                            <c:choose>
                                                <c:when test="${voucher.discountType == 'PERCENTAGE'}">
                                                    ${voucher.discountValue}%
                                                    <c:if test="${not empty voucher.maxDiscount}">
                                                        <small>Tối đa: <fmt:formatNumber value="${voucher.maxDiscount}" pattern="#,###"/>₫</small>
                                                    </c:if>
                                                </c:when>
                                                <c:when test="${voucher.discountType == 'FIXED_AMOUNT'}">
                                                    <fmt:formatNumber value="${voucher.discountValue}" pattern="#,###"/>₫
                                                </c:when>
                                                <c:when test="${voucher.discountType == 'FREE_SHIPPING'}">
                                                    Miễn phí ship
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <c:if test="${voucher.minOrderAmount > 0}">
                                            <small>Đơn từ: <fmt:formatNumber value="${voucher.minOrderAmount}" pattern="#,###"/>₫</small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <div class="usage-display">
                                            <span class="usage-text">
                                                ${voucher.currentUses}
                                                <c:choose>
                                                    <c:when test="${not empty voucher.maxUses}">/ ${voucher.maxUses}</c:when>
                                                    <c:otherwise>/ ∞</c:otherwise>
                                                </c:choose>
                                            </span>
                                            <c:if test="${not empty voucher.maxUses && voucher.maxUses > 0}">
                                                <div class="usage-bar">
                                                    <div class="usage-bar-fill" style="width: ${(voucher.currentUses / voucher.maxUses) * 100}%"></div>
                                                </div>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="date-display">
                                            <div><i class="fas fa-play start"></i> ${voucher.startDate.toLocalDate()}</div>
                                            <div><i class="fas fa-stop end"></i> ${voucher.endDate.toLocalDate()}</div>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${voucher.active && voucher.currentlyValid}">
                                                <span class="badge badge-active">
                                                    <i class="fas fa-check"></i> Hoạt động
                                                </span>
                                            </c:when>
                                            <c:when test="${!voucher.active}">
                                                <span class="badge badge-inactive">
                                                    <i class="fas fa-ban"></i> Vô hiệu
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-expired">
                                                    <i class="fas fa-clock"></i> Hết hạn
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <nav class="actions">
                                            <a href="${pageContext.request.contextPath}/admin/vouchers?action=showUpdate&id=${voucher.voucherId}" 
                                               class="action-btn edit" data-tooltip="Sửa">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/vouchers?action=toggleStatus&id=${voucher.voucherId}" 
                                               class="action-btn toggle ${!voucher.active ? 'activate' : ''}"
                                               data-tooltip="${voucher.active ? 'Vô hiệu hóa' : 'Kích hoạt'}">
                                                <i class="fas ${voucher.active ? 'fa-ban' : 'fa-check'}"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/vouchers?action=delete&id=${voucher.voucherId}" 
                                               class="action-btn delete" data-tooltip="Xóa"
                                               onclick="return confirm('Bạn có chắc muốn xóa voucher ${voucher.code}?');">
                                                <i class="fas fa-trash"></i>
                                            </a>
                                        </nav>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
    
    <jsp:include page="/admin/footer_admin.jsp"/>
</body>
</html>
