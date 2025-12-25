package com.bookstore.service;

import com.bookstore.dao.OrderDAO;
import com.bookstore.model.*;
import com.bookstore.model.Order.OrderStatus;
import com.bookstore.model.Order.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OrderService - Business logic for Order management
 * Handles order creation from cart, status updates, and order queries
 */
public class OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    /**
     * Create an order from shopping cart
     * This is the main method for checkout process
     * 
     * @param customer        Customer placing the order
     * @param shippingAddress Address for delivery
     * @param cart            Shopping cart with items
     * @param paymentMethod   Payment method selected
     * @return Created Order with OrderDetails
     * @throws IllegalArgumentException if validation fails
     */
    public Order createOrderFromCart(
            Customer customer,
            Address shippingAddress,
            ShoppingCart cart,
            String paymentMethod) {

        // Validation
        validateOrderCreation(customer, shippingAddress, cart);

        try {
            // Create Order entity
            Order order = new Order();
            order.setCustomer(customer);
            order.setShippingAddress(shippingAddress);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setPaymentMethod(paymentMethod);
            order.setRecipientName(shippingAddress.getRecipientName() != null ? shippingAddress.getRecipientName()
                    : customer.getFullName());

            // Convert CartItems to OrderDetails
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem cartItem : cart.getItems()) {
                Book book = cartItem.getBook();
                Integer quantity = cartItem.getQuantity();
                BigDecimal unitPrice = book.getPrice();

                // Create OrderDetail
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBook(book);
                orderDetail.setQuantity(quantity);
                orderDetail.setUnitPrice(unitPrice);
                orderDetail.setSubTotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));

                // Add to order
                order.getOrderDetails().add(orderDetail);

                // Calculate total
                totalAmount = totalAmount.add(orderDetail.getSubTotal());
            }

            order.setTotalAmount(totalAmount);

            // Save order (cascade saves OrderDetails)
            orderDAO.save(order);

            LOGGER.log(Level.INFO, "Order created successfully from cart. OrderId: {0}, Total: {1}",
                    new Object[] { order.getOrderId(), totalAmount });

            return order;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating order from cart", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Validate order creation parameters
     */
    private void validateOrderCreation(Customer customer, Address shippingAddress, ShoppingCart cart) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer không được null");
        }

        if (shippingAddress == null) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được null");
        }

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        // Validate address belongs to customer
        if (!shippingAddress.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new IllegalArgumentException("Địa chỉ không thuộc về khách hàng này");
        }

        // Validate cart has valid items
        for (CartItem item : cart.getItems()) {
            if (item.getBook() == null) {
                throw new IllegalArgumentException("Cart item không có sách");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ");
            }
            if (item.getBook().getPrice() == null || item.getBook().getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Giá sản phẩm không hợp lệ");
            }
        }
    }

    /**
     * Get order by ID
     */
    public Order getOrderById(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }
        return orderDAO.findById(orderId);
    }

    /**
     * Get order by ID with details eagerly loaded
     */
    public Order getOrderByIdWithDetails(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }
        return orderDAO.findByIdWithDetails(orderId);
    }

    /**
     * Get all orders for a customer
     */
    public List<Order> getCustomerOrders(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.findByCustomerId(customerId);
    }

    /**
     * Get customer orders with details
     */
    public List<Order> getCustomerOrdersWithDetails(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.findByCustomerIdWithDetails(customerId);
    }

    /**
     * Update order status
     */
    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        if (orderId == null || newStatus == null) {
            throw new IllegalArgumentException("Order ID và status không được null");
        }

        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        order.setOrderStatus(newStatus);
        orderDAO.update(order);

        LOGGER.log(Level.INFO, "Order {0} status updated to {1}",
                new Object[] { orderId, newStatus });
    }

    /**
     * Update payment status
     */
    public void updatePaymentStatus(Integer orderId, PaymentStatus newStatus) {
        if (orderId == null || newStatus == null) {
            throw new IllegalArgumentException("Order ID và payment status không được null");
        }

        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        order.setPaymentStatus(newStatus);
        orderDAO.update(order);

        LOGGER.log(Level.INFO, "Order {0} payment status updated to {1}",
                new Object[] { orderId, newStatus });
    }

    /**
     * Cancel an order
     */
    public void cancelOrder(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }

        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        // Only allow cancellation if order is PENDING or PROCESSING
        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã giao hoặc đang giao");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderDAO.update(order);

        LOGGER.log(Level.INFO, "Order {0} cancelled", orderId);
    }

    /**
     * Get customer order count
     */
    public long getCustomerOrderCount(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.countByCustomerId(customerId);
    }

    /**
     * Get total amount customer has spent (only paid orders)
     */
    public BigDecimal getCustomerTotalSpent(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.getTotalRevenueByCustomer(customerId);
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status không được null");
        }
        return orderDAO.findByStatus(status);
    }

    /**
     * Get orders by payment status
     */
    public List<Order> getOrdersByPaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new IllegalArgumentException("Payment status không được null");
        }
        return orderDAO.findByPaymentStatus(paymentStatus);
    }

    /**
     * Get all orders (admin only)
     */
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }
}
