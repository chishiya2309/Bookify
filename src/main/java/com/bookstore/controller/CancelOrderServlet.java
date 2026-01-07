package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CancelOrderServlet - Xử lý yêu cầu hủy đơn hàng từ khách hàng
 * Chỉ cho phép hủy đơn hàng cho đơn hàng COD hoặc Bank Transfer ở trạng thái
 * PENDING
 */
@WebServlet(name = "CancelOrderServlet", urlPatterns = { "/customer/cancel-order" })
public class CancelOrderServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CancelOrderServlet.class.getName());
    private final OrderService orderService = new OrderService();

    /**
     * Xử lý yêu cầu hủy đơn hàng từ link click
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy orderId từ tham số 'id' (từ link) hoặc 'orderId'
        String orderIdStr = request.getParameter("id");
        if (orderIdStr == null) {
            orderIdStr = request.getParameter("orderId");
        }

        if (orderIdStr != null) {
            request.setAttribute("orderIdFromGet", orderIdStr);
        }
        processCancel(request, response);
    }

    /**
     * Xử lý yêu cầu hủy đơn hàng từ form submit
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processCancel(request, response);
    }

    /**
     * Xử lý logic hủy đơn hàng cho cả GET và POST
     */
    private void processCancel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Customer customer = (Customer) (session != null ? session.getAttribute("customer") : null);

        // Kiểm tra nếu khách hàng đã đăng nhập
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=customer/orders");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        // Kiểm tra tham số 'id' cho GET requests
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            orderIdStr = request.getParameter("id");
        }
        // Kiểm tra tham số 'orderIdFromGet' từ doGet
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            orderIdStr = (String) request.getAttribute("orderIdFromGet");
        }
        String reason = request.getParameter("reason");

        // Kiểm tra orderId
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            redirectWithError(request, response, null, "Mã đơn hàng không hợp lệ");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);

            // Lấy đơn hàng và xác nhận quyền sở hữu
            Order order = orderService.getOrderByIdWithDetails(orderId);
            if (order == null) {
                redirectWithError(request, response, orderId, "Không tìm thấy đơn hàng");
                return;
            }

            // Kiểm tra khách hàng có quyền sở hữu đơn hàng không
            if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
                LOGGER.log(Level.WARNING, "Customer {0} tried to cancel order {1} that belongs to customer {2}",
                        new Object[] { customer.getUserId(), orderId, order.getCustomer().getUserId() });
                redirectWithError(request, response, orderId, "Bạn không có quyền huỷ đơn hàng này");
                return;
            }

            // Kiểm tra đơn hàng có thể hủy không
            if (!orderService.canCancelOrder(order)) {
                String message;
                if ("BANK_TRANSFER".equalsIgnoreCase(order.getPaymentMethod())
                        && order.getPaymentStatus() == com.bookstore.model.Order.PaymentStatus.PAID) {
                    message = "Không thể huỷ đơn hàng đã thanh toán. Vui lòng liên hệ hotline để được hỗ trợ.";
                } else if (order.getOrderStatus() != null && !"PENDING".equals(order.getOrderStatus().name())) {
                    message = "Không thể huỷ đơn hàng ở trạng thái hiện tại. Vui lòng liên hệ hotline để được hỗ trợ.";
                } else {
                    message = "Không thể huỷ đơn hàng này. Vui lòng liên hệ hotline để được hỗ trợ.";
                }
                redirectWithError(request, response, orderId, message);
                return;
            }

            // Hủy đơn hàng
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Khách hàng yêu cầu huỷ";
            }

            orderService.cancelOrder(orderId, reason);

            LOGGER.log(Level.INFO, "Order {0} cancelled by customer {1}. Reason: {2}",
                    new Object[] { orderId, customer.getUserId(), reason });

            // Redirect với thông báo thành công
            String successMessage = URLEncoder.encode("Đơn hàng #" + orderId + " đã được huỷ thành công",
                    StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() +
                    "/customer/order-confirmation?orderId=" + orderId + "&cancelled=true&message=" + successMessage);

        } catch (NumberFormatException e) {
            redirectWithError(request, response, null, "Mã đơn hàng không hợp lệ");
        } catch (IllegalStateException e) {
            redirectWithError(request, response, safelyParseOrderId(orderIdStr), e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling order", e);
            redirectWithError(request, response,
                    safelyParseOrderId(orderIdStr),
                    "Đã xảy ra lỗi khi huỷ đơn hàng. Vui lòng thử lại sau.");
        }
    }

    /**
     * An toàn khi phân tích chuỗi orderId thành số nguyên không ném
     * NumberFormatException.
     * Trả về null nếu đầu vào là null hoặc không hợp lệ.
     */
    private Integer safelyParseOrderId(String orderIdStr) {
        if (orderIdStr == null) {
            return null;
        }
        try {
            return Integer.valueOf(orderIdStr.trim());
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Invalid orderId format: {0}", orderIdStr);
            return null;
        }
    }

    /**
     * Redirect với thông báo lỗi
     */
    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
            Integer orderId, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String redirectUrl;

        if (orderId != null) {
            redirectUrl = request.getContextPath() +
                    "/customer/order-confirmation?orderId=" + orderId + "&error=" + encodedMessage;
        } else {
            redirectUrl = request.getContextPath() + "/customer/orders?error=" + encodedMessage;
        }

        response.sendRedirect(redirectUrl);
    }
}
