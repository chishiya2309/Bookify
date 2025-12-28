package com.bookstore.config;

/**
 * EmailConfig - Configuration for Brevo HTTP API
 * Using HTTP API instead of SMTP for better cloud hosting compatibility
 * 
 * IMPORTANT: On Render.com and other cloud providers, SMTP ports (587, 465)
 * are often blocked. HTTP API works reliably through standard HTTPS (port 443).
 **/
public class EmailConfig {

    // Brevo HTTP API Configuration
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    // Brevo API Key - Get from: https://app.brevo.com/settings/keys/api
    // This is different from SMTP key!
    private static final String API_KEY = "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";

    private static final String FROM_EMAIL = "lequanghung.work@gmail.com";
    private static final String FROM_NAME = "Bookify - Nhà sách trực tuyến";

    // Admin email for payment issue notifications
    private static final String ADMIN_EMAIL = "lequanghung.work@gmail.com";

    /**
     * Get Brevo API URL
     */
    public static String getApiUrl() {
        return API_URL;
    }

    /**
     * Get Brevo API Key
     */
    public static String getApiKey() {
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.startsWith("YOUR_")) {
            throw new IllegalStateException(
                    "Brevo API key not configured. Get it from: https://app.brevo.com/settings/keys/api");
        }
        return API_KEY;
    }

    public static String getFromEmail() {
        return FROM_EMAIL;
    }

    public static String getFromName() {
        return FROM_NAME;
    }

    public static boolean isConfigured() {
        return API_KEY != null && !API_KEY.isEmpty() && !API_KEY.startsWith("YOUR_");
    }

    public static String getAdminEmail() {
        return ADMIN_EMAIL;
    }
}
