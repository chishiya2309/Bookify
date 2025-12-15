package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.model.Customer;
import com.bookstore.model.Admin;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Constructor for manual EntityManager injection
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    // Find user by email (works for both Customer and Admin)
    public Optional<User> findByEmail(String email) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class
            );
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    // Find customer by email
    public Optional<Customer> findCustomerByEmail(String email) {
        try {
            TypedQuery<Customer> query = entityManager.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class
            );
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    // Find admin by email
    public Optional<Admin> findAdminByEmail(String email) {
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                "SELECT a FROM Admin a WHERE a.email = :email", Admin.class
            );
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    // Check if email exists
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class
        )
        .setParameter("email", email)
        .getSingleResult();
        return count > 0;
    }
    
    // Check if phone exists (for customers)
    public boolean existsByPhoneNumber(String phoneNumber) {
        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM Customer c WHERE c.phoneNumber = :phone", Long.class
        )
        .setParameter("phone", phoneNumber)
        .getSingleResult();
        return count > 0;
    }
    
    // Save customer
    public Customer saveCustomer(Customer customer) {
        if (customer.getUserId() == null) {
            entityManager.persist(customer);
            return customer;
        } else {
            return entityManager.merge(customer);
        }
    }
    
    // Save admin
    public Admin saveAdmin(Admin admin) {
        if (admin.getUserId() == null) {
            entityManager.persist(admin);
            return admin;
        } else {
            return entityManager.merge(admin);
        }
    }
    
    // Find user by ID
    public Optional<User> findById(Integer userId) {
        User user = entityManager.find(User.class, userId);
        return Optional.ofNullable(user);
    }
    
    // Update user
    public User update(User user) {
        return entityManager.merge(user);
    }
    
    // Delete user
    public void delete(Integer userId) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}