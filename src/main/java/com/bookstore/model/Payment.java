package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bookstore.config.VietnamTimeConfig;

@Entity
@Table(name = "payments", indexes = {
        // Tìm payment theo order (quan hệ 1-1, query thường xuyên)
        @Index(name = "idx_payments_order_id", columnList = "order_id"),

        // Tìm payment theo transaction_id (tra cứu giao dịch từ payment gateway)
        @Index(name = "idx_payments_transaction_id", columnList = "transaction_id"),

        // Lọc payment theo trạng thái (PENDING, COMPLETED, FAILED, REFUNDED)
        @Index(name = "idx_payments_status", columnList = "status"),

        // Lọc payment theo phương thức (CREDIT_CARD, COD, BANK_TRANSFER, E_WALLET)
        @Index(name = "idx_payments_method", columnList = "method"),

        // Lọc payment theo ngày (báo cáo doanh thu, reconciliation)
        @Index(name = "idx_payments_date", columnList = "payment_date DESC"),

        // Composite: Status + Date (thanh toán đang pending, failed cần xử lý)
        @Index(name = "idx_payments_status_date", columnList = "status, payment_date DESC"),

        // Composite: Method + Date (phân tích phương thức thanh toán)
        @Index(name = "idx_payments_method_date", columnList = "method, payment_date DESC"),

        // Thống kê theo số tiền (phân tích giao dịch lớn)
        @Index(name = "idx_payments_amount", columnList = "amount DESC"),

        // Composite: Status + Method (phân tích tỷ lệ thành công theo phương thức)
        @Index(name = "idx_payments_status_method", columnList = "status, method")
}, uniqueConstraints = {
        // Mỗi đơn hàng chỉ có 1 payment record (1-1 relationship)
        @UniqueConstraint(name = "uk_payments_order_id", columnNames = { "order_id" }),

        // Transaction ID phải unique (tránh duplicate payment)
        @UniqueConstraint(name = "uk_payments_transaction_id", columnNames = { "transaction_id" })
})
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;
    @NotNull(message = "Số tiền thanh toán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Size(max = 100, message = "Transaction ID tối đa 100 ký tự")
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentMethod method;

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @NotNull(message = "Payment phải thuộc về một đơn hàng")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;

    public Payment() {
        this.paymentDate = VietnamTimeConfig.now();
        this.createdAt = VietnamTimeConfig.now();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(BigDecimal amount, PaymentMethod method) {
        this();
        this.amount = amount;
        this.method = method;
    }

    public Payment(Order order, BigDecimal amount, PaymentMethod method) {
        this();
        this.order = order;
        this.amount = amount;
        this.method = method;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    public enum PaymentMethod {
        COD, CREDIT_CARD, BANK_TRANSFER, SEPAY, MOMO, VNPAY
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = VietnamTimeConfig.now();
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = VietnamTimeConfig.now();
        this.updatedAt = VietnamTimeConfig.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = VietnamTimeConfig.now();
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}