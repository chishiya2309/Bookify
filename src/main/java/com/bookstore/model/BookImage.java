package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "book_images", indexes = {
        @Index(name = "idx_book_images_order", columnList = "book_id, sort_order")
})
public class BookImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @NotBlank(message = "URL ảnh không được để trống")
    @Size(max = 500, message = "URL ảnh tối đa 500 ký tự")
    @Pattern(regexp = "^$|^(?i)https?://.+", message = "URL ảnh không hợp lệ (phải bắt đầu bằng http:// hoặc https://)", flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(nullable = false, length = 500)
    private String url;

    @Size(max = 255, message = "Chú thích ảnh tối đa 255 ký tự")
    @Column(length = 255)
    private String caption;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Min(value = 0, message = "Thứ tự sắp xếp không được âm")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @NotNull(message = "Ảnh phải thuộc về một cuốn sách")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Constructors
    public BookImage() {
    }

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
