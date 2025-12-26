package com.bookstore.config;

import java.util.Properties;

/**
 * EmailConfig - Configuration for Brevo (Sendinblue) SMTP
 **/
public class EmailConfig {

    // Brevo SMTP Configuration (Port 587 with STARTTLS)
    private static final String SMTP_HOST = "smtp-relay.brevo.com";
    private static final String SMTP_PORT = "587";

    private static final String SMTP_USERNAME = "9d4aab001@smtp-brevo.com";
    private static final String SMTP_PASSWORD = "xsmtpsib-2f26310a595e31fce16822c7836520b6bf7f0b523095efcb4f107fa5249ccf51-32OypTEmSKw5kYUa";

    private static final String FROM_EMAIL = "lequanghung.work@gmail.com";
    private static final String FROM_NAME = "Bookify - Nhà sách trực tuyến";

    /**
     * Get SMTP username
     */
    public static String getSmtpUsername() {
        if (SMTP_USERNAME == null || SMTP_USERNAME.isEmpty() || SMTP_USERNAME.equals("YOUR_BREVO_EMAIL")) {
            throw new IllegalStateException("Brevo SMTP username not configured. Update EmailConfig.java");
        }
        return SMTP_USERNAME;
    }

    /**
     * Get SMTP password (SMTP Key)
     */
    public static String getSmtpPassword() {
        if (SMTP_PASSWORD == null || SMTP_PASSWORD.isEmpty() || SMTP_PASSWORD.equals("YOUR_BREVO_SMTP_KEY")) {
            throw new IllegalStateException(
                    "Brevo SMTP key not configured. Get it from: https://app.brevo.com/settings/keys/smtp");
        }
        return SMTP_PASSWORD;
    }

    /**
     * Get mail properties for Jakarta Mail (STARTTLS on Port 587)
     */
    public static Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // STARTTLS for port 587
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.debug", "true");
        return props;
    }

    public static String getFromEmail() {
        return FROM_EMAIL;
    }

    public static String getFromName() {
        return FROM_NAME;
    }

    public static String getSmtpHost() {
        return SMTP_HOST;
    }

    public static int getSmtpPort() {
        return Integer.parseInt(SMTP_PORT);
    }

    public static boolean isConfigured() {
        return !SMTP_USERNAME.equals("YOUR_BREVO_EMAIL")
                && !SMTP_PASSWORD.equals("YOUR_BREVO_SMTP_KEY");
    }
}
