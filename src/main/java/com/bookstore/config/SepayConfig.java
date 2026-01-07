package com.bookstore.config;

/**
 * Sepay Documentation: https://docs.sepay.vn/
 */
public class SepayConfig {

    // ========== SEPAY API CONFIGURATION ==========
    /**
     * Sepay API Base URL
     */
    public static final String API_BASE_URL = "https://my.sepay.vn/userapi";

    /**
     * API Token - Lấy từ Sepay Dashboard → API
     * Required: Set environment variable SEPAY_API_TOKEN
     */
    private static final String API_TOKEN = System.getenv("SEPAY_API_TOKEN");

    /**
     * Account Number - Số tài khoản ngân hàng nhận tiền
     * Required: Set environment variable SEPAY_ACCOUNT_NUMBER
     */
    private static final String ACCOUNT_NUMBER = System.getenv("SEPAY_ACCOUNT_NUMBER");

    /**
     * Bank Code - Mã ngân hàng (VD: MB, VCB, TCB, ACB...)
     * Required: Set environment variable SEPAY_BANK_CODE
     */
    private static final String BANK_CODE = System.getenv("SEPAY_BANK_CODE");

    // ========== PAYMENT SETTINGS ==========
    /**
     * Payment timeout in minutes
     */
    public static final int PAYMENT_TIMEOUT_MINUTES = 15;

    /**
     * Currency
     */
    public static final String CURRENCY = "VND";

    /**
     * Transfer content prefix
     */
    public static final String TRANSFER_CONTENT_PREFIX = "BOOKIFY";

    // ========== SEPAY MERCHANT CONFIGURATION ==========
    /**
     * Merchant ID (Mã đơn vị) - Get from Sepay Dashboard -> Cổng thanh toán QR
     * Required: Set environment variable SEPAY_MERCHANT_ID
     */
    private static final String MERCHANT_ID = System.getenv("SEPAY_MERCHANT_ID");

    /**
     * Secret Key - Get from Sepay Dashboard -> Cổng thanh toán QR
     * Required: Set environment variable SEPAY_SECRET_KEY
     */
    private static final String SECRET_KEY = System.getenv("SEPAY_SECRET_KEY");

    /**
     * Webhook API Key - Get from Sepay Dashboard
     * Required: Set environment variable SEPAY_WEBHOOK_API_KEY
     */
    private static final String WEBHOOK_API_KEY = System.getenv("SEPAY_WEBHOOK_API_KEY");

    // ========== CALLBACK URLs ==========
    /**
     * Return URL - Where customer is redirected after payment
     * Required: Set environment variable SEPAY_RETURN_URL (use HTTPS in production)
     */
    private static String returnUrl = System.getenv("SEPAY_RETURN_URL");

    /**
     * Notify URL - Webhook endpoint for Sepay notifications
     * Required: Set environment variable SEPAY_NOTIFY_URL (use HTTPS in production)
     */
    private static String notifyUrl = System.getenv("SEPAY_NOTIFY_URL");

    // ========== GETTERS ==========
    public static String getApiToken() {
        return API_TOKEN;
    }

    public static String getAccountNumber() {
        return ACCOUNT_NUMBER;
    }

    public static String getBankCode() {
        return BANK_CODE;
    }

    public static String getReturnUrl() {
        return returnUrl;
    }

    public static void setReturnUrl(String url) {
        returnUrl = url;
    }

    public static String getNotifyUrl() {
        return notifyUrl;
    }

    public static void setNotifyUrl(String url) {
        notifyUrl = url;
    }

    public static String getMerchantId() {
        return MERCHANT_ID;
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }

    /**
     * Generate transfer content for order
     * Format: BOOKIFY [ORDER_ID]
     */
    public static String generateTransferContent(Integer orderId) {
        return String.format("%s %d", TRANSFER_CONTENT_PREFIX, orderId);
    }

    /**
     * Verify IPN signature from Sepay using HMAC-SHA256
     * 
     * @param payload   Request body
     * @param signature Signature from X-Sepay-Signature header
     * @return true if signature is valid
     */
    public static boolean verifyWebhookSignature(String payload, String signature) {
        // If no signature provided, fail verification
        if (signature == null || signature.isEmpty()) {
            return false;
        }

        // If secret key not configured, skip signature verification
        if (SECRET_KEY == null || SECRET_KEY.isEmpty() || SECRET_KEY.equals("YOUR_SECRET_KEY_HERE")) {
            // Log warning but allow for testing purposes
            return true;
        }

        try {
            javax.crypto.Mac sha256Hmac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    SECRET_KEY.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String calculatedSignature = java.util.Base64.getEncoder().encodeToString(hash);

            // Constant-time comparison to prevent timing attacks
            return java.security.MessageDigest.isEqual(
                    calculatedSignature.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    signature.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify API Key from webhook request header
     * The API Key should match the one configured in Sepay Dashboard
     * 
     * @param apiKey API Key from X-Sepay-Api-Key or Authorization header
     * @return true if API Key is valid
     */
    public static boolean verifyWebhookApiKey(String apiKey) {
        // Nếu WEBHOOK_API_KEY chưa cấu hình (rỗng), cho phép tất cả requests
        // Điều này cho phép hoạt động khi chưa setup API Key trong Sepay
        if (WEBHOOK_API_KEY == null || WEBHOOK_API_KEY.isEmpty()) {
            return true;
        }

        // Nếu đã cấu hình API Key, bắt buộc phải verify
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }

        // Verify API Key matches
        return WEBHOOK_API_KEY.equals(apiKey);
    }

    /**
     * Get Webhook API Key (for testing/debugging)
     */
    public static String getWebhookApiKey() {
        return WEBHOOK_API_KEY;
    }

    /**
     * Combined webhook verification - checks both API Key and Signature
     * 
     * @param apiKey    API Key from header
     * @param payload   Request body
     * @param signature Signature from header (optional)
     * @return true if verification passes
     */
    public static boolean verifyWebhook(String apiKey, String payload, String signature) {
        // Step 1: Verify API Key (required)
        if (!verifyWebhookApiKey(apiKey)) {
            return false;
        }

        // Step 2: Verify Signature if provided
        if (signature != null && !signature.isEmpty()) {
            return verifyWebhookSignature(payload, signature);
        }

        return true;
    }

    /**
     * Check if Sepay is properly configured
     */
    public static boolean isConfigured() {
        return !API_TOKEN.equals("YOUR_SEPAY_API_TOKEN_HERE")
                && !SECRET_KEY.equals("YOUR_SECRET_KEY_HERE");
    }

    /**
     * Get bank name from code
     */
    public static String getBankName(String code) {
        switch (code.toUpperCase()) {
            case "VCB":
                return "Vietcombank";
            case "TCB":
                return "Techcombank";
            case "MB":
                return "MBBank";
            case "ACB":
                return "ACB";
            case "VTB":
                return "VietinBank";
            case "TPB":
                return "TPBank";
            case "BIDV":
                return "BIDV";
            case "AGR":
                return "Agribank";
            case "SCB":
                return "Sacombank";
            case "VPB":
                return "VPBank";
            default:
                return code;
        }
    }

    /**
     * Get full bank name
     */
    public static String getFullBankName() {
        return getBankName(BANK_CODE);
    }

    /**
     * Get bank BIN (Bank Identification Number) for VietQR
     * Reference: https://api.vietqr.io/v2/banks
     */
    public static String getBankBin(String code) {
        switch (code.toUpperCase()) {
            case "VCB":
                return "970436";
            case "TCB":
                return "970407";
            case "MB":
                return "970422";
            case "ACB":
                return "970416";
            case "VTB":
                return "970415";
            case "TPB":
                return "970423";
            case "BIDV":
                return "970418";
            case "AGR":
                return "970405";
            case "SCB":
                return "970403";
            case "VPB":
                return "970432";
            default:
                return code;
        }
    }

    /**
     * Generate VietQR URL for displaying QR code
     * Uses img.vietqr.io API
     * 
     * @param orderId Order ID
     * @param amount  Amount in VND
     * @return VietQR image URL
     */
    public static String generateVietQRUrl(Integer orderId, long amount) {
        String bankBin = getBankBin(BANK_CODE);
        String transferContent = generateTransferContent(orderId);

        // VietQR URL format:
        // https://img.vietqr.io/image/{BANK_BIN}-{ACCOUNT_NO}-{TEMPLATE}.png
        // With query params: ?amount={AMOUNT}&addInfo={CONTENT}&accountName={NAME}
        return String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                bankBin,
                ACCOUNT_NUMBER,
                amount,
                java.net.URLEncoder.encode(transferContent, java.nio.charset.StandardCharsets.UTF_8),
                java.net.URLEncoder.encode("BOOKIFY SHOP", java.nio.charset.StandardCharsets.UTF_8));
    }
}
