package com.bookstore.controller;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OrderConfirmationServlet - Display order confirmation page
 * Shows order details after successful checkout
 */
@WebServlet(name = "OrderConfirmationServlet", urlPatterns = { "/customer/order-confirmation" })
public class OrderConfirmationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OrderConfirmationServlet.class.getName());
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }

    /**
     * Handles GET request - Display order confirmation
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Restore customer from JWT if not in session
        if (customer == null) {
            customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
        }

        // Redirect to login if not authenticated
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/login?redirect=order-confirmation");
            return;
        }

        try {
            // Get order ID from parameter
            String orderIdStr = request.getParameter("orderId");

            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Không tìm thấy mã đơn hàng");
                request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Load order with details
            Order order = orderService.getOrderByIdWithDetails(orderId);

            if (order == null) {
                request.setAttribute("error", "Không tìm thấy đơn hàng với mã: " + orderId);
                request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);
                return;
            }

            // Verify order belongs to customer (security check)
            if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
                LOGGER.log(Level.WARNING, "Customer {0} attempted to access order {1} belonging to customer {2}",
                        new Object[] { customer.getUserId(), orderId, order.getCustomer().getUserId() });
                request.setAttribute("error", "Bạn không có quyền xem đơn hàng này");
                request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);
                return;
            }

            // Set attributes for JSP
            request.setAttribute("order", order);
            request.setAttribute("user", customer);
            request.setAttribute("isGuest", false); // Customer is required for this page

            // Load categories for header
            com.bookstore.service.CustomerServices customerServices = new com.bookstore.service.CustomerServices();
            request.setAttribute("listCategories", customerServices.listAllCategories());

            // If payment method is BANK_TRANSFER and not yet paid, add VietQR info
            if ("BANK_TRANSFER".equals(order.getPaymentMethod())
                    && order.getPaymentStatus() != Order.PaymentStatus.PAID) {
                // Generate VietQR URL
                String vietQRUrl = com.bookstore.config.SepayConfig.generateVietQRUrl(
                        order.getOrderId(),
                        order.getTotalAmount().longValue());
                request.setAttribute("vietQRUrl", vietQRUrl);
                request.setAttribute("bankName", com.bookstore.config.SepayConfig.getFullBankName());
                request.setAttribute("accountNumber", com.bookstore.config.SepayConfig.getAccountNumber());
                request.setAttribute("transferContent",
                        com.bookstore.config.SepayConfig.generateTransferContent(order.getOrderId()));
                request.setAttribute("showPaymentQR", true);
            }

            // Check for success message from session
            String confirmationMessage = (String) session.getAttribute("orderConfirmation");
            if (confirmationMessage != null) {
                request.setAttribute("successMessage", confirmationMessage);
                session.removeAttribute("orderConfirmation");
            }

            // Forward to JSP
            request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid order ID format: " + request.getParameter("orderId"), e);
            request.setAttribute("error", "Mã đơn hàng không hợp lệ");
            request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading order confirmation", e);
            request.setAttribute("error", "Đã xảy ra lỗi khi tải thông tin đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/customer/order-confirmation.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Order Confirmation Servlet - Display order details after checkout";
    }
}
