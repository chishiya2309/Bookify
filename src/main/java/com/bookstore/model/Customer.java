package com.bookstore.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "register_date")
    private LocalDate registerDate;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShoppingCart shoppingCart;
    
    // Constructors
    public Customer() {
        super();
        this.registerDate = LocalDate.now();
    }
    
    public Customer(String email, String password, String fullName, String phoneNumber) {
        super(email, password, fullName);
        this.phoneNumber = phoneNumber;
        this.registerDate = LocalDate.now();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getRegisterDate() {
        return registerDate;
    }
    
    public void setRegisterDate(LocalDate registerDate) {
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
    
    public boolean login() {
        return true;
    }
    
    public void logout() {
        // Implementation
    }
    
    // Business methods
    public void register() {
        this.registerDate = LocalDate.now();
    }
    
    public void updateProfile() {
        // Update profile logic
    }
    
    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }
    
    public void removeAddress(Integer addressId) {
        addresses.removeIf(addr -> addr.getAddressId().equals(addressId));
    }
    
    public List<Book> searchBook(String keyword) {
        // Search book logic
        return new ArrayList<>();
    }
    
    public void addToCart(Book book, int quantity) {
        // Add to cart logic
    }
    
    public Order placeOrder() {
        // Place order logic
        return new Order();
    }
    
    public List<Order> viewOrderHistory() {
        return orders;
    }
    
    public void writeReview(Book book, int rating, String comment) {
        // Write review logic
    }
}