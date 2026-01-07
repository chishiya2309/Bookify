package com.bookstore.service;

import com.bookstore.config.EmailConfig;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.Payment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * EmailService - Dịch vụ gửi email thông qua Brevo HTTP API
 * Email được gửi bất đồng bộ để tránh chặn luồng chính (main thread)
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    @SuppressWarnings("deprecation")
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getInstance(new Locale("vi", "VN"));

    // Thread pool để gửi email bất đồng bộ - tránh block các thread xử lý
    // webhook/request
    private static final ExecutorService EMAIL_EXECUTOR = Executors.newFixedThreadPool(2);

    /**
     * Send email with HTML content (async - non-blocking)
     * Uses Brevo HTTP API instead of SMTP for cloud compatibility
     * 
     * @param to          Email người nhận
     * @param subject     Tiêu đề email
     * @param htmlContent Nội dung email
     */
    public void sendEmail(String to, String subject, String htmlContent) {
        EMAIL_EXECUTOR.submit(() -> sendEmailViaApi(to, subject, htmlContent));
    }

    /**
     * Gửi email đồng bộ (sync) qua Brevo HTTP API
     */
    private void sendEmailViaApi(String to, String subject, String htmlContent) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(EmailConfig.getApiUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("api-key", EmailConfig.getApiKey());
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            // Build payload JSON - escape các ký tự đặc biệt trong nội dung
            String escapedHtmlContent = escapeJsonString(htmlContent);
            String escapedSubject = escapeJsonString(subject);
            String escapedFromName = escapeJsonString(EmailConfig.getFromName());

            String jsonPayload = String.format(
                    "{" +
                            "\"sender\":{\"name\":\"%s\",\"email\":\"%s\"}," +
                            "\"to\":[{\"email\":\"%s\"}]," +
                            "\"subject\":\"%s\"," +
                            "\"htmlContent\":\"%s\"" +
                            "}",
                    escapedFromName,
                    EmailConfig.getFromEmail(),
                    to,
                    escapedSubject,
                    escapedHtmlContent);

            // Gửi request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Kiểm tra response
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOGGER.log(Level.INFO, "Email sent successfully via Brevo API to: {0}, subject: {1}",
                        new Object[] { to, subject });
            } else {
                // Đọc response lỗi
                String errorResponse = "";
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    errorResponse = br.lines().collect(Collectors.joining("\n"));
                }
                LOGGER.log(Level.WARNING, "Brevo API returned error {0}: {1}",
                        new Object[] { responseCode, errorResponse });
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send email via Brevo API to: " + to + " - " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Escape các ký tự đặc biệt cho chuỗi JSON
     */
    private String escapeJsonString(String input) {
        if (input == null)
            return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Gửi email xác nhận đơn hàng
     * 
     * @param order Đơn hàng cần xác nhận
     */
    public void sendOrderConfirmation(Order order) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

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

            String template = loadTemplate("order-confirmation.html");

            String shippingFeeDisplay;
            if (order.getShippingFee() == null || order.getShippingFee().compareTo(BigDecimal.ZERO) == 0) {
                shippingFeeDisplay = "Miễn phí";
            } else {
                shippingFeeDisplay = CURRENCY_FORMATTER.format(order.getShippingFee()) + "₫";
            }

            BigDecimal subtotal = order.getSubtotal();
            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
                subtotal = order.getOrderDetails().stream()
                        .map(OrderDetail::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

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
     * Gửi email xác nhận thanh toán
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
     * Gửi email thông báo giao hàng
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
     * Gửi email thông báo hủy đơn hàng
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
     * Gửi email xác nhận giao hàng (khi đơn hàng được đánh dấu là DELIVERED)
     * Cảm ơn khách hàng đã mua hàng
     */
    public void sendDeliveryConfirmation(Order order) {
        try {
            String customerEmail = order.getCustomer().getEmail();
            String customerName = order.getCustomer().getFullName();

            StringBuilder itemsSummary = new StringBuilder();
            for (OrderDetail detail : order.getOrderDetails()) {
                itemsSummary.append(String.format(
                        "<tr>" +
                                "<td style='padding: 12px; border-bottom: 1px solid #e0e0e0;'>%s</td>" +
                                "<td style='padding: 12px; border-bottom: 1px solid #e0e0e0; text-align: center;'>%d</td>"
                                +
                                "<td style='padding: 12px; border-bottom: 1px solid #e0e0e0; text-align: right;'>%s₫</td>"
                                +
                                "</tr>",
                        detail.getBook().getTitle(),
                        detail.getQuantity(),
                        CURRENCY_FORMATTER.format(detail.getSubTotal())));
            }

            BigDecimal subtotal = order.getSubtotal();
            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
                subtotal = order.getOrderDetails().stream()
                        .map(OrderDetail::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            String shippingFeeDisplay;
            if (order.getShippingFee() == null || order.getShippingFee().compareTo(BigDecimal.ZERO) == 0) {
                shippingFeeDisplay = "Miễn phí";
            } else {
                shippingFeeDisplay = CURRENCY_FORMATTER.format(order.getShippingFee()) + "₫";
            }

            String voucherRow = "";
            if (order.getVoucherDiscount() != null && order.getVoucherDiscount().compareTo(BigDecimal.ZERO) > 0) {
                String voucherLabel = order.getVoucherCode() != null
                        ? "Giảm giá (" + order.getVoucherCode() + ")"
                        : "Giảm giá";
                voucherRow = "<tr style='background-color: #f8f9fa;'>" +
                        "<td colspan='2' style='padding: 12px; color: #dc3545; border-bottom: 1px solid #e0e0e0;'>"
                        + voucherLabel + "</td>" +
                        "<td style='padding: 12px; text-align: right; color: #dc3545; font-weight: bold; border-bottom: 1px solid #e0e0e0;'>-"
                        +
                        CURRENCY_FORMATTER.format(order.getVoucherDiscount()) + "₫</td>" +
                        "</tr>";
            }

            String template = loadTemplate("delivery-confirmation.html");
            String html = template
                    .replace("{{customerName}}", customerName)
                    .replace("{{orderId}}", order.getOrderId().toString())
                    .replace("{{items}}", itemsSummary.toString())
                    .replace("{{subtotal}}", CURRENCY_FORMATTER.format(subtotal) + "₫")
                    .replace("{{shippingFee}}", shippingFeeDisplay)
                    .replace("{{voucherRow}}", voucherRow)
                    .replace("{{totalAmount}}", CURRENCY_FORMATTER.format(order.getTotalAmount()) + "₫");

            sendEmail(customerEmail,
                    "✅ Đơn hàng #" + order.getOrderId() + " đã được giao thành công - Cảm ơn quý khách!", html);

            LOGGER.log(Level.INFO, "Delivery confirmation email sent for order: {0}", order.getOrderId());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send delivery confirmation for order: " + order.getOrderId(), e);
        }
    }

    /**
     * Gửi email thông báo cho admin
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
        }
    }

    /**
     * Load email template from resources
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

    private String getFallbackTemplate() {
        return "<!DOCTYPE html><html><body>" +
                "<h1>Bookify</h1>" +
                "<p>Cảm ơn bạn đã đặt hàng tại Bookify!</p>" +
                "</body></html>";
    }

    /**
     * Format địa chỉ giao hàng
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
            case "BANK_TRANSFER":
                return "Chuyển khoản ngân hàng";
            default:
                return method;
        }
    }

    // ==================== FORGOT PASSWORD OTP ====================

    /**
     * Gửi email OTP để reset mật khẩu (synchronous for immediate feedback)
     * 
     * @param toEmail recipient email
     * @param otpCode 6-digit OTP code
     * @return true if sent successfully
     */
    public boolean sendOtpEmail(String toEmail, String otpCode) {
        String subject = "Mã xác nhận đặt lại mật khẩu - Bookify";
        String htmlContent = buildOtpEmailTemplate(otpCode);

        return sendEmailSync(toEmail, subject, htmlContent);
    }

    /**
     * Gửi email đồng bộ (blocking) - dùng cho OTP khi cần phản hồi ngay lập tức
     */
    private boolean sendEmailSync(String to, String subject, String htmlContent) {
        HttpURLConnection connection = null;
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(EmailConfig.getApiUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("api-key", EmailConfig.getApiKey());
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            String escapedHtmlContent = escapeJsonString(htmlContent);
            String escapedSubject = escapeJsonString(subject);
            String escapedFromName = escapeJsonString(EmailConfig.getFromName());

            String jsonPayload = String.format(
                    "{" +
                            "\"sender\":{\"name\":\"%s\",\"email\":\"%s\"}," +
                            "\"to\":[{\"email\":\"%s\"}]," +
                            "\"subject\":\"%s\"," +
                            "\"htmlContent\":\"%s\"" +
                            "}",
                    escapedFromName,
                    EmailConfig.getFromEmail(),
                    to,
                    escapedSubject,
                    escapedHtmlContent);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                LOGGER.log(Level.INFO, "OTP email sent successfully to: {0}", to);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to send OTP email, response code: {0}", responseCode);
                return false;
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send OTP email to: " + to + " - " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Xây dựng template email OTP
     */
    private String buildOtpEmailTemplate(String otpCode) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "<div style='max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>"
                +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #0D6EFD; margin: 0;'>📚 Bookify</h1>" +
                "<p style='color: #666; margin-top: 5px;'>Nhà sách trực tuyến</p>" +
                "</div>" +
                "<h2 style='color: #333; text-align: center;'>Mã xác nhận đặt lại mật khẩu</h2>" +
                "<p style='color: #666; text-align: center;'>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng sử dụng mã sau:</p>"
                +
                "<div style='background: #f8f9fa; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0;'>"
                +
                "<span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #0D6EFD;'>" + otpCode
                + "</span>" +
                "</div>" +
                "<p style='color: #666; text-align: center; font-size: 14px;'>Mã này có hiệu lực trong <strong>5 phút</strong>.</p>"
                +
                "<p style='color: #999; text-align: center; font-size: 12px;'>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                +
                "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='color: #999; text-align: center; font-size: 12px;'>&copy; 2025 Bookify. All rights reserved.</p>"
                +
                "</div></body></html>";
    }
}
