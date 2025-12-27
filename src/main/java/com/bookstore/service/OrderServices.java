package com.bookstore.service;

import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;

import java.util.List;

public class OrderServices {

    private final OrderDAO orderDAO;

    public OrderServices() {
        this.orderDAO = new OrderDAO();
    }

    /* =========================
       CUSTOMER SIDE
       ========================= */

    // My Order History
    public List<Order> getOrderHistory(Customer customer) {
        return orderDAO.findOrdersByCustomer(customer);
    }

    // Order Detail (Customer)
    public Order getOrderDetail(Integer orderId, Customer customer) {
        return orderDAO.findOrderDetail(orderId, customer);
    }

    // Tổng số lượng sách trong đơn
    public int calculateTotalQuantity(Order order) {
        if (order == null || order.getOrderDetails() == null) return 0;

        return order.getOrderDetails()
                .stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }

    /* =========================
       ADMIN SIDE
       ========================= */

    // Lấy tất cả đơn hàng
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    // Lấy 1 đơn theo ID
    public Order getOrderById(Integer orderId) {
        return orderDAO.findById(orderId);
    }

    // Cập nhật trạng thái đơn
    public void updateOrderStatus(Integer orderId, Order.OrderStatus newStatus) {
        Order order = orderDAO.findById(orderId);
        if (order == null) return;

        order.setOrderStatus(newStatus);
        orderDAO.update(order);
    }

    // Hủy đơn
    public boolean cancelOrder(Integer orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) return false;

        // ❗ Không cho hủy nếu đã hoàn tất
        if (order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            return false;
        }

        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderDAO.update(order);
        return true;
    }
}
