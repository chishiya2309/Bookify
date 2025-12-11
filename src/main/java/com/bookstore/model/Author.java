package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "authors",
        indexes = {
                // Tìm kiếm tác giả theo tên 
                @Index(name = "idx_authors_name", columnList = "name"),
        }
)
public class Author implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Integer authorId;
    
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả tối đa 100 ký tự")
    @Pattern(
            regexp = "^[\\p{L}\\s'\\-\\.]+$",
            message = "Tên tác giả chỉ được chứa chữ cái, khoảng trắng, dấu gạch ngang, nháy đơn và chấm"
    )
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 10000, message = "Tiểu sử tối đa 10.000 ký tự")
    @Column(columnDefinition = "TEXT")
    private String biography;
    
    @Size(max = 500, message = "URL ảnh tối đa 500 ký tự")
    @Pattern(
            regexp = "^$|^(?i)https?://.+",
            message = "URL ảnh không hợp lệ (phải bắt đầu bằng http:// hoặc https://)"
    )
    @Column(name = "photo_url", length = 500)
    private String photoUrl;
    
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
    
    public Author() {}
    
    public Author(String name) {
        this.name = name;
    }
    
    public Author(String name, String biography, String photoUrl) {
        this.name = name;
        this.biography = biography;
        this.photoUrl = photoUrl;
    }
    
    public Integer getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    public List<Book> getBooks() {
        return books;
    }
    
    public void setBooks(List<Book> books) {
        this.books = books;
    }
    
    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", name='" + name + '\'' +
                '}';
    }
}
