package com.bookstore.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "books",
        indexes = {
                @Index(name = "idx_books_title", columnList = "title"),
                @Index(name = "idx_books_price", columnList = "price"),
                @Index(name = "idx_books_category_price", columnList = "category_id, price"),
                @Index(name = "idx_books_publisher", columnList = "publisher_id"),
                @Index(name = "idx_books_last_updated", columnList = "last_updated DESC"),
                @Index(name = "idx_books_publish_date", columnList = "publish_date DESC"),
                @Index(name = "idx_books_in_stock", columnList = "quantity_in_stock"),
                @Index(name = "uk_books_isbn", columnList = "isbn", unique = true)
        }
)
public class Book implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 20, unique = true, nullable = false)
    private String isbn;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock = 0;
    
    @Column(name = "publish_date")
    private LocalDate publishDate;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDate lastUpdated;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
    
    @ManyToMany
    @JoinTable(
        name = "book_authors",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // Constructors
    public Book() {
    }
    
    public Book(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }
    
    // Getters and Setters
    public Integer getBookId() {
        return bookId;
    }
    
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getQuantityInStock() {
        return quantityInStock;
    }
    
    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
    
    public LocalDate getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
    
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Publisher getPublisher() {
        return publisher;
    }
    
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    
    public List<Author> getAuthors() {
        return authors;
    }
    
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
    
    public List<BookImage> getImages() {
        return images;
    }
    
    public void setImages(List<BookImage> images) {
        this.images = images;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    // Business methods
    public boolean isAvailable(int quantityRequested) {
        return quantityInStock >= quantityRequested;
    }
    
    public Double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    public int getTotalSold() {
        // Implementation would query OrderDetails
        return 0;
    }
    
    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }
    
    public void addImages(List<BookImage> newImages) {
        for (BookImage image : newImages) {
            image.setBook(this);
            images.add(image);
        }
    }
    
    @PrePersist
    public void prePersist() {
        this.lastUpdated = LocalDate.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", price=" + price +
                '}';
    }
}
