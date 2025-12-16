package com.bookstore.service;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation pattern (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // SQL injection patterns to detect common attack vectors
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        ".*([';]|--|\\/\\*|\\*\\/|xp_|sp_|exec|execute|insert|select|delete|update|drop|create|alter|union).*",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Validates email format
     * @param email the email to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Checks if input contains potential SQL injection patterns
     * @param input the input to check
     * @return true if potential SQL injection detected, false otherwise
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).matches();
    }
    
    /**
     * Validates input is not null or empty
     * @param input the input to validate
     * @return true if input is valid (not null and not empty), false otherwise
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
    /**
     * Validates password strength
     * @param password the password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }
}
