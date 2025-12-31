package com.bookstore.config;

/**
 * EmailConfig - Configuration for Brevo HTTP API
 * Using HTTP API instead of SMTP for better cloud hosting compatibility
 * 
 * IMPORTANT: API Key should be set via environment variable BREVO_API_KEY
 * On Render: Settings > Environment > Add BREVO_API_KEY
 **/
public class EmailConfig {

    // Brevo HTTP API Configuration
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    // Read API Key from environment variable for security
    // Set BREVO_API_KEY in Render Dashboard: Environment > Add Environment Variable
    private static final String API_KEY = System.getenv("BREVO_API_KEY") != null
            ? System.getenv("BREVO_API_KEY")
            : ""; // Fallback empty - will throw error if not configured

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
     * Get Brevo API Key from environment variable
     */
    public static String getApiKey() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException(
                    "Brevo API key not configured. Set BREVO_API_KEY environment variable. " +
                            "Get your key from: https://app.brevo.com/settings/keys/api");
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
        return API_KEY != null && !API_KEY.isEmpty();
    }

    public static String getAdminEmail() {
        return ADMIN_EMAIL;
    }
}
