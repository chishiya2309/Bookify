package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(
        name = "order_details",
        indexes = {
                // Lấy tất cả chi tiết của 1 đơn hàng (query phổ biến nhất)
                @Index(name = "idx_order_details_order_id", columnList = "order_id"),

                // Tìm sách nào đã được bán (thống kê sản phẩm)
                @Index(name = "idx_order_details_book_id", columnList = "book_id"),

                // Composite: Order + Book (tối ưu join queries)
                @Index(name = "idx_order_details_order_book", columnList = "order_id, book_id"),

                // Thống kê theo giá (phân tích doanh thu)
                @Index(name = "idx_order_details_unit_price", columnList = "unit_price DESC"),

                // Thống kê theo subtotal (phân tích đơn giá trị cao)
                @Index(name = "idx_order_details_subtotal", columnList = "sub_total DESC")
        },
        uniqueConstraints = {
                // Mỗi sách chỉ xuất hiện 1 lần trong 1 đơn hàng
                @UniqueConstraint(
                        name = "uk_order_book",
                        columnNames = {"order_id", "book_id"}
                )
        }
)
public class OrderDetail implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải lớn hơn 0")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @NotNull(message = "Order detail phải thuộc về một đơn hàng")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull(message = "Order detail phải có sách")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public OrderDetail() {}

    public OrderDetail(Order order, Book book, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = calcSubTotal();
    }

    public BigDecimal calcSubTotal() {
        if (unitPrice == null || quantity == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public Integer getOrderDetailId() {
        return orderDetailId;
    }
    
    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.subTotal = calcSubTotal();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        this.subTotal = calcSubTotal();
    }
    
    @PrePersist
    @PreUpdate
    public void updateSubTotal() {
        this.subTotal = calcSubTotal();
    }

    
    public BigDecimal getSubTotal() {
        return subTotal;
    }
    
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }

    
    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subTotal=" + subTotal +
                '}';
    }
}