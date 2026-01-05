package com.bookstore.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * VietnamTimeConfig - Centralized timezone configuration for Vietnam (GMT+7)
 * 
 * Use this class instead of LocalDateTime.now() to ensure consistent
 * Vietnam timezone across all environments (local dev, Render, etc.)
 */
public class VietnamTimeConfig {

    // Vietnam timezone: GMT+7 (Asia/Ho_Chi_Minh)
    public static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * Get current date-time in Vietnam timezone (GMT+7)
     * Use this instead of LocalDateTime.now()
     * 
     * @return LocalDateTime in Vietnam timezone
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }

    /**
     * Get Vietnam ZoneId for use with other date-time operations
     * 
     * @return ZoneId for Asia/Ho_Chi_Minh
     */
    public static ZoneId getZone() {
        return VIETNAM_ZONE;
    }
}
