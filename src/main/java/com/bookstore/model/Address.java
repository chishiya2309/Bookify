package com.bookstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "addresses", indexes = {
        // Lấy tất cả địa chỉ của 1 khách hàng
        @Index(name = "idx_addresses_customer_id", columnList = "customer_id"),
        // Tìm nhanh địa chỉ mặc định của khách
        @Index(name = "idx_addresses_default", columnList = "customer_id, is_default DESC"),
        // Tìm theo tỉnh / thành phố nếu có chức năng lọc
        @Index(name = "idx_addresses_province", columnList = "province")
})
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;
    @NotBlank(message = "Địa chỉ đường không được để trống")
    @Size(max = 255, message = "Địa chỉ đường tối đa 255 ký tự")
    @Column(name = "street_line", nullable = false, length = 255)
    private String street;
    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 255, message = "Phường/Xã tối đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 255, message = "Quận/Huyện tối đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String district;
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100, message = "Tỉnh/Thành phố tối đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String province;

    @NotBlank(message = "Mã bưu điện không được để trống")
    @Size(max = 20, message = "Mã bưu điện tối đa 20 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9 -]*$", message = "Mã bưu điện chỉ chứa chữ cái, số, dấu gạch ngang và khoảng trắng")
    @Column(name = "zip_code", length = 20)
    private String zipCode;
    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100, message = "Quốc gia tối đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String country = "Vietnam";

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Size(max = 100, message = "Tên người nhận tối đa 100 ký tự")
    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Số điện thoại không hợp lệ")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Địa chỉ phải thuộc về một khách hàng")
    private Customer customer;

    public Address() {
    }

    public Address(String street, String ward, String district, String province, String zipCode) {
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.zipCode = zipCode;
        this.country = "Vietnam";
    }

    // Getters and Setters
    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean validateAddress() {
        return street != null && !street.trim().isEmpty() &&
                ward != null && !ward.trim().isEmpty() &&
                district != null && !district.trim().isEmpty() &&
                province != null && !province.trim().isEmpty();
    }

    @Override
    public String toString() {
        return street + ", " + ward + ", " + district + ", " + province;
    }
}
