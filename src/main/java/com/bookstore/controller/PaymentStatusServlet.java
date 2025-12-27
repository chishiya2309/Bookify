package com.bookstore.controller;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.OrderService;
import com.google.gson.JsonObject;
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
 * PaymentStatusServlet - API endpoint to check payment status
 * Used by bank-transfer-payment.jsp to poll for payment confirmation
 * 
 * SECURITY: Only the order owner can view payment status
 */
@WebServlet(name = "PaymentStatusServlet", urlPatterns = { "/api/payment/status" })
public class PaymentStatusServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PaymentStatusServlet.class.getName());
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject result = new JsonObject();

        try {
            // ========== AUTHENTICATION CHECK ==========
            HttpSession session = request.getSession(false);
            Customer customer = null;

            if (session != null) {
                customer = (Customer) session.getAttribute("customer");
            }

            // Try to restore from JWT if not in session
            if (customer == null) {
                customer = JwtAuthHelper.restoreCustomerFromJwt(request, session, DBUtil.getEmFactory());
            }

            if (customer == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.addProperty("error", "Unauthorized - Please login");
                result.addProperty("paid", false);
                response.getWriter().write(result.toString());
                return;
            }
            // ========== END AUTHENTICATION ==========

            String orderIdStr = request.getParameter("orderId");

            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                result.addProperty("error", "Missing orderId");
                result.addProperty("paid", false);
                response.getWriter().write(result.toString());
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Get order
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                result.addProperty("error", "Order not found");
                result.addProperty("paid", false);
                response.getWriter().write(result.toString());
                return;
            }

            // ========== AUTHORIZATION CHECK ==========
            // Verify the order belongs to the logged-in customer
            if (!order.getCustomer().getUserId().equals(customer.getUserId())) {
                LOGGER.log(Level.WARNING, "Customer {0} attempted to access order {1} belonging to customer {2}",
                        new Object[] { customer.getUserId(), orderId, order.getCustomer().getUserId() });
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                result.addProperty("error", "Forbidden - You do not own this order");
                result.addProperty("paid", false);
                response.getWriter().write(result.toString());
                return;
            }
            // ========== END AUTHORIZATION ==========

            // Check payment status
            boolean isPaid = order.getPaymentStatus() == Order.PaymentStatus.PAID;

            result.addProperty("orderId", orderId);
            result.addProperty("paid", isPaid);
            result.addProperty("paymentStatus", order.getPaymentStatus().toString());
            result.addProperty("orderStatus", order.getOrderStatus().toString());

            response.getWriter().write(result.toString());

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid order ID format", e);
            result.addProperty("error", "Invalid orderId");
            result.addProperty("paid", false);
            response.getWriter().write(result.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking payment status", e);
            result.addProperty("error", "Internal server error");
            result.addProperty("paid", false);
            response.getWriter().write(result.toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "Payment Status API - Check if order is paid (authenticated)";
    }
}
