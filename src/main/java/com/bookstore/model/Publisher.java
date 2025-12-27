package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "publishers",
        indexes = {
                // Tìm kiếm publisher theo tên (search, filter, autocomplete)
                @Index(name = "idx_publishers_name", columnList = "name"),

                // Tìm publisher theo email (liên hệ, tra cứu)
                @Index(name = "idx_publishers_email", columnList = "contact_email"),
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_publishers_name",
                        columnNames = {"name"}
                ),
                @UniqueConstraint(
                        name = "uk_publishers_email",
                        columnNames = {"contact_email"}
                )
        }
)
public class Publisher implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id")
    private Integer publisherId;
    
    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    @Size(max = 100, message = "Tên nhà xuất bản tôi đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    @Column(length = 255)
    private String address;
    
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Column(name = "contact_email", unique = true, length = 100)
    private String contactEmail;
    
    @Size(max = 255, message = "Website tối đa 255 ký tự")
    @Pattern(
            regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(/.*)?$",
            message = "Website không hợp lệ (VD: example.com, https://www.example.com)"
    )
    @Column(length = 255)
    private String website;
    
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
    
    // Constructors
    public Publisher() {}
    
    public Publisher(String name) {
        this.name = name;
    }
    
    public Publisher(String name, String address, String contactEmail, String website) {
        this.name = name;
        this.address = address;
        this.contactEmail = contactEmail;
        this.website = website;
    }
    
    // Getters and Setters
    public Integer getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public List<Book> getBooks() {
        return books;
    }
    
    public void setBooks(List<Book> books) {
        this.books = books;
    }
    
    @Override
    public String toString() {
        return "Publisher{" +
                "publisherId=" + publisherId +
                ", name='" + name + '\'' +
                '}';
    }
}