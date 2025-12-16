package com.bookstore.service;

/**
 * Application configuration utility to handle environment-based settings.
 * This class provides centralized configuration management for security and 
 * environment-specific settings.
 */
public class AppConfig {
    
    private static final String ENV_PRODUCTION = "production";
    private static final String ENV_KEY = "APP_ENV";
    private static final String SECURE_COOKIE_KEY = "SECURE_COOKIES";
    
    /**
     * Determines if cookies should be marked as secure (HTTPS only).
     * In production environments or when explicitly configured, cookies will be secure.
     * 
     * Configuration priority:
     * 1. SECURE_COOKIES environment variable (explicit override)
     * 2. APP_ENV=production (defaults to secure)
     * 3. Default to false for development
     * 
     * @return true if cookies should be secure, false otherwise
     */
    public static boolean isSecureCookiesEnabled() {
        // Check explicit SECURE_COOKIES setting first
        String secureCookiesSetting = System.getenv(SECURE_COOKIE_KEY);
        if (secureCookiesSetting != null) {
            return Boolean.parseBoolean(secureCookiesSetting);
        }
        
        // Check if running in production environment
        String environment = System.getenv(ENV_KEY);
        if (environment != null && environment.equalsIgnoreCase(ENV_PRODUCTION)) {
            return true;
        }
        
        // Default to false for development
        return false;
    }
    
    /**
     * Gets the current application environment.
     * 
     * @return the environment name, defaults to "development"
     */
    public static String getEnvironment() {
        String environment = System.getenv(ENV_KEY);
        return environment != null ? environment : "development";
    }
}
