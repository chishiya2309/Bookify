/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.service;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.math.BigDecimal;

public class OrderDetailServices {
//    public BigDecimal calcSubTotal() {
//        
//    }
    
    @PrePersist
    @PreUpdate
    public void calculateSubTotal() {
//        calcSubTotal();
    }
}
