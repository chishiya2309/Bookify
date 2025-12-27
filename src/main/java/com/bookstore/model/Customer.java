package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "customers",
        indexes = {
                // Tìm customer theo số điện thoại (hỗ trợ, tra cứu đơn hàng)
                @Index(name = "idx_customers_phone", columnList = "phone_number"),

                // Lọc customer theo ngày đăng ký (analytics, marketing campaigns)
                @Index(name = "idx_customers_register_date", columnList = "register_date DESC"),

                // Tìm customer theo user_id (kế thừa từ User)
                @Index(name = "idx_customers_user_id", columnList = "user_id")
        }
)
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
        regexp = "^(\\+84|0)[0-9]{9}$",
        message = "Số điện thoại không hợp lệ. Vui lòng sử dụng 10 số (VD: 0912345678)"
    )
    @Size(min = 10, max = 20, message = "Độ dài số điện thoại phải từ 10 đến 20 ký tự")
    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;
    
    @Column(name = "register_date", nullable = false)
    private LocalDateTime registerDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ShoppingCart shoppingCart;
    
    // Constructors
    public Customer() {
        super();
        this.registerDate = LocalDateTime.now();
    }
    
    public Customer(String email, String password, String fullName, String phoneNumber) {
        super(email, password, fullName);
        this.phoneNumber = phoneNumber;
        this.registerDate = LocalDateTime.now();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDateTime getRegisterDate() {
        return registerDate;
    }
    
    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }
    
    public List<Address> getAddresses() {
        return addresses;
    }
    
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }
    
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}