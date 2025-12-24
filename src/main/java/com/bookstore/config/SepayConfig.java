package com.bookstore.config;

/**
 * Sepay Configuration
 * Store Sepay API credentials and endpoints
 * In production, load from environment variables or config file
 */
public class SepayConfig {

    // Sepay API endpoints
    public static final String SEPAY_API_BASE_URL = "https://my.sepay.vn/userapi";
    public static final String SEPAY_PAYMENT_URL = SEPAY_API_BASE_URL + "/transactions/create";
    public static final String SEPAY_VERIFY_URL = SEPAY_API_BASE_URL + "/transactions/check";

    // Merchant credentials - MUST be configured from environment variables
    private static String merchantId = System.getenv("SEPAY_MERCHANT_ID");
    private static String secretKey = System.getenv("SEPAY_SECRET_KEY");
    private static String apiToken = System.getenv("SEPAY_API_TOKEN");

    // Callback URLs
    private static String returnUrl = "http://localhost:8080/Bookify/payment/return";
    private static String notifyUrl = "http://localhost:8080/Bookify/payment/notify";

    // Rate limit: 2 calls per second
    public static final int RATE_LIMIT_PER_SECOND = 2;

    public static String getMerchantId() {
        if (merchantId == null || merchantId.isEmpty()) {
            // For development, use test credentials
            return "test_merchant_id";
        }
        return merchantId;
    }

    public static void setMerchantId(String merchantId) {
        SepayConfig.merchantId = merchantId;
    }

    public static String getSecretKey() {
        if (secretKey == null || secretKey.isEmpty()) {
            // For development, use test credentials
            return "test_secret_key";
        }
        return secretKey;
    }

    public static void setSecretKey(String secretKey) {
        SepayConfig.secretKey = secretKey;
    }

    public static String getApiToken() {
        if (apiToken == null || apiToken.isEmpty()) {
            // For development, use test token
            return "test_api_token";
        }
        return apiToken;
    }

    public static void setApiToken(String apiToken) {
        SepayConfig.apiToken = apiToken;
    }

    public static String getReturnUrl() {
        return returnUrl;
    }

    public static void setReturnUrl(String returnUrl) {
        SepayConfig.returnUrl = returnUrl;
    }

    public static String getNotifyUrl() {
        return notifyUrl;
    }

    public static void setNotifyUrl(String notifyUrl) {
        SepayConfig.notifyUrl = notifyUrl;
    }

    /**
     * Check if Sepay is properly configured
     * 
     * @return true if configured
     */
    public static boolean isConfigured() {
        return merchantId != null && !merchantId.isEmpty()
                && secretKey != null && !secretKey.isEmpty();
    }
}
