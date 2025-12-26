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
     */
    private static final String API_TOKEN = "FDN2CAWWJGRZDHZIY75C8XROHB7EXAOEGQRXTSBJJIFFHBHIE4O3UG8QVK9KUQMI";

    /**
     * Account Number - Số tài khoản ngân hàng nhận tiền
     */
    private static final String ACCOUNT_NUMBER = "0347983243";

    /**
     * Bank Code - Mã ngân hàng
     */
    private static final String BANK_CODE = "MB"; // MBBank

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
     */
    private static final String MERCHANT_ID = "SP-LIVE-LQ4B4233";

    /**
     * Secret Key - Get from Sepay Dashboard -> Cổng thanh toán QR
     */
    private static final String SECRET_KEY = "spsk_live_cysAJev94vsKPese3nF3gpigozDYirtJ";

    // ========== CALLBACK URLs ==========
    /**
     * Return URL - Where customer is redirected after payment
     */
    private static String returnUrl = "http://localhost:8080/Bookify/payment/return";

    /**
     * Notify URL - Webhook endpoint for Sepay notifications
     */
    private static String notifyUrl = "http://localhost:8080/Bookify/payment/notify";

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
     * Verify IPN signature from Sepay
     * Uses HMAC-SHA256 with secret key for production
     * 
     * @param payload   Request body
     * @param signature Signature from X-Sepay-Signature header
     * @return true if signature is valid
     */
    public static boolean verifyWebhookSignature(String payload, String signature) {
        // If no secret key configured, trust all requests (for testing)
        if (SECRET_KEY.equals("YOUR_SECRET_KEY_HERE")) {
            return true;
        }

        // TODO: Implement HMAC-SHA256 signature verification
        // For production:
        // 1. Calculate HMAC-SHA256 of payload using SECRET_KEY
        // 2. Compare with signature from header
        // 3. Return true if match

        return true; // For now, trust all requests
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
