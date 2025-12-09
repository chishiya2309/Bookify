package com.bookstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    public Admin() {
        super();
    }
    
    public Admin(String email, String password, String fullName) {
        super(email, password, fullName);
    }
}