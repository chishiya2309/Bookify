package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        indexes = {
                // Lấy tất cả review của 1 sách 
                @Index(name = "idx_reviews_book_id", columnList = "book_id"),

                // Lấy tất cả review của 1 customer
                @Index(name = "idx_reviews_customer_id", columnList = "customer_id"),

                // Composite: Book + Date (hiển thị review mới nhất của sách)
                @Index(name = "idx_reviews_book_date", columnList = "book_id, review_date DESC"),

                // Composite: Book + Rating (lọc review theo rating)
                @Index(name = "idx_reviews_book_rating", columnList = "book_id, rating DESC"),

                // Composite: Customer + Date (lịch sử review của khách)
                @Index(name = "idx_reviews_customer_date", columnList = "customer_id, review_date DESC"),
                
                @Index(name = "idx_reviews_verified", columnList = "is_verified")
        },
        uniqueConstraints = {
                // Mỗi khách chỉ review 1 sách 1 lần duy nhất
                @UniqueConstraint(
                        name = "uk_review_customer_book",
                        columnNames = {"customer_id", "book_id"}
                )
        }
)
public class Review implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    
    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating tối thiểu là 1 sao")
    @Max(value = 5, message = "Rating tối đa là 5 sao")
    @Column(nullable = false)
    private Integer rating;
    
    @Size(max = 255, message = "Tiêu đề đánh giá tối đa 255 ký tự")
    @Column(length = 255)
    private String headline;
    
    @Size(max = 2000, message = "Nội dung đánh giá tối đa 2000 ký tự")
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    // Constructors
    public Review() {
        this.reviewDate = LocalDateTime.now();
    }
    
    public Review(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    public Review(Integer rating, String headline, String comment) {
        this.rating = rating;
        this.headline = headline;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getHeadline() {
        return headline;
    }
    
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    @PrePersist
    public void onCreate() {
        this.reviewDate = LocalDateTime.now();
        if (this.isVerified == null) this.isVerified = false;
    }
    
    public Boolean getVerified() { return isVerified; }
    public void setVerified(Boolean verified) { isVerified = verified; }
    
    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", rating=" + rating +
                ", headline='" + headline + '\'' +
                ", reviewDate=" + reviewDate +
                '}';
    }
}