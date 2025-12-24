package com.bookstore.service;

import com.bookstore.dao.PaymentDAO;
import com.bookstore.config.SepayConfig;
import com.bookstore.model.Order;
import com.bookstore.model.Payment;
import com.bookstore.model.Payment.PaymentMethod;
import com.bookstore.model.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PaymentService - Service layer for payment processing
 * Handles payment creation, processing, validation, and gateway integration
 */
public class PaymentService {

    private static final Logger LOGGER = Logger.getLogger(PaymentService.class.getName());
    private final PaymentDAO paymentDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }

    // ==================== PAYMENT CREATION & PROCESSING ====================

    /**
     * Create a new payment for an order
     * 
     * @param order   Order to create payment for
     * @param method  Payment method
     * @param gateway Payment gateway (optional for COD)
     * @return Created payment
     */
    public Payment createPayment(Order order, PaymentMethod method, String gateway) {
        validatePaymentAmount(order.getTotalAmount());
        validatePaymentMethod(method);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentGateway(gateway);
        payment.setPaymentDate(LocalDateTime.now());

        // Generate transaction ID
        String transactionId = generateTransactionId(order.getOrderId());
        payment.setTransactionId(transactionId);

        try {
            paymentDAO.save(payment);
            LOGGER.log(Level.INFO, "Payment created: {0}", payment.getTransactionId());
            return payment;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create payment", e);
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    /**
     * Process payment based on payment method
     * 
     * @param payment        Payment to process
     * @param paymentDetails Additional payment details (card info, bank code, etc.)
     * @return Processing result with status and redirect URL if needed
     */
    public PaymentResult processPayment(Payment payment, Map<String, Object> paymentDetails) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        try {
            switch (payment.getMethod()) {
                case COD:
                    return processCODPayment(payment);
                case SEPAY:
                    return processSepayPayment(payment, paymentDetails);
                case CREDIT_CARD:
                    return processCreditCardPayment(payment, paymentDetails);
                case BANK_TRANSFER:
                    return processBankTransferPayment(payment, paymentDetails);
                case VNPAY:
                case MOMO:
                    return processMockOnlinePayment(payment); // Mock for now
                default:
                    throw new IllegalArgumentException("Unsupported payment method: " + payment.getMethod());
            }
        } catch (Exception e) {
            handlePaymentFailure(payment, e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    /**
     * Verify payment from gateway callback
     * 
     * @param transactionId Transaction ID from gateway
     * @return true if payment is verified and completed
     */
    public boolean verifyPayment(String transactionId) {
        validateTransactionId(transactionId);

        Payment payment = paymentDAO.findByTransactionId(transactionId);
        if (payment == null) {
            LOGGER.log(Level.WARNING, "Payment not found for transaction: {0}", transactionId);
            return false;
        }

        // For Sepay, verify with gateway API
        if (payment.getMethod() == PaymentMethod.SEPAY) {
            return verifySepayTransaction(transactionId);
        }

        return payment.getStatus() == PaymentStatus.COMPLETED;
    }

    /**
     * Update payment status
     * 
     * @param paymentId Payment ID
     * @param status    New status
     */
    public void updatePaymentStatus(Integer paymentId, PaymentStatus status) {
        Payment payment = paymentDAO.findById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        payment.setStatus(status);
        paymentDAO.update(payment);

        LOGGER.log(Level.INFO, "Payment {0} status updated to {1}",
                new Object[] { paymentId, status });
    }

    // ==================== PAYMENT GATEWAY INTEGRATION ====================

    /**
     * Process COD payment - automatically mark as pending until delivery
     * 
     * @param payment Payment to process
     * @return Payment result
     */
    private PaymentResult processCODPayment(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        paymentDAO.update(payment);

        LOGGER.log(Level.INFO, "COD payment processed: {0}", payment.getTransactionId());

        return new PaymentResult(true, "COD payment created successfully",
                PaymentStatus.PENDING, null);
    }

    /**
     * Process Sepay payment - integrate with Sepay API
     * 
     * @param payment        Payment to process
     * @param paymentDetails Payment details
     * @return Payment result with redirect URL
     */
    private PaymentResult processSepayPayment(Payment payment, Map<String, Object> paymentDetails) {
        try {
            // Build Sepay payment request
            Map<String, Object> sepayRequest = buildSepayPaymentRequest(payment);

            // In production, call Sepay API here
            // String redirectUrl = callSepayAPI(sepayRequest);

            // For now, generate a mock redirect URL
            String redirectUrl = generateSepayRedirectUrl(payment);

            payment.setStatus(PaymentStatus.PENDING);
            paymentDAO.update(payment);

            LOGGER.log(Level.INFO, "Sepay payment initiated: {0}", payment.getTransactionId());

            return new PaymentResult(true, "Redirect to Sepay gateway",
                    PaymentStatus.PENDING, redirectUrl);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Sepay payment failed", e);
            return new PaymentResult(false, "Sepay payment failed: " + e.getMessage(),
                    PaymentStatus.FAILED, null);
        }
    }

    /**
     * Process credit card payment (mock implementation)
     * 
     * @param payment        Payment to process
     * @param paymentDetails Card details
     * @return Payment result
     */
    private PaymentResult processCreditCardPayment(Payment payment, Map<String, Object> paymentDetails) {
        // Mock implementation - in production, integrate with payment gateway
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentDAO.update(payment);

        return new PaymentResult(true, "Credit card payment successful",
                PaymentStatus.COMPLETED, null);
    }

    /**
     * Process bank transfer payment
     * 
     * @param payment        Payment to process
     * @param paymentDetails Bank details
     * @return Payment result
     */
    private PaymentResult processBankTransferPayment(Payment payment, Map<String, Object> paymentDetails) {
        String bankCode = (String) paymentDetails.get("bankCode");

        payment.setStatus(PaymentStatus.PENDING);
        paymentDAO.update(payment);

        return new PaymentResult(true, "Bank transfer initiated. Please complete the transfer.",
                PaymentStatus.PENDING, null);
    }

    /**
     * Process mock online payment (for VNPay, MoMo)
     * 
     * @param payment Payment to process
     * @return Payment result
     */
    private PaymentResult processMockOnlinePayment(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        paymentDAO.update(payment);

        return new PaymentResult(true, "Online payment initiated (mock)",
                PaymentStatus.PENDING, null);
    }

    // ==================== SEPAY INTEGRATION HELPERS ====================

    /**
     * Build Sepay payment request
     * 
     * @param payment Payment object
     * @return Request map
     */
    private Map<String, Object> buildSepayPaymentRequest(Payment payment) {
        Map<String, Object> request = new HashMap<>();
        request.put("merchant_id", SepayConfig.getMerchantId());
        request.put("transaction_id", payment.getTransactionId());
        request.put("amount", payment.getAmount().longValue());
        request.put("order_id", payment.getOrder().getOrderId());
        request.put("return_url", SepayConfig.getReturnUrl());
        request.put("notify_url", SepayConfig.getNotifyUrl());

        // Add signature for security
        String signature = generateSepaySignature(request);
        request.put("signature", signature);

        return request;
    }

    /**
     * Generate Sepay redirect URL
     * 
     * @param payment Payment object
     * @return Redirect URL
     */
    private String generateSepayRedirectUrl(Payment payment) {
        // In production, this would be the actual Sepay payment URL
        return String.format("%s/payment?transaction_id=%s&amount=%s",
                SepayConfig.SEPAY_API_BASE_URL,
                payment.getTransactionId(),
                payment.getAmount());
    }

    /**
     * Verify Sepay transaction with API
     * 
     * @param transactionId Transaction ID
     * @return true if verified
     */
    private boolean verifySepayTransaction(String transactionId) {
        // In production, call Sepay API to verify transaction
        // For now, return true for testing
        LOGGER.log(Level.INFO, "Verifying Sepay transaction: {0}", transactionId);
        return true;
    }

    /**
     * Generate Sepay signature for security
     * 
     * @param request Request data
     * @return Signature string
     */
    private String generateSepaySignature(Map<String, Object> request) {
        // In production, implement proper HMAC-SHA256 signature using
        // SepayConfig.getSecretKey()
        // String secretKey = SepayConfig.getSecretKey();
        // Implement HMAC-SHA256 with secretKey

        // For now, return a mock signature
        return "mock_signature_" + UUID.randomUUID().toString();
    }

    // ==================== VALIDATION ====================

    /**
     * Validate payment amount
     * 
     * @param amount Amount to validate
     */
    public void validatePaymentAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        // Optional: Add maximum amount validation
        BigDecimal maxAmount = new BigDecimal("100000000"); // 100 million
        if (amount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds maximum limit");
        }
    }

    /**
     * Validate payment method
     * 
     * @param method Payment method to validate
     */
    public void validatePaymentMethod(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
    }

    /**
     * Validate transaction ID
     * 
     * @param transactionId Transaction ID to validate
     */
    public void validateTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
    }

    /**
     * Check if payment is completed
     * 
     * @param paymentId Payment ID
     * @return true if completed
     */
    public boolean isPaymentCompleted(Integer paymentId) {
        Payment payment = paymentDAO.findById(paymentId);
        return payment != null && payment.getStatus() == PaymentStatus.COMPLETED;
    }

    // ==================== QUERY METHODS ====================

    /**
     * Get payment by order ID
     * 
     * @param orderId Order ID
     * @return Payment or null
     */
    public Payment getPaymentByOrderId(Integer orderId) {
        return paymentDAO.findByOrderId(orderId);
    }

    /**
     * Get payment by transaction ID
     * 
     * @param transactionId Transaction ID
     * @return Payment or null
     */
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentDAO.findByTransactionId(transactionId);
    }

    /**
     * Get payment by ID
     * 
     * @param paymentId Payment ID
     * @return Payment or null
     */
    public Payment getPaymentById(Integer paymentId) {
        return paymentDAO.findById(paymentId);
    }

    // ==================== TRANSACTION MANAGEMENT ====================

    /**
     * Generate unique transaction ID
     * Format: PAY-{timestamp}-{orderId}-{random}
     * 
     * @param orderId Order ID
     * @return Transaction ID
     */
    public String generateTransactionId(Integer orderId) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("PAY-%s-%d-%s", timestamp, orderId, random);
    }

    /**
     * Handle payment failure
     * 
     * @param payment Payment that failed
     * @param reason  Failure reason
     */
    private void handlePaymentFailure(Payment payment, String reason) {
        payment.setStatus(PaymentStatus.FAILED);
        paymentDAO.update(payment);

        LOGGER.log(Level.WARNING, "Payment failed: {0}, Reason: {1}",
                new Object[] { payment.getTransactionId(), reason });
    }

    /**
     * Handle payment success
     * 
     * @param payment       Payment that succeeded
     * @param transactionId Transaction ID from gateway
     */
    public void handlePaymentSuccess(Payment payment, String transactionId) {
        payment.setStatus(PaymentStatus.COMPLETED);
        if (transactionId != null && !transactionId.equals(payment.getTransactionId())) {
            payment.setTransactionId(transactionId);
        }
        paymentDAO.update(payment);

        LOGGER.log(Level.INFO, "Payment successful: {0}", payment.getTransactionId());
    }

    // ==================== INNER CLASS ====================

    /**
     * Payment processing result
     */
    public static class PaymentResult {
        private final boolean success;
        private final String message;
        private final PaymentStatus status;
        private final String redirectUrl;

        public PaymentResult(boolean success, String message, PaymentStatus status, String redirectUrl) {
            this.success = success;
            this.message = message;
            this.status = status;
            this.redirectUrl = redirectUrl;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public PaymentStatus getStatus() {
            return status;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public boolean requiresRedirect() {
            return redirectUrl != null && !redirectUrl.isEmpty();
        }
    }
}
