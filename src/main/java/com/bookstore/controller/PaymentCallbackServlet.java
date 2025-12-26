package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.Payment;
import com.bookstore.service.EmailService;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PaymentCallbackServlet - Handle payment gateway callbacks
 * Processes return and notification callbacks from Sepay and other payment
 * gateways
 */
@WebServlet(name = "PaymentCallbackServlet", urlPatterns = {
        "/payment/return",
        "/payment/notify"
})
public class PaymentCallbackServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PaymentCallbackServlet.class.getName());
    private final PaymentService paymentService = new PaymentService();
    private final OrderService orderService = new OrderService();
    private final EmailService emailService = new EmailService();

    /**
     * Handle GET requests - User return from payment gateway
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String transactionId = request.getParameter("transaction_id");
        String status = request.getParameter("status");

        LOGGER.log(Level.INFO, "Payment return callback - Transaction: {0}, Status: {1}",
                new Object[] { transactionId, status });

        if (transactionId == null || transactionId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/checkout?error=invalid_transaction");
            return;
        }

        try {
            // Verify payment with gateway
            boolean verified = paymentService.verifyPayment(transactionId);

            if (verified && "success".equalsIgnoreCase(status)) {
                // Payment successful
                Payment payment = paymentService.getPaymentByTransactionId(transactionId);
                if (payment != null) {
                    paymentService.handlePaymentSuccess(payment, transactionId);

                    // Send payment confirmation email
                    try {
                        Order order = payment.getOrder();
                        if (order != null) {
                            emailService.sendPaymentConfirmation(order, payment);
                            LOGGER.log(Level.INFO, "Payment confirmation email sent for order: {0}",
                                    order.getOrderId());
                        }
                    } catch (Exception emailEx) {
                        LOGGER.log(Level.WARNING, "Failed to send payment confirmation email", emailEx);
                    }
                }

                // Redirect to success page - use orderId (OrderConfirmationServlet expects
                // orderId)
                Order order = payment.getOrder();
                Integer orderId = (order != null) ? order.getOrderId() : null;
                if (orderId != null) {
                    response.sendRedirect(request.getContextPath() +
                            "/customer/order-confirmation?orderId=" + orderId);
                } else {
                    // Fallback with transaction_id if order not available
                    response.sendRedirect(request.getContextPath() +
                            "/customer/order-confirmation?transaction_id=" + transactionId);
                }
            } else {
                // Payment failed or cancelled
                response.sendRedirect(request.getContextPath() +
                        "/customer/checkout?error=payment_failed");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment return", e);
            response.sendRedirect(request.getContextPath() +
                    "/customer/checkout?error=processing_error");
        }
    }

    /**
     * Handle POST requests - Server-to-server notification from payment gateway
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String transactionId = request.getParameter("transaction_id");
        String status = request.getParameter("status");
        String amount = request.getParameter("amount");
        String signature = request.getParameter("signature");

        LOGGER.log(Level.INFO, "Payment notification - Transaction: {0}, Status: {1}, Amount: {2}",
                new Object[] { transactionId, status, amount });

        try {
            // Verify signature to ensure request is from Sepay
            // In production, implement proper signature verification

            if (transactionId != null && !transactionId.isEmpty()) {
                Payment payment = paymentService.getPaymentByTransactionId(transactionId);

                if (payment != null) {
                    if ("success".equalsIgnoreCase(status)) {
                        paymentService.handlePaymentSuccess(payment, transactionId);

                        // Send payment confirmation email
                        try {
                            Order order = payment.getOrder();
                            if (order != null) {
                                emailService.sendPaymentConfirmation(order, payment);
                                LOGGER.log(Level.INFO, "Payment confirmation email sent for order: {0}",
                                        order.getOrderId());
                            }
                        } catch (Exception emailEx) {
                            LOGGER.log(Level.WARNING, "Failed to send payment confirmation email", emailEx);
                        }

                        // Return success response to gateway
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\":\"success\"}");
                    } else {
                        // Payment failed
                        paymentService.updatePaymentStatus(payment.getPaymentId(),
                                Payment.PaymentStatus.FAILED);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\":\"received\"}");
                    }
                } else {
                    LOGGER.log(Level.WARNING, "Payment not found for transaction: {0}", transactionId);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"status\":\"not_found\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"status\":\"invalid_request\"}");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing payment notification", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Payment Gateway Callback Servlet";
    }
}
