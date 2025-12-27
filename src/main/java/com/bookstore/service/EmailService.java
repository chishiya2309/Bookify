package com.bookstore.service;

import com.bookstore.config.EmailConfig;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.Payment;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * EmailService - Service for sending emails via Brevo SMTP
 * Supports HTML templates and business email notifications
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    @SuppressWarnings("deprecation")
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getInstance(new Locale("vi", "VN"));

    /**
     * Send email with HTML content
     * 
     * @param to          Recipient email address
     * @param subject     Email subject
     * @param htmlContent HTML content
     */
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            // Create session
            Session session = Session.getInstance(
                    EmailConfig.getMailProperties(),
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    EmailConfig.getSmtpUsername(),
                                    EmailConfig.getSmtpPassword());
                        }
                    });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                    EmailConfig.getFromEmail(),
                    EmailConfig.getFromName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            // Send email
            Transport.send(message);

            LOGGER.log(Level.INFO, "Email sent successfully to: {0}, subject: {1}",
                    new Object[] { to, subject });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to: " + to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send order confirmation email
     * 
     * @param order Order to confirm
     */
    public void sendOrderConfirmation(Order order) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

            // Build items HTML
            StringBuilder itemsHtml = new StringBuilder();
            for (OrderDetail detail : order.getOrderDetails()) {
                String imageUrl = detail.getBook().getPrimaryImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = "https://via.placeholder.com/80x110?text=No+Image";
                }

                String authors = detail.getBook().getAuthors().stream()
                        .map(author -> author.getName())
                        .collect(Collectors.joining(", "));

                itemsHtml.append(String.format(
                        "<tr>" +
                                "<td style='padding: 15px; border-bottom: 1px solid #e0e0e0;'>" +
                                "<img src='%s' alt='%s' style='width: 60px; height: 80px; object-fit: cover; border-radius: 4px;'>"
                                +
                                "</td>" +
                                "<td style='padding: 15px; border-bottom: 1px solid #e0e0e0;'>" +
                                "<strong>%s</strong><br>" +
                                "<span style='color: #666; font-size: 13px;'>%s</span>" +
                                "</td>" +
                                "<td style='padding: 15px; border-bottom: 1px solid #e0e0e0; text-align: center;'>%d</td>"
                                +
                                "<td style='padding: 15px; border-bottom: 1px solid #e0e0e0; text-align: right;'>" +
                                "<strong>%s₫</strong>" +
                                "</td>" +
                                "</tr>",
                        imageUrl,
                        detail.getBook().getTitle(),
                        detail.getBook().getTitle(),
                        authors,
                        detail.getQuantity(),
                        CURRENCY_FORMATTER.format(detail.getSubTotal())));
            }

            // Load and populate template
            String template = loadTemplate("order-confirmation.html");

            // Format shipping fee - show "Miễn phí" if shipping fee is 0, otherwise show
            // the amount
            String shippingFeeDisplay;
            if (order.getShippingFee() == null || order.getShippingFee().compareTo(BigDecimal.ZERO) == 0) {
                shippingFeeDisplay = "Miễn phí";
            } else {
                shippingFeeDisplay = CURRENCY_FORMATTER.format(order.getShippingFee()) + "₫";
            }

            // Get subtotal - use order.getSubtotal() if available, otherwise calculate from
            // order details
            BigDecimal subtotal = order.getSubtotal();
            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
                // Calculate subtotal from order details if not set
                subtotal = order.getOrderDetails().stream()
                        .map(OrderDetail::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            // Build voucher discount row if applicable
            String voucherRow = "";
            if (order.getVoucherDiscount() != null && order.getVoucherDiscount().compareTo(BigDecimal.ZERO) > 0) {
                String voucherLabel = order.getVoucherCode() != null
                        ? "Giảm giá (" + order.getVoucherCode() + "):"
                        : "Giảm giá:";
                voucherRow = "<tr>" +
                        "<td style='color: #dc3545'>" + voucherLabel + "</td>" +
                        "<td align='right' style='color: #dc3545; font-weight: bold'>-" +
                        CURRENCY_FORMATTER.format(order.getVoucherDiscount()) + "₫</td>" +
                        "</tr>";
            }

            String html = template
                    .replace("{{customerName}}", customerName)
                    .replace("{{orderId}}", order.getOrderId().toString())
                    .replace("{{orderDate}}", order.getOrderDate().format(DATE_FORMATTER))
                    .replace("{{items}}", itemsHtml.toString())
                    .replace("{{shippingAddress}}", formatAddress(order))
                    .replace("{{recipientName}}", order.getRecipientName())
                    .replace("{{recipientPhone}}", order.getShippingAddress().getPhoneNumber())
                    .replace("{{paymentMethod}}", formatPaymentMethod(order.getPaymentMethod()))
                    .replace("{{subtotal}}", CURRENCY_FORMATTER.format(subtotal) + "₫")
                    .replace("{{shipping}}", shippingFeeDisplay)
                    .replace("{{voucherRow}}", voucherRow)
                    .replace("{{totalAmount}}", CURRENCY_FORMATTER.format(order.getTotalAmount()) + "₫");

            sendEmail(customerEmail, "Xác nhận đơn hàng #" + order.getOrderId(), html);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send order confirmation for order: " + order.getOrderId(), e);
        }
    }

    /**
     * Send payment confirmation email
     * 
     * @param order   Order
     * @param payment Payment
     */
    public void sendPaymentConfirmation(Order order, Payment payment) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

            String template = loadTemplate("payment-confirmation.html");
            String html = template
                    .replace("{{customerName}}", customerName)
                    .replace("{{orderId}}", order.getOrderId().toString())
                    .replace("{{transactionId}}",
                            payment.getTransactionId() != null ? payment.getTransactionId() : "N/A")
                    .replace("{{amount}}", CURRENCY_FORMATTER.format(payment.getAmount()))
                    .replace("{{paymentMethod}}", payment.getMethod().toString())
                    .replace("{{paymentDate}}",
                            payment.getPaymentDate() != null ? payment.getPaymentDate().format(DATE_FORMATTER) : "N/A");

            sendEmail(customerEmail, "Xác nhận thanh toán - Đơn hàng #" + order.getOrderId(), html);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send payment confirmation for order: " + order.getOrderId(), e);
        }
    }

    /**
     * Send shipping notification email
     * 
     * @param order          Order
     * @param trackingNumber Tracking number
     */
    public void sendShippingNotification(Order order, String trackingNumber) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

            String template = loadTemplate("shipping-notification.html");
            String html = template
                    .replace("{{customerName}}", customerName)
                    .replace("{{orderId}}", order.getOrderId().toString())
                    .replace("{{trackingNumber}}", trackingNumber)
                    .replace("{{carrier}}", "Giao hàng nhanh")
                    .replace("{{estimatedDelivery}}", "3-5 ngày làm việc");

            sendEmail(customerEmail, "Đơn hàng #" + order.getOrderId() + " đang được giao", html);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send shipping notification for order: " + order.getOrderId(), e);
        }
    }

    /**
     * Send order cancellation email
     * 
     * @param order  Cancelled order
     * @param reason Cancellation reason
     */
    public void sendOrderCancellation(Order order, String reason) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

            String template = loadTemplate("order-cancellation.html");
            String html = template
                    .replace("{{customerName}}", customerName)
                    .replace("{{orderId}}", order.getOrderId().toString())
                    .replace("{{reason}}", reason != null ? reason : "Theo yêu cầu của khách hàng")
                    .replace("{{cancelDate}}", java.time.LocalDateTime.now().format(DATE_FORMATTER))
                    .replace("{{totalAmount}}", CURRENCY_FORMATTER.format(order.getTotalAmount()));

            sendEmail(customerEmail, "Đơn hàng #" + order.getOrderId() + " đã bị huỷ", html);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send cancellation email for order: " + order.getOrderId(), e);
        }
    }

    /**
     * Send admin notification email for payment issues
     * 
     * @param subject Email subject
     * @param message Notification message content
     */
    public void sendAdminNotification(String subject, String message) {
        try {
            String adminEmail = EmailConfig.getAdminEmail();

            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset='UTF-8'></head>" +
                    "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #f9f9f9; border-radius: 8px; padding: 20px;'>"
                    +
                    "<h2 style='color: #d9534f; border-bottom: 2px solid #d9534f; padding-bottom: 10px;'>" +
                    "⚠️ " + subject + "</h2>" +
                    "<div style='background: white; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                    "<pre style='white-space: pre-wrap; font-family: inherit; margin: 0;'>" + message + "</pre>" +
                    "</div>" +
                    "<p style='color: #666; font-size: 12px; margin-top: 20px;'>" +
                    "Đây là email tự động từ hệ thống Bookify. Vui lòng xử lý kịp thời.</p>" +
                    "</div></body></html>";

            sendEmail(adminEmail, "[ADMIN] " + subject, html);

            LOGGER.log(Level.INFO, "Admin notification sent: {0}", subject);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send admin notification: " + subject, e);
            // Don't throw - admin notifications should not break main flow
        }
    }

    /**
     * Load email template from resources
     * 
     * @param templateName Template file name
     * @return Template content
     */
    private String loadTemplate(String templateName) {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("email-templates/" + templateName);

            if (is == null) {
                LOGGER.log(Level.WARNING, "Template not found: {0}, using fallback", templateName);
                return getFallbackTemplate();
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading template: " + templateName, e);
            return getFallbackTemplate();
        }
    }

    /**
     * Get fallback template when main template not found
     */
    private String getFallbackTemplate() {
        return "<!DOCTYPE html><html><body>" +
                "<h1>Bookify</h1>" +
                "<p>Cảm ơn bạn đã đặt hàng tại Bookify!</p>" +
                "</body></html>";
    }

    /**
     * Format shipping address
     */
    private String formatAddress(Order order) {
        return String.format("%s, %s, %s, %s",
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getWard(),
                order.getShippingAddress().getDistrict(),
                order.getShippingAddress().getProvince());
    }

    /**
     * Format payment method for display
     */
    private String formatPaymentMethod(String method) {
        if (method == null)
            return "N/A";
        switch (method.toUpperCase()) {
            case "COD":
                return "Thanh toán khi nhận hàng (COD)";
            case "SEPAY":
                return "Chuyển khoản ngân hàng (Sepay)";
            case "CREDIT_CARD":
                return "Thẻ tín dụng";
            case "BANK_TRANSFER":
                return "Chuyển khoản ngân hàng";
            default:
                return method;
        }
    }
}
