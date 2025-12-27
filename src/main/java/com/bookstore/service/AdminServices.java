package com.bookstore.service;

import com.bookstore.dao.AdminHomePageDAO;
import com.bookstore.dao.AdminOrderDAO;
import com.bookstore.model.Address;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.model.Review;
import jakarta.servlet.http.HttpServletRequest;

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
    public List<Order> listAllOrders() {
        List<Order> orders = AdminOrderDAO.findAllOrders();
        return orders;
    }


    public Order getOrder(int id) {
        return AdminOrderDAO.findById(id);
    }

    public void deleteOrder(int id) {
        Order o = AdminOrderDAO.findById(id);
        if (o != null) {
            AdminOrderDAO.delete(o);
        }
    }
    public Order getOrderForEdit(int id) {
    return AdminOrderDAO.findByIdWithDetails(id);
}

public void updateOrder(
        int orderId,
        String recipientName,
        String paymentMethod,
        Order.OrderStatus status,
        Address address
) {
    AdminOrderDAO.updateOrder(orderId, recipientName, paymentMethod, status, address);
}

public void updateOrderDetailQty(int detailId, int qty) {
    AdminOrderDAO.updateOrderDetailQty(detailId, qty);
}

public void removeOrderDetail(int detailId) {
    AdminOrderDAO.removeOrderDetail(detailId);
}

    

}