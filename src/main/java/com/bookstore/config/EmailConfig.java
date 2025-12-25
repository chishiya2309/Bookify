package com.bookstore.config;

import java.util.Properties;

/**
 * EmailConfig - Configuration for Brevo (Sendinblue) SMTP
 * Free tier: 300 emails/day
 */
public class EmailConfig {

    // Brevo SMTP Configuration
    private static final String SMTP_HOST = "smtp-relay.brevo.com";
    private static final String SMTP_PORT = "587";

    // From environment variables
    private static final String SMTP_USERNAME = "lequanghung.work@gmail.com";
    private static final String SMTP_PASSWORD = "xsmtpsib-2f26310a595e31fce16822c7836520b6bf7f0b523095efcb4f107fa5249ccf51-UZu3pUybcHasekqp";

    // Email settings
    private static final String FROM_EMAIL = "noreply@bookify.com";
    private static final String FROM_NAME = "Bookify - Books Delivered To Your Door";

    /**
     * Get SMTP username from environment
     */
    public static String getSmtpUsername() {
        if (SMTP_USERNAME == null || SMTP_USERNAME.isEmpty()) {
            throw new IllegalStateException("BREVO_SMTP_USERNAME environment variable not set");
        }
        return SMTP_USERNAME;
    }

    /**
     * Get SMTP password from environment
     */
    public static String getSmtpPassword() {
        if (SMTP_PASSWORD == null || SMTP_PASSWORD.isEmpty()) {
            throw new IllegalStateException("BREVO_SMTP_PASSWORD environment variable not set");
        }
        return SMTP_PASSWORD;
    }

    /**
     * Get mail properties for Jakarta Mail
     */
    public static Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        return props;
    }

    /**
     * Get from email address
     */
    public static String getFromEmail() {
        return FROM_EMAIL;
    }

    /**
     * Get from name
     */
    public static String getFromName() {
        return FROM_NAME;
    }

    /**
     * Get SMTP host
     */
    public static String getSmtpHost() {
        return SMTP_HOST;
    }

    /**
     * Get SMTP port
     */
    public static int getSmtpPort() {
        return Integer.parseInt(SMTP_PORT);
    }
}
