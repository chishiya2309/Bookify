package com.bookstore.service;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation pattern (RFC 5322 simplified)
    // Ensures no consecutive dots and proper structure
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
    );
    
    // SQL injection patterns to detect obvious attack vectors
    // Note: This is a defense-in-depth measure. Primary protection comes from parameterized queries.
    // This pattern focuses on SQL metacharacters and comment syntax rather than keywords
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        ".*([';]|--|/\\*|\\*/|xp_|sp_|@@|char\\s*\\(|exec\\s*\\(|cast\\s*\\().*",
        Pattern.CASE_INSENSITIVE
    );
    
    // Password validation patterns (pre-compiled for performance)
    private static final Pattern PASSWORD_UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SPECIAL_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    
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
     * Requires: at least 8 characters, one uppercase, one lowercase, one digit, one special character
     * @param password the password to validate
     * @return true if password meets security requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Use pre-compiled patterns for better performance
        return PASSWORD_UPPERCASE_PATTERN.matcher(password).matches()
            && PASSWORD_LOWERCASE_PATTERN.matcher(password).matches()
            && PASSWORD_DIGIT_PATTERN.matcher(password).matches()
            && PASSWORD_SPECIAL_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validates password with basic requirements (for backward compatibility)
     * Requires: at least 6 characters
     * @param password the password to validate
     * @return true if password meets minimum length, false otherwise
     */
    public static boolean isValidPasswordBasic(String password) {
        return password != null && password.length() >= 6;
    }
}
