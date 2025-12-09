package com.bookstore.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "book_images")
public class BookImage implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;
    
    @Column(nullable = false, length = 500)
    private String url;
    
    @Column(length = 255)
    private String caption;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    // Constructors
    public BookImage() {}
    
    public BookImage(String url) {
        this.url = url;
    }
    
    public BookImage(String url, String caption, Boolean isPrimary, Integer sortOrder) {
        this.url = url;
        this.caption = caption;
        this.isPrimary = isPrimary;
        this.sortOrder = sortOrder;
    }

    public Integer getImageId() {
        return imageId;
    }
    
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    @Override
    public String toString() {
        return "BookImage{" +
                "imageId=" + imageId +
                ", url='" + url + '\'' +
                ", isPrimary=" + isPrimary +
                '}';
    }
}