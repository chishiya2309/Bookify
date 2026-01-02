<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê - Bookify Admin</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f0f2f5;
            color: #333;
        }

        .stats-container {
            padding: 24px 30px;
            max-width: 1600px;
            margin: 0 auto;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .page-header h2 {
            color: #1a237e;
            font-size: 26px;
            font-weight: 700;
        }

        .page-header .date-info {
            color: #666;
            font-size: 14px;
        }

        /* ===== KPI Cards ===== */
        .kpi-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 24px;
        }

        .kpi-card {
            background: white;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            position: relative;
            overflow: hidden;
        }

        .kpi-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
        }

        .kpi-card.revenue::before { background: linear-gradient(135deg, #667eea, #764ba2); }
        .kpi-card.orders::before { background: linear-gradient(135deg, #f093fb, #f5576c); }
        .kpi-card.customers::before { background: linear-gradient(135deg, #4facfe, #00f2fe); }
        .kpi-card.avg::before { background: linear-gradient(135deg, #43e97b, #38f9d7); }

        .kpi-card .icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 16px;
        }

        .kpi-card.revenue .icon { background: rgba(102, 126, 234, 0.1); color: #667eea; }
        .kpi-card.orders .icon { background: rgba(245, 87, 108, 0.1); color: #f5576c; }
        .kpi-card.customers .icon { background: rgba(79, 172, 254, 0.1); color: #4facfe; }
        .kpi-card.avg .icon { background: rgba(67, 233, 123, 0.1); color: #43e97b; }

        .kpi-card .label {
            font-size: 13px;
            color: #888;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 8px;
        }

        .kpi-card .value {
            font-size: 28px;
            font-weight: 700;
            color: #1a237e;
        }

        /* ===== Revenue Summary ===== */
        .revenue-summary {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-bottom: 24px;
        }

        .revenue-card {
            background: linear-gradient(135deg, #1a237e, #3949ab);
            border-radius: 12px;
            padding: 20px 24px;
            color: white;
        }

        .revenue-card .label {
            font-size: 13px;
            opacity: 0.85;
            margin-bottom: 8px;
        }

        .revenue-card .value {
            font-size: 24px;
            font-weight: 700;
        }

        /* ===== Charts Section ===== */
        .charts-grid {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr;
            gap: 20px;
            margin-bottom: 24px;
        }

        .chart-card {
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        }

        .chart-card h3 {
            font-size: 16px;
            color: #1a237e;
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 1px solid #eee;
        }

        .chart-container {
            position: relative;
            height: 280px;
        }

        /* ===== Tables Section ===== */
        .tables-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 24px;
        }

        .table-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .table-card h3 {
            font-size: 16px;
            color: #1a237e;
            padding: 16px 20px;
            border-bottom: 1px solid #eee;
            margin: 0;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .data-table th {
            background: #f8f9fa;
            padding: 12px 16px;
            text-align: left;
            font-size: 12px;
            font-weight: 600;
            color: #666;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .data-table td {
            padding: 12px 16px;
            border-bottom: 1px solid #f0f0f0;
            font-size: 13px;
        }

        .data-table tr:hover {
            background-color: #f8f9ff;
        }

        .data-table tr:last-child td {
            border-bottom: none;
        }

        .book-info {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .book-info img {
            width: 40px;
            height: 55px;
            object-fit: cover;
            border-radius: 4px;
            background: #f0f0f0;
        }

        .book-info .title {
            font-weight: 500;
            color: #333;
            max-width: 200px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .amount {
            font-weight: 600;
            color: #2e7d32;
        }

        .count-badge {
            display: inline-block;
            padding: 4px 10px;
            background: #e8eaf6;
            color: #3949ab;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }

        /* Status Badges */
        .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
        }

        .status-pending { background: #fff3cd; color: #856404; }
        .status-processing { background: #cce5ff; color: #004085; }
        .status-shipped { background: #d1ecf1; color: #0c5460; }
        .status-delivered { background: #d4edda; color: #155724; }
        .status-cancelled { background: #f8d7da; color: #721c24; }

        /* Low Stock Alert */
        .low-stock {
            color: #c62828;
            font-weight: 600;
        }

        .stock-warning {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            color: #c62828;
            font-weight: 600;
        }

        .stock-warning::before {
            content: '⚠️';
        }

        /* Empty State */
        .empty-state {
            padding: 30px;
            text-align: center;
            color: #999;
        }

        /* Responsive */
        @media (max-width: 1200px) {
            .kpi-grid { grid-template-columns: repeat(2, 1fr); }
            .charts-grid { grid-template-columns: 1fr; }
            .tables-grid { grid-template-columns: 1fr; }
        }

        @media (max-width: 768px) {
            .kpi-grid { grid-template-columns: 1fr; }
            .revenue-summary { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp" />

    <div class="stats-container">
        <div class="page-header">
            <h2>📊 Thống kê tổng quan</h2>
            <%
                // Use Vietnam timezone for correct display on Render (UTC server)
                java.time.LocalDateTime vietnamNow = com.bookstore.config.VietnamTimeConfig.now();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            %>
            <span class="date-info">Cập nhật: <%= vietnamNow.format(formatter) %></span>
        </div>

        <!-- KPI Cards -->
        <div class="kpi-grid">
            <div class="kpi-card revenue">
                <div class="icon">💰</div>
                <div class="label">Tổng doanh thu</div>
                <div class="value"><fmt:formatNumber value="${totalRevenue}" pattern="#,###"/>₫</div>
            </div>
            <div class="kpi-card orders">
                <div class="icon">📦</div>
                <div class="label">Tổng đơn hàng</div>
                <div class="value"><fmt:formatNumber value="${totalOrders}" pattern="#,###"/></div>
            </div>
            <div class="kpi-card customers">
                <div class="icon">👥</div>
                <div class="label">Tổng khách hàng</div>
                <div class="value"><fmt:formatNumber value="${totalCustomers}" pattern="#,###"/></div>
            </div>
            <div class="kpi-card avg">
                <div class="icon">📈</div>
                <div class="label">Giá trị đơn TB</div>
                <div class="value"><fmt:formatNumber value="${avgOrderValue}" pattern="#,###"/>₫</div>
            </div>
        </div>

        <!-- Revenue Summary -->
        <div class="revenue-summary">
            <div class="revenue-card">
                <div class="label">🌅 Doanh thu hôm nay</div>
                <div class="value"><fmt:formatNumber value="${todayRevenue}" pattern="#,###"/>₫</div>
            </div>
            <div class="revenue-card">
                <div class="label">📅 Doanh thu tháng này</div>
                <div class="value"><fmt:formatNumber value="${thisMonthRevenue}" pattern="#,###"/>₫</div>
            </div>
            <div class="revenue-card">
                <div class="label">📆 Doanh thu năm ${currentYear}</div>
                <div class="value"><fmt:formatNumber value="${thisYearRevenue}" pattern="#,###"/>₫</div>
            </div>
        </div>

        <!-- Charts -->
        <div class="charts-grid">
            <div class="chart-card">
                <h3>📈 Doanh thu theo tháng (${currentYear})</h3>
                <div class="chart-container">
                    <canvas id="revenueChart"></canvas>
                </div>
            </div>
            <div class="chart-card">
                <h3>📊 Đơn hàng theo trạng thái</h3>
                <div class="chart-container">
                    <canvas id="statusChart"></canvas>
                </div>
            </div>
            <div class="chart-card">
                <h3>💳 Phương thức thanh toán</h3>
                <div class="chart-container">
                    <canvas id="paymentChart"></canvas>
                </div>
            </div>
        </div>

        <!-- Tables -->
        <div class="tables-grid">
            <!-- Top Selling Books -->
            <div class="table-card">
                <h3>🏆 Top 10 sách bán chạy</h3>
                <c:choose>
                    <c:when test="${empty topBooks}">
                        <div class="empty-state">Chưa có dữ liệu</div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Sách</th>
                                    <th>Đã bán</th>
                                    <th>Doanh thu</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${topBooks}" var="book" varStatus="st">
                                    <tr>
                                        <td>${st.index + 1}</td>
                                        <td>
                                            <div class="book-info">
                                                <c:choose>
                                                    <c:when test="${not empty book[2]}">
                                                        <img src="${book[2]}" alt="${book[1]}" loading="lazy"
                                                             onerror="this.style.display='none'">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="width:40px;height:55px;background:#f0f0f0;border-radius:4px;"></div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="title">${book[1]}</span>
                                            </div>
                                        </td>
                                        <td><span class="count-badge"><fmt:formatNumber value="${book[3]}" pattern="#,###"/></span></td>
                                        <td class="amount"><fmt:formatNumber value="${book[4]}" pattern="#,###"/>₫</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Top Customers -->
            <div class="table-card">
                <h3>⭐ Top 10 khách hàng thân thiết</h3>
                <c:choose>
                    <c:when test="${empty topCustomers}">
                        <div class="empty-state">Chưa có dữ liệu</div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Khách hàng</th>
                                    <th>Số đơn</th>
                                    <th>Tổng chi tiêu</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${topCustomers}" var="cust" varStatus="st">
                                    <tr>
                                        <td>${st.index + 1}</td>
                                        <td>
                                            <div><strong>${cust[1]}</strong></div>
                                            <div style="font-size: 11px; color: #888;">${cust[2]}</div>
                                        </td>
                                        <td><span class="count-badge"><fmt:formatNumber value="${cust[3]}" pattern="#,###"/></span></td>
                                        <td class="amount"><fmt:formatNumber value="${cust[4]}" pattern="#,###"/>₫</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Low Stock Books -->
            <div class="table-card">
                <h3>⚠️ Sách sắp hết hàng (≤10 cuốn)</h3>
                <c:choose>
                    <c:when test="${empty lowStockBooks}">
                        <div class="empty-state">✅ Không có sách nào sắp hết</div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Sách</th>
                                    <th>Còn lại</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${lowStockBooks}" var="book">
                                    <tr>
                                        <td>
                                            <div class="book-info">
                                                <c:choose>
                                                    <c:when test="${not empty book[2]}">
                                                        <img src="${book[2]}" alt="${book[1]}" loading="lazy"
                                                             onerror="this.style.display='none'">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="width:40px;height:55px;background:#f0f0f0;border-radius:4px;"></div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="title">${book[1]}</span>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="stock-warning">${book[3]} cuốn</span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Recent Orders -->
            <div class="table-card">
                <h3>🕐 Đơn hàng gần đây</h3>
                <c:choose>
                    <c:when test="${empty recentOrders}">
                        <div class="empty-state">Chưa có đơn hàng</div>
                    </c:when>
                    <c:otherwise>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Khách hàng</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentOrders}" var="order">
                                    <tr>
                                        <td><strong>#${order.orderId}</strong></td>
                                        <td>${order.customer.fullName}</td>
                                        <td class="amount"><fmt:formatNumber value="${order.totalAmount}" pattern="#,###"/>₫</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.orderStatus == 'PENDING'}">
                                                    <span class="status-badge status-pending">Chờ xử lý</span>
                                                </c:when>
                                                <c:when test="${order.orderStatus == 'PROCESSING'}">
                                                    <span class="status-badge status-processing">Đang xử lý</span>
                                                </c:when>
                                                <c:when test="${order.orderStatus == 'SHIPPED'}">
                                                    <span class="status-badge status-shipped">Đang giao</span>
                                                </c:when>
                                                <c:when test="${order.orderStatus == 'DELIVERED'}">
                                                    <span class="status-badge status-delivered">Đã giao</span>
                                                </c:when>
                                                <c:when test="${order.orderStatus == 'CANCELLED'}">
                                                    <span class="status-badge status-cancelled">Đã hủy</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge">${order.orderStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <jsp:include page="/admin/footer_admin.jsp" />

    <script>
        // ===== Revenue by Month Chart =====
        const revenueCtx = document.getElementById('revenueChart').getContext('2d');
        const monthLabels = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'];
        const revenueData = new Array(12).fill(0);

        <c:forEach items="${revenueByMonth}" var="item">
            revenueData[${item[0]} - 1] = ${item[1]};
        </c:forEach>

        new Chart(revenueCtx, {
            type: 'bar',
            data: {
                labels: monthLabels,
                datasets: [{
                    label: 'Doanh thu (VNĐ)',
                    data: revenueData,
                    backgroundColor: 'rgba(26, 35, 126, 0.8)',
                    borderColor: '#1a237e',
                    borderWidth: 1,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                if (value >= 1000000) return (value / 1000000).toFixed(1) + 'M';
                                if (value >= 1000) return (value / 1000).toFixed(0) + 'K';
                                return value;
                            }
                        }
                    }
                }
            }
        });

        // ===== Order Status Pie Chart =====
        const statusCtx = document.getElementById('statusChart').getContext('2d');
        const statusLabels = [];
        const statusData = [];
        const statusColors = {
            'PENDING': '#fff3cd',
            'PROCESSING': '#cce5ff',
            'SHIPPED': '#d1ecf1',
            'DELIVERED': '#d4edda',
            'CANCELLED': '#f8d7da'
        };
        const statusBorderColors = {
            'PENDING': '#856404',
            'PROCESSING': '#004085',
            'SHIPPED': '#0c5460',
            'DELIVERED': '#155724',
            'CANCELLED': '#721c24'
        };
        const statusNameMap = {
            'PENDING': 'Chờ xử lý',
            'PROCESSING': 'Đang xử lý',
            'SHIPPED': 'Đang giao',
            'DELIVERED': 'Đã giao',
            'CANCELLED': 'Đã hủy'
        };

        const bgColors = [];
        const borderColors = [];

        <c:forEach items="${ordersByStatus}" var="item">
            statusLabels.push(statusNameMap['${item[0]}'] || '${item[0]}');
            statusData.push(${item[1]});
            bgColors.push(statusColors['${item[0]}'] || '#e0e0e0');
            borderColors.push(statusBorderColors['${item[0]}'] || '#999');
        </c:forEach>

        new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: statusLabels,
                datasets: [{
                    data: statusData,
                    backgroundColor: bgColors,
                    borderColor: borderColors,
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { font: { size: 11 } }
                    }
                }
            }
        });

        // ===== Payment Method Pie Chart =====
        const paymentCtx = document.getElementById('paymentChart').getContext('2d');
        const paymentLabels = [];
        const paymentData = [];
        const paymentColors = ['#667eea', '#f5576c', '#4facfe', '#43e97b', '#ff9966'];

        <c:forEach items="${ordersByPayment}" var="item" varStatus="st">
            paymentLabels.push('${item[0]}');
            paymentData.push(${item[1]});
        </c:forEach>

        new Chart(paymentCtx, {
            type: 'doughnut',
            data: {
                labels: paymentLabels,
                datasets: [{
                    data: paymentData,
                    backgroundColor: paymentColors.slice(0, paymentLabels.length),
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { font: { size: 11 } }
                    }
                }
            }
        });
    </script>
</body>
</html>
