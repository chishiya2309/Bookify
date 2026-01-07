package com.bookstore.config;

/**
 * EmailConfig - Configuration for Brevo HTTP API
 **/
public class EmailConfig {

    // Brevo HTTP API Configuration
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    // Read API Key from environment variable for security
    private static final String API_KEY = System.getenv("BREVO_API_KEY") != null
            ? System.getenv("BREVO_API_KEY")
            : ""; // Giá trị dự phòng bị trống - sẽ báo lỗi nếu chưa được cấu hình.

    private static final String FROM_EMAIL = "lequanghung.work@gmail.com";
    private static final String FROM_NAME = "Bookify - Nhà sách trực tuyến";

    // Email quản trị viên nhận thông báo sự cố thanh toán
    private static final String ADMIN_EMAIL = "lequanghung.work@gmail.com";

    /**
     * Lấy Brevo API URL
     */
    public static String getApiUrl() {
        return API_URL;
    }

    /**
     * Lấy Brevo API Key từ biến môi trường
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
