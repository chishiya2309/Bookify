<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Quản lý đơn hàng</title>
    <style>
        .orders-container {
            padding: 20px 30px;
            background-color: #f8f9fa;
            min-height: 100vh;
        }
        
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .page-header h2 {
            color: #1a237e;
            margin: 0;
            font-size: 24px;
        }
        
        .stats-info {
            color: #666;
            font-size: 14px;
        }
        
        .orders-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .orders-table th {
            background: linear-gradient(135deg, #1a237e, #3949ab);
            color: white;
            padding: 14px 12px;
            text-align: left;
            font-weight: 600;
            font-size: 13px;
        }
        
        .orders-table td {
            padding: 12px;
            border-bottom: 1px solid #eee;
            font-size: 13px;
            color: #333;
        }
        
        .orders-table tr:hover {
            background-color: #f5f7ff;
        }
        
        .orders-table tr:last-child td {
            border-bottom: none;
        }
        
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
        
        .payment-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 500;
            background: #e8eaf6;
            color: #3949ab;
        }
        
        .action-btn {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .btn-view {
            background: #e3f2fd;
            color: #1565c0;
        }
        
        .btn-view:hover {
            background: #1565c0;
            color: white;
        }
        
        .btn-delete {
            background: #ffebee;
            color: #c62828;
        }
        
        .btn-delete:hover {
            background: #c62828;
            color: white;
        }
        
        .amount {
            font-weight: 600;
            color: #2e7d32;
        }
        
        /* Pagination */
        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            padding: 15px 0;
        }
        
        .pagination {
            display: flex;
            gap: 5px;
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .pagination a, .pagination span {
            display: inline-block;
            padding: 8px 14px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 13px;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .pagination a {
            background: white;
            color: #1a237e;
            border: 1px solid #ddd;
        }
        
        .pagination a:hover {
            background: #1a237e;
            color: white;
            border-color: #1a237e;
        }
        
        .pagination .active {
            background: #1a237e;
            color: white;
            border: 1px solid #1a237e;
        }
        
        .pagination .disabled {
            background: #f5f5f5;
            color: #999;
            border: 1px solid #ddd;
            cursor: not-allowed;
        }
        
        .page-size-selector {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .page-size-selector label {
            font-size: 13px;
            color: #666;
        }
        
        .page-size-selector select {
            padding: 6px 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 13px;
        }
        
        .empty-state {
            text-align: center;
            padding: 50px;
            color: #666;
        }
        
        .empty-state i {
            font-size: 48px;
            color: #ccc;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <jsp:include page="/admin/header_admin.jsp" />

    <div class="orders-container">
        <div class="page-header">
            <h2>Quản lý đơn hàng</h2>
            <span class="stats-info">Tổng: ${totalOrders} đơn hàng</span>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="empty-state">
                    <p>Chưa có đơn hàng nào.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table class="orders-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Mã đơn</th>
                            <th>Khách hàng</th>
                            <th>Số lượng</th>
                            <th>Tổng tiền</th>
                            <th>Thanh toán</th>
                            <th>Trạng thái</th>
                            <th>Ngày đặt</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orders}" var="o" varStatus="st">
                            <tr>
                                <td>${currentPage * pageSize + st.index + 1}</td>
                                <td><strong>#${o.orderId}</strong></td>
                                <td>${o.customer.fullName}</td>
                                <td>${o.totalQuantity}</td>
                                <td class="amount">
                                    <fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                                <td>
                                    <span class="payment-badge">${o.paymentMethod}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${o.orderStatus == 'PENDING'}">
                                            <span class="status-badge status-pending">Chờ xử lý</span>
                                        </c:when>
                                        <c:when test="${o.orderStatus == 'PROCESSING'}">
                                            <span class="status-badge status-processing">Đang xử lý</span>
                                        </c:when>
                                        <c:when test="${o.orderStatus == 'SHIPPED'}">
                                            <span class="status-badge status-shipped">Đang giao</span>
                                        </c:when>
                                        <c:when test="${o.orderStatus == 'DELIVERED'}">
                                            <span class="status-badge status-delivered">Đã giao</span>
                                        </c:when>
                                        <c:when test="${o.orderStatus == 'CANCELLED'}">
                                            <span class="status-badge status-cancelled">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge">${o.orderStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:parseDate value="${o.orderDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both"/>
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders?action=edit&id=${o.orderId}" 
                                       class="action-btn btn-view">Xem / Sửa</a>
                                    <a href="${pageContext.request.contextPath}/admin/orders?action=delete&id=${o.orderId}"
                                       class="action-btn btn-delete"
                                       onclick="return confirm('Bạn có chắc muốn xóa đơn hàng #${o.orderId}?')">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Pagination -->
                <div class="pagination-container">
                    <div class="page-size-selector">
                        <label>Hiển thị:</label>
                        <select onchange="changePageSize(this.value)">
                            <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                            <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                            <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                        </select>
                        <span>đơn hàng / trang</span>
                    </div>

                    <div class="pagination">
                        <c:if test="${currentPage > 0}">
                            <a href="${pageContext.request.contextPath}/admin/orders?page=0&size=${pageSize}">« Đầu</a>
                            <a href="${pageContext.request.contextPath}/admin/orders?page=${currentPage - 1}&size=${pageSize}">‹ Trước</a>
                        </c:if>
                        <c:if test="${currentPage == 0}">
                            <span class="disabled">« Đầu</span>
                            <span class="disabled">‹ Trước</span>
                        </c:if>

                        <c:forEach begin="${Math.max(0, currentPage - 2)}" end="${Math.min(totalPages - 1, currentPage + 2)}" var="i">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="active">${i + 1}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/admin/orders?page=${i}&size=${pageSize}">${i + 1}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages - 1}">
                            <a href="${pageContext.request.contextPath}/admin/orders?page=${currentPage + 1}&size=${pageSize}">Sau ›</a>
                            <a href="${pageContext.request.contextPath}/admin/orders?page=${totalPages - 1}&size=${pageSize}">Cuối »</a>
                        </c:if>
                        <c:if test="${currentPage >= totalPages - 1}">
                            <span class="disabled">Sau ›</span>
                            <span class="disabled">Cuối »</span>
                        </c:if>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="/admin/footer_admin.jsp" />

    <script>
        function changePageSize(size) {
            window.location.href = '${pageContext.request.contextPath}/admin/orders?page=0&size=' + size;
        }
    </script>
</body>
</html>
