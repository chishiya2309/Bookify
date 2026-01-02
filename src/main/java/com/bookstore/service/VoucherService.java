package com.bookstore.service;

import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.Voucher;
import com.bookstore.model.Voucher.DiscountType;
import com.bookstore.model.VoucherUsage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VoucherService - Business logic for voucher operations
 */
public class VoucherService {

    private static final Logger LOGGER = Logger.getLogger(VoucherService.class.getName());
    private final VoucherDAO voucherDAO = new VoucherDAO();

    public static class ValidationResult {
        private boolean valid;
        private String message;
        private Voucher voucher;
        private BigDecimal discount;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public ValidationResult(boolean valid, String message, Voucher voucher, BigDecimal discount) {
            this.valid = valid;
            this.message = message;
            this.voucher = voucher;
            this.discount = discount;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public Voucher getVoucher() {
            return voucher;
        }

        public BigDecimal getDiscount() {
            return discount;
        }
    }

    /**
     * Validate voucher code for a customer and order amount
     * 
     * @param code        Voucher code
     * @param subtotal    Order subtotal (before shipping and discount)
     * @param shippingFee Shipping fee
     * @param customer    Customer applying the voucher
     * @return ValidationResult with status and message
     */
    public ValidationResult validateVoucher(String code, BigDecimal subtotal,
            BigDecimal shippingFee, Customer customer) {

        if (code == null || code.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập mã giảm giá");
        }

        Voucher voucher = voucherDAO.findByCode(code.trim());
        if (voucher == null) {
            return new ValidationResult(false, "Mã giảm giá không tồn tại");
        }

        if (voucher.getIsActive() == null || !voucher.getIsActive()) {
            return new ValidationResult(false, "Mã giảm giá đã bị vô hiệu hóa");
        }

        LocalDateTime now = com.bookstore.config.VietnamTimeConfig.now();
        if (now.isBefore(voucher.getStartDate())) {
            return new ValidationResult(false, "Mã giảm giá chưa có hiệu lực");
        }
        if (now.isAfter(voucher.getEndDate())) {
            return new ValidationResult(false, "Mã giảm giá đã hết hạn");
        }

        if (voucher.getMinOrderAmount() != null &&
                subtotal.compareTo(voucher.getMinOrderAmount()) < 0) {
            return new ValidationResult(false,
                    String.format("Đơn hàng tối thiểu %,.0f₫ để áp dụng mã này",
                            voucher.getMinOrderAmount()));
        }

        if (voucher.getMaxUses() != null &&
                voucher.getCurrentUses() >= voucher.getMaxUses()) {
            return new ValidationResult(false, "Mã giảm giá đã hết lượt sử dụng");
        }

        if (customer != null && voucher.getMaxUsesPerUser() != null) {
            int userUsageCount = voucherDAO.getUserUsageCount(
                    voucher.getVoucherId(), customer.getUserId());
            if (userUsageCount >= voucher.getMaxUsesPerUser()) {
                return new ValidationResult(false, "Bạn đã sử dụng hết lượt cho mã này");
            }
        }

        BigDecimal discount = calculateDiscount(voucher, subtotal, shippingFee);

        String successMessage = buildSuccessMessage(voucher, discount);

        LOGGER.log(Level.INFO, "Voucher {0} validated successfully. Discount: {1}",
                new Object[] { code, discount });

        return new ValidationResult(true, successMessage, voucher, discount);
    }

    /**
     * Tính tiền giảm dựa trên loại voucher
     */
    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal subtotal, BigDecimal shippingFee) {
        if (voucher == null || subtotal == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        switch (voucher.getDiscountType()) {
            case PERCENTAGE:
                // Tính phần trăm của tổng phụ
                discount = subtotal.multiply(voucher.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);

                // Áp dụng trần tiền giảm nếu có
                if (voucher.getMaxDiscount() != null &&
                        discount.compareTo(voucher.getMaxDiscount()) > 0) {
                    discount = voucher.getMaxDiscount();
                }
                break;

            case FIXED_AMOUNT:
                // Giảm tiền cố định (không vượt quá tổng phụ)
                discount = voucher.getDiscountValue();
                if (discount.compareTo(subtotal) > 0) {
                    discount = subtotal;
                }
                break;

            case FREE_SHIPPING:
                // Giảm phí vận chuyển
                discount = shippingFee != null ? shippingFee : BigDecimal.ZERO;
                break;
        }

        return discount;
    }

    /**
     * Áp dụng voucher vào đơn hàng và ghi nhận sử dụng
     */
    public void applyVoucher(Order order, Voucher voucher, BigDecimal discountAmount) {
        if (order == null || voucher == null) {
            return;
        }

        order.setVoucherId(voucher.getVoucherId());
        order.setVoucherCode(voucher.getCode());
        order.setVoucherDiscount(discountAmount);

        voucherDAO.incrementUsage(voucher.getVoucherId());

        VoucherUsage usage = new VoucherUsage(
                voucher,
                order.getCustomer(),
                order,
                discountAmount);
        voucherDAO.recordUsage(usage);

        LOGGER.log(Level.INFO, "Voucher {0} applied to order {1}. Discount: {2}",
                new Object[] { voucher.getCode(), order.getOrderId(), discountAmount });
    }

    private String buildSuccessMessage(Voucher voucher, BigDecimal discount) {
        switch (voucher.getDiscountType()) {
            case PERCENTAGE:
                if (voucher.getMaxDiscount() != null) {
                    return String.format("Giảm %,.0f%% (tối đa %,.0f₫) - Tiết kiệm %,.0f₫",
                            voucher.getDiscountValue(), voucher.getMaxDiscount(), discount);
                }
                return String.format("Giảm %,.0f%% - Tiết kiệm %,.0f₫",
                        voucher.getDiscountValue(), discount);

            case FIXED_AMOUNT:
                return String.format("Giảm %,.0f₫", discount);

            case FREE_SHIPPING:
                return "Miễn phí vận chuyển";

            default:
                return String.format("Tiết kiệm %,.0f₫", discount);
        }
    }

    public Voucher findByCode(String code) {
        return voucherDAO.findByCode(code);
    }
}
