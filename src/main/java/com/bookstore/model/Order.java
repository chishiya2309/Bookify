package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(
        name = "orders",
        indexes = {
                // Lấy tất cả đơn hàng của 1 customer
                @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
                // Lọc đơn hàng theo trạng thái (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
                @Index(name = "idx_orders_status", columnList = "order_status"),
                // Lọc đơn theo trạng thái thanh toán (UNPAID, PAID, REFUNDED)
                @Index(name = "idx_orders_payment_status", columnList = "payment_status"),
                // Lọc đơn theo ngày đặt (báo cáo doanh thu theo thời gian)
                @Index(name = "idx_orders_date", columnList = "order_date DESC"),
                // Composite: Customer + Date (lịch sử đơn hàng của khách)
                @Index(name = "idx_orders_customer_date", columnList = "customer_id, order_date DESC"),
                // Composite: Status + Date (đơn hàng cần xử lý)
                @Index(name = "idx_orders_status_date", columnList = "order_status, order_date DESC"),
                // Composite: Payment Status + Date (theo dõi thanh toán)
                @Index(name = "idx_orders_payment_date", columnList = "payment_status, order_date DESC"),
                // Tổng tiền đơn hàng (phân tích, báo cáo)
                @Index(name = "idx_orders_total_amount", columnList = "total_amount DESC"),
                // Địa chỉ giao hàng (thống kê vùng địa lý)
                @Index(name = "idx_orders_shipping_address", columnList = "shipping_address_id"),
                // Phương thức thanh toán (phân tích)
                @Index(name = "idx_orders_payment_method", columnList = "payment_method")
        }
)
public class Order implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    
    @NotNull(message = "Ngày đặt hàng không được để trống")
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING) 
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50, nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;
    
    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng tiền phải lớn hơn 0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Size(max = 50, message = "Phương thức thanh toán tối đa 50 ký tự")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Size(max = 100, message = "Tên người nhận tối đa 100 ký tự")
    @Column(name = "recipient_name", length = 100)
    private String recipientName;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Đơn hàng phải có địa chỉ giao hàng")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;
    
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }
    
    public Order() {
        this.orderDate = LocalDateTime.now();
    }
    
    public Order(Customer customer, Address shippingAddress) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.orderDate = LocalDateTime.now();
    }
    
    public Integer getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Address getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", orderStatus='" + orderStatus + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}