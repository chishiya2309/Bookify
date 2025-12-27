/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.data;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBUtil {
    
    // Tên "bookify_pu" phải khớp chính xác với persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("bookify_pu");
    
    public static EntityManagerFactory getEmFactory() {
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}