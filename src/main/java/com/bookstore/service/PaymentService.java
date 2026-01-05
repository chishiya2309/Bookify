package com.bookstore.service;

import com.bookstore.dao.PaymentDAO;
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
        payment.setPaymentDate(com.bookstore.config.VietnamTimeConfig.now());

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
            // Xử lý đặc biệt: Nếu số tiền = 0đ, tự động hoàn tất payment
            if (payment.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentDAO.update(payment);
                LOGGER.log(Level.INFO, "Zero amount payment auto-completed: {0}", payment.getTransactionId());
                return new PaymentResult(true, "Đơn hàng miễn phí - không cần thanh toán",
                        PaymentStatus.COMPLETED, null);
            }

            switch (payment.getMethod()) {
                case COD:
                    return processCODPayment(payment);
                case BANK_TRANSFER:
                    return processBankTransferPayment(payment, paymentDetails);
                case CREDIT_CARD:
                    return processCreditCardPayment(payment, paymentDetails);// Mock for now
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

        // For bank transfer, payment is verified via Sepay webhook
        // Just check the current status
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
     * Process bank transfer payment using VietQR
     * Payment status remains PENDING until Sepay webhook confirms payment
     * 
     * Flow:
     * 1. Create payment with PENDING status
     * 2. User sees VietQR code on order-confirmation page
     * 3. User transfers money via banking app
     * 4. Sepay monitors bank account and sends webhook
     * 5. SepayWebhookServlet updates order to PAID
     * 
     * @param payment        Payment to process
     * @param paymentDetails Bank details (not used for VietQR)
     * @return Payment result
     */
    private PaymentResult processBankTransferPayment(Payment payment, Map<String, Object> paymentDetails) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentGateway("VietQR");
        paymentDAO.update(payment);

        LOGGER.log(Level.INFO, "Bank transfer payment initiated: {0}", payment.getTransactionId());

        // No redirect - user stays on order-confirmation page with QR code
        return new PaymentResult(true,
                "Vui lòng quét mã QR hoặc chuyển khoản theo thông tin bên dưới",
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

    // ==================== VALIDATION ====================

    /**
     * Validate payment amount
     * 
     * @param amount Amount to validate
     */
    public void validatePaymentAmount(BigDecimal amount) {
        // Cho phép số tiền = 0 (khi áp voucher 100%)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
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
        String timestamp = com.bookstore.config.VietnamTimeConfig.now()
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
