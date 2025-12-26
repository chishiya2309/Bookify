package com.bookstore.controller;

import com.bookstore.config.SepayConfig;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.Payment;
import com.bookstore.model.ShoppingCart;
import com.bookstore.service.EmailService;
import com.bookstore.service.OrderService;
import com.bookstore.service.PaymentService;
import com.bookstore.service.ShoppingCartServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SepayWebhookServlet - Dedicated webhook endpoint for Sepay payment
 * notifications
 * 
 * This is a PURE API endpoint (no JSP, no redirect)
 * Follows payment gateway best practices
 * 
 * URL: /api/sepay/webhook
 */
@WebServlet(name = "SepayWebhookServlet", urlPatterns = { "/api/sepay/webhook" })
public class SepayWebhookServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SepayWebhookServlet.class.getName());
    private final Gson gson = new Gson();
    private final PaymentService paymentService = new PaymentService();
    private final OrderService orderService = new OrderService();
    private final EmailService emailService = new EmailService();
    private final ShoppingCartServices cartService = new ShoppingCartServices();

    /**
     * Handle POST webhook from Sepay
     * This is called when customer transfers money
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read webhook payload
            String payload = readRequestBody(request);
            LOGGER.log(Level.INFO, "Sepay webhook received: {0}", payload);

            // Parse JSON
            JsonObject webhookData = gson.fromJson(payload, JsonObject.class);

            // Extract data from Sepay webhook (using actual Sepay field names!)
            // Sepay sends: accountNumber, content, transferAmount, referenceCode, etc.
            String transactionContent = getStringValue(webhookData, "content");
            long amountIn = getLongValue(webhookData, "transferAmount");
            String transactionDate = getStringValue(webhookData, "transactionDate");
            String referenceNumber = getStringValue(webhookData, "referenceCode");
            String accountNumber = getStringValue(webhookData, "accountNumber");
            String transferType = getStringValue(webhookData, "transferType");

            LOGGER.log(Level.INFO, "Transaction content: {0}, Amount: {1}, Type: {2}",
                    new Object[] { transactionContent, amountIn, transferType });

            // Only process incoming transfers
            if (!"in".equals(transferType)) {
                LOGGER.log(Level.INFO, "Ignoring outgoing transfer");
                sendSuccessResponse(response, "Ignored outgoing transfer");
                return;
            }

            // Verify account number matches our config
            if (!accountNumber.equals(SepayConfig.getAccountNumber())) {
                LOGGER.log(Level.WARNING, "Account number mismatch: {0} vs {1}",
                        new Object[] { accountNumber, SepayConfig.getAccountNumber() });
                sendErrorResponse(response, "Invalid account number");
                return;
            }

            // Extract order ID from transaction content
            // Format: "BOOKIFY 123" or "bookify 123" (can be embedded in longer content)
            Integer orderId = extractOrderIdFromContent(transactionContent);

            if (orderId == null) {
                LOGGER.log(Level.WARNING, "Cannot extract order ID from: {0}", transactionContent);
                sendErrorResponse(response, "Invalid transaction content format");
                return;
            }

            // Get order from database
            Order order = orderService.getOrderByIdWithDetails(orderId);

            if (order == null) {
                LOGGER.log(Level.WARNING, "Order not found: {0}", orderId);
                sendErrorResponse(response, "Order not found");
                return;
            }

            // Verify amount matches (allow some tolerance for transaction fees)
            long expectedAmount = order.getTotalAmount().longValue();
            if (amountIn < expectedAmount) {
                LOGGER.log(Level.WARNING, "Amount mismatch - Expected: {0}, Received: {1}",
                        new Object[] { expectedAmount, amountIn });
                sendErrorResponse(response, "Amount mismatch");
                return;
            }

            // Check if already paid
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                LOGGER.log(Level.INFO, "Order {0} already paid, skipping", orderId);
                sendSuccessResponse(response, "Already processed");
                return;
            }

            // Update order payment status
            orderService.updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
            orderService.updateOrderStatus(orderId, Order.OrderStatus.PROCESSING);

            LOGGER.log(Level.INFO, "Order {0} payment confirmed via Sepay", orderId);

            // Clear customer cart after successful payment
            try {
                Customer customer = order.getCustomer();
                if (customer != null) {
                    ShoppingCart cart = cartService.getCartByCustomer(customer);
                    if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
                        cartService.clearCart(cart);
                        LOGGER.log(Level.INFO, "Cart cleared for customer {0} after payment confirmed",
                                customer.getUserId());
                    }
                }
            } catch (Exception cartEx) {
                LOGGER.log(Level.WARNING, "Failed to clear cart after payment", cartEx);
                // Don't fail webhook if cart clear fails
            }

            // Send payment confirmation email
            try {
                // Create payment record for email
                Payment payment = new Payment();
                payment.setOrder(order);
                payment.setAmount(order.getTotalAmount());
                payment.setMethod(Payment.PaymentMethod.BANK_TRANSFER);
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setTransactionId(referenceNumber);
                payment.setPaymentDate(java.time.LocalDateTime.now());

                emailService.sendPaymentConfirmation(order, payment);
                LOGGER.log(Level.INFO, "Payment confirmation email sent for order: {0}", orderId);
            } catch (Exception emailEx) {
                LOGGER.log(Level.WARNING, "Failed to send payment confirmation email", emailEx);
                // Don't fail the webhook if email fails
            }

            // Return success response to Sepay
            sendSuccessResponse(response, "Payment processed successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing Sepay webhook", e);
            sendErrorResponse(response, "Internal server error");
        }
    }

    /**
     * Handle GET requests - for testing/verification
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject result = new JsonObject();
        result.addProperty("status", "ok");
        result.addProperty("message", "Sepay webhook endpoint is active");
        result.addProperty("endpoint", "/api/sepay/webhook");
        result.addProperty("method", "POST");

        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Extract order ID from transaction content
     * Format: "BOOKIFY 123" or "bookify 123"
     */
    private Integer extractOrderIdFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        // Pattern: BOOKIFY [ORDER_ID] (case-insensitive)
        Pattern pattern = Pattern.compile("BOOKIFY\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content.trim());

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Read request body as string
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * Get string value from JSON object
     */
    private String getStringValue(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return "";
    }

    /**
     * Get long value from JSON object
     */
    private long getLongValue(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsLong();
        }
        return 0L;
    }

    /**
     * Send success response to Sepay
     */
    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);

        JsonObject result = new JsonObject();
        result.addProperty("status", "success");
        result.addProperty("message", message);

        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Send error response to Sepay
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        JsonObject result = new JsonObject();
        result.addProperty("status", "error");
        result.addProperty("message", message);

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    public String getServletInfo() {
        return "Sepay Webhook API Endpoint";
    }
}
