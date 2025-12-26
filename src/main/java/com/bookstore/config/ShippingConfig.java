package com.bookstore.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * ShippingConfig - Configuration for shipping fee calculation
 * 
 * Fees are based on region (province) and order subtotal.
 * Free shipping for orders above threshold.
 */
public class ShippingConfig {

    // ========== THRESHOLDS ==========
    /** Miễn phí ship cho đơn hàng trên 300.000₫ */
    public static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("300000");

    // ========== SHIPPING FEES BY REGION ==========
    /** Nội thành Hà Nội, TP.HCM */
    public static final BigDecimal FEE_METRO = new BigDecimal("15000");

    /** Các tỉnh thành thông thường */
    public static final BigDecimal FEE_URBAN = new BigDecimal("25000");

    /** Vùng sâu xa, hải đảo */
    public static final BigDecimal FEE_REMOTE = new BigDecimal("35000");

    // ========== PROVINCE LISTS ==========
    /** Các thành phố lớn - phí thấp nhất */
    private static final Set<String> METRO_PROVINCES = new HashSet<>(Arrays.asList(
            "Hà Nội", "Ha Noi",
            "TP. Hồ Chí Minh", "TP.HCM", "Hồ Chí Minh", "Ho Chi Minh",
            "Thành phố Hồ Chí Minh"));

    /** Các tỉnh vùng sâu xa, hải đảo - phí cao nhất */
    private static final Set<String> REMOTE_PROVINCES = new HashSet<>(Arrays.asList(
            // Miền núi phía Bắc
            "Hà Giang", "Ha Giang",
            "Cao Bằng", "Cao Bang",
            "Bắc Kạn", "Bac Kan",
            "Lạng Sơn", "Lang Son",
            "Lai Châu", "Lai Chau",
            "Điện Biên", "Dien Bien",
            "Sơn La", "Son La",
            "Lào Cai", "Lao Cai",
            "Yên Bái", "Yen Bai",

            // Tây Nguyên
            "Kon Tum",
            "Gia Lai",
            "Đắk Lắk", "Dak Lak",
            "Đắk Nông", "Dak Nong",

            // Hải đảo
            "Kiên Giang", "Kien Giang", // Có Phú Quốc
            "Bà Rịa - Vũng Tàu", "Ba Ria - Vung Tau", // Có Côn Đảo
            "Quảng Ninh", "Quang Ninh" // Có các đảo
    ));

    /**
     * Calculate shipping fee based on province and order subtotal
     * 
     * @param province Shipping destination province
     * @param subtotal Order subtotal (before shipping)
     * @return Shipping fee (BigDecimal)
     */
    public static BigDecimal calculateShippingFee(String province, BigDecimal subtotal) {
        // Free shipping for orders above threshold
        if (subtotal != null && subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        // Determine region and return appropriate fee
        return getShippingFeeByProvince(province);
    }

    /**
     * Get shipping fee based on province name
     * 
     * @param province Province name
     * @return Shipping fee for the region
     */
    public static BigDecimal getShippingFeeByProvince(String province) {
        if (province == null || province.trim().isEmpty()) {
            return FEE_URBAN; // Default to urban fee
        }

        String normalizedProvince = normalizeProvinceName(province);

        // Check metro cities first (lowest fee)
        for (String metro : METRO_PROVINCES) {
            if (normalizedProvince.contains(normalizeProvinceName(metro))) {
                return FEE_METRO;
            }
        }

        // Check remote areas (highest fee)
        for (String remote : REMOTE_PROVINCES) {
            if (normalizedProvince.contains(normalizeProvinceName(remote))) {
                return FEE_REMOTE;
            }
        }

        // Default to urban fee
        return FEE_URBAN;
    }

    /**
     * Normalize province name for comparison
     * Removes diacritics and converts to lowercase
     */
    private static String normalizeProvinceName(String name) {
        if (name == null)
            return "";

        // Convert to lowercase and trim
        String normalized = name.toLowerCase().trim();

        // Remove common prefixes
        normalized = normalized
                .replace("tỉnh ", "")
                .replace("tinh ", "")
                .replace("thành phố ", "")
                .replace("thanh pho ", "")
                .replace("tp. ", "")
                .replace("tp.", "");

        return normalized;
    }

    /**
     * Get human-readable region name for display
     * 
     * @param province Province name
     * @return Region name in Vietnamese
     */
    public static String getRegionName(String province) {
        BigDecimal fee = getShippingFeeByProvince(province);

        if (fee.equals(FEE_METRO)) {
            return "Nội thành";
        } else if (fee.equals(FEE_REMOTE)) {
            return "Vùng sâu xa";
        } else {
            return "Tỉnh thành";
        }
    }

    /**
     * Check if shipping is free for the given subtotal
     * 
     * @param subtotal Order subtotal
     * @return true if free shipping applies
     */
    public static boolean isFreeShipping(BigDecimal subtotal) {
        return subtotal != null && subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0;
    }

    /**
     * Get amount needed for free shipping
     * 
     * @param subtotal Current order subtotal
     * @return Amount needed (0 if already qualifies)
     */
    public static BigDecimal getAmountForFreeShipping(BigDecimal subtotal) {
        if (isFreeShipping(subtotal)) {
            return BigDecimal.ZERO;
        }
        return FREE_SHIPPING_THRESHOLD.subtract(subtotal);
    }
}
