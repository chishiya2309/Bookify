package com.bookstore.service;

import com.bookstore.dao.AdminHomePageDAO;
import com.bookstore.model.Order;
import com.bookstore.model.Review;

import java.util.List;

public class AdminServices {
    
    public long countAllBooks() {
        return AdminHomePageDAO.countAllBooks();
    }

    public long countAllUsers() {
        return AdminHomePageDAO.countAllUsers();
    }

    public long countAllCustomers() {
        return AdminHomePageDAO.countAllCustomers();
    }

    public long countAllReviews() {
        return AdminHomePageDAO.countAllReviews();
    }

    public long countAllOrders() {
        return AdminHomePageDAO.countAllOrders();
    }

    public List<Order> findRecentSales() {
        return AdminHomePageDAO.findRecentSales();
    }

    public List<Review> findRecentReviews() {
        return AdminHomePageDAO.findRecentReviews();
    }
}