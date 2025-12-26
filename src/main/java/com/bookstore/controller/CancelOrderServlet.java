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
 * CancelOrderServlet - Handles order cancellation requests from customers
 * Only allows cancellation for COD orders in PENDING or PROCESSING status
 */
@WebServlet(name = "CancelOrderServlet", urlPatterns = { "/customer/cancel-order" })
public class CancelOrderServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CancelOrderServlet.class.getName());
    private final OrderService orderService = new OrderService();

    /**
     * Handle POST request to cancel an order
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Customer customer = (Customer) (session != null ? session.getAttribute("customer") : null);

        // Check if customer is logged in
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=customer/orders");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        String reason = request.getParameter("reason");

        // Validate orderId
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            redirectWithError(request, response, null, "Mã đơn hàng không hợp lệ");
            return;
        }

        try {
            Integer orderId = Integer.parseInt(orderIdStr);

            // Get order and verify ownership
            Order order = orderService.getOrderByIdWithDetails(orderId);
            if (order == null) {
                redirectWithError(request, response, orderId, "Không tìm thấy đơn hàng");
                return;
            }

            // Verify customer owns this order
            if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
                LOGGER.log(Level.WARNING, "Customer {0} tried to cancel order {1} that belongs to customer {2}",
                        new Object[] { customer.getUserId(), orderId, order.getCustomer().getUserId() });
                redirectWithError(request, response, orderId, "Bạn không có quyền huỷ đơn hàng này");
                return;
            }

            // Check if order can be cancelled
            if (!orderService.canCancelOrder(order)) {
                String message;
                if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
                    message = "Chỉ có thể huỷ đơn hàng thanh toán khi nhận hàng (COD). " +
                            "Vui lòng liên hệ hotline để được hỗ trợ.";
                } else {
                    message = "Không thể huỷ đơn hàng ở trạng thái hiện tại";
                }
                redirectWithError(request, response, orderId, message);
                return;
            }

            // Cancel the order
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Khách hàng yêu cầu huỷ";
            }

            orderService.cancelOrder(orderId, reason);

            LOGGER.log(Level.INFO, "Order {0} cancelled by customer {1}. Reason: {2}",
                    new Object[] { orderId, customer.getUserId(), reason });

            // Redirect with success message
            String successMessage = URLEncoder.encode("Đơn hàng #" + orderId + " đã được huỷ thành công",
                    StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() +
                    "/customer/order-confirmation?orderId=" + orderId + "&cancelled=true&message=" + successMessage);

        } catch (NumberFormatException e) {
            redirectWithError(request, response, null, "Mã đơn hàng không hợp lệ");
        } catch (IllegalStateException e) {
            // Order cannot be cancelled (status or payment method restrictions)
            redirectWithError(request, response, Integer.parseInt(orderIdStr), e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling order", e);
            redirectWithError(request, response,
                    orderIdStr != null ? Integer.parseInt(orderIdStr) : null,
                    "Đã xảy ra lỗi khi huỷ đơn hàng. Vui lòng thử lại sau.");
        }
    }

    /**
     * Redirect with error message
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
