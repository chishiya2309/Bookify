package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.config.ShippingConfig;
import com.bookstore.data.DBUtil;
import com.bookstore.model.*;
import com.bookstore.model.Order.OrderStatus;
import com.bookstore.model.Order.PaymentStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final EmailService emailService;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.emailService = new EmailService();
    }

    /**
     * Create an order from shopping cart
     * This is the main method for checkout process
     * 
     * Uses PESSIMISTIC_WRITE lock to prevent race conditions when multiple
     * users checkout the same items simultaneously. All stock validation and
     * deduction happens within a single transaction.
     * 
     * @param customer        Customer placing the order
     * @param shippingAddress Address for delivery
     * @param cart            Shopping cart with items
     * @param paymentMethod   Payment method selected
     * @return Created Order with OrderDetails
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if insufficient stock
     */
    public Order createOrderFromCart(
            Customer customer,
            Address shippingAddress,
            ShoppingCart cart,
            String paymentMethod) {

        // Basic validation (before starting transaction)
        validateOrderCreation(customer, shippingAddress, cart);

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Order order = null;

        try {
            tx.begin();

            // Create Order entity
            order = new Order();
            order.setCustomer(customer);
            order.setShippingAddress(shippingAddress);
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setPaymentMethod(paymentMethod);
            order.setRecipientName(shippingAddress.getRecipientName() != null
                    ? shippingAddress.getRecipientName()
                    : customer.getFullName());

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<String> stockErrors = new ArrayList<>();

            // Process each cart item with pessimistic locking
            for (CartItem cartItem : cart.getItems()) {
                Integer bookId = cartItem.getBook().getBookId();
                Integer quantity = cartItem.getQuantity();

                // Lock the book row for update - prevents other transactions from
                // reading/modifying
                Book lockedBook = em.find(Book.class, bookId, LockModeType.PESSIMISTIC_WRITE);

                if (lockedBook == null) {
                    stockErrors.add("Sản phẩm \"" + cartItem.getBook().getTitle() + "\" không tồn tại");
                    continue;
                }

                // Validate stock
                if (lockedBook.getQuantityInStock() < quantity) {
                    stockErrors.add("Sản phẩm \"" + lockedBook.getTitle() + "\" chỉ còn "
                            + lockedBook.getQuantityInStock() + " sản phẩm (yêu cầu: " + quantity + ")");
                    continue;
                }

                // Deduct stock immediately (within same transaction)
                int newStock = lockedBook.getQuantityInStock() - quantity;
                lockedBook.setQuantityInStock(newStock);
                // No need to call persist/merge - entity is already managed

                LOGGER.log(Level.INFO, "Deducted {0} units from book {1}. New stock: {2}",
                        new Object[] { quantity, bookId, newStock });

                // Create OrderDetail
                BigDecimal unitPrice = lockedBook.getPrice();
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBook(lockedBook);
                orderDetail.setQuantity(quantity);
                orderDetail.setUnitPrice(unitPrice);
                orderDetail.setSubTotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));

                order.getOrderDetails().add(orderDetail);
                totalAmount = totalAmount.add(orderDetail.getSubTotal());
            }

            // If any stock errors, rollback and throw exception
            if (!stockErrors.isEmpty()) {
                tx.rollback();
                throw new IllegalStateException("Lỗi kiểm tra tồn kho:\n• " + String.join("\n• ", stockErrors));
            }

            // Calculate shipping fee based on province
            BigDecimal subtotal = totalAmount;
            BigDecimal shippingFee = ShippingConfig.calculateShippingFee(
                    shippingAddress.getProvince(), subtotal);
            BigDecimal grandTotal = subtotal.add(shippingFee);

            order.setSubtotal(subtotal);
            order.setShippingFee(shippingFee);
            order.setTotalAmount(grandTotal);

            LOGGER.log(Level.INFO, "Order pricing - Subtotal: {0}, Shipping: {1}, Total: {2}",
                    new Object[] { subtotal, shippingFee, grandTotal });

            // Persist order (cascade saves OrderDetails)
            em.persist(order);

            // Commit transaction - this releases all locks
            tx.commit();

            LOGGER.log(Level.INFO, "Order created successfully with locking. OrderId: {0}, Total: {1}",
                    new Object[] { order.getOrderId(), grandTotal });

            // Send order confirmation email (outside transaction)
            try {
                emailService.sendOrderConfirmation(order);
                LOGGER.log(Level.INFO, "Order confirmation email sent for order: {0}", order.getOrderId());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to send order confirmation email for order: " + order.getOrderId(),
                        e);
                // Don't fail the order creation if email fails
            }

            return order;

        } catch (IllegalStateException e) {
            // Re-throw stock errors as-is
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error creating order from cart", e);
            throw new RuntimeException("Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage(), e);
        } finally {
            em.close();
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
     * Cancel an order (COD orders only, PENDING or PROCESSING status)
     * Restores stock and sends cancellation email
     * 
     * @param orderId Order ID to cancel
     * @param reason  Cancellation reason
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException    if order cannot be cancelled
     */
    public void cancelOrder(Integer orderId, String reason) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }

        // Load order with details for stock restoration
        Order order = orderDAO.findByIdWithDetails(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        // Only allow cancellation for COD payment method
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalStateException(
                    "Chỉ có thể huỷ đơn hàng thanh toán khi nhận hàng (COD). " +
                            "Đơn hàng đã thanh toán online vui lòng liên hệ hotline để được hỗ trợ hoàn tiền.");
        }

        // Only allow cancellation if order is PENDING or PROCESSING
        if (order.getOrderStatus() != OrderStatus.PENDING &&
                order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Chỉ có thể huỷ đơn hàng ở trạng thái Chờ xác nhận hoặc Đang xử lý. " +
                            "Trạng thái hiện tại: " + order.getOrderStatus());
        }

        // Restore stock for each order item
        restoreStock(order);

        // Update order status to CANCELLED
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderDAO.update(order);

        LOGGER.log(Level.INFO, "Order {0} cancelled. Reason: {1}", new Object[] { orderId, reason });

        // Send cancellation email
        try {
            emailService.sendOrderCancellation(order, reason);
            LOGGER.log(Level.INFO, "Cancellation email sent for order: {0}", orderId);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send cancellation email for order: " + orderId, e);
            // Don't fail the cancellation if email fails
        }
    }

    /**
     * Restore stock quantities when order is cancelled
     */
    private void restoreStock(Order order) {
        BookDAO bookDAO = new BookDAO();

        for (OrderDetail detail : order.getOrderDetails()) {
            Book book = detail.getBook();
            int restoredQty = detail.getQuantity();

            // Reload book to get current stock
            Book currentBook = bookDAO.findById(book.getBookId());
            if (currentBook != null) {
                int newStock = currentBook.getQuantityInStock() + restoredQty;
                currentBook.setQuantityInStock(newStock);
                BookDAO.updateBook(currentBook); // Static method call

                LOGGER.log(Level.INFO, "Restored {0} units of book {1}. New stock: {2}",
                        new Object[] { restoredQty, book.getBookId(), newStock });
            }
        }
    }

    /**
     * Check if an order can be cancelled by customer
     * 
     * @param order Order to check
     * @return true if can be cancelled, false otherwise
     */
    public boolean canCancelOrder(Order order) {
        if (order == null) {
            return false;
        }

        // Only COD orders can be cancelled by customer
        if (!"COD".equalsIgnoreCase(order.getPaymentMethod())) {
            return false;
        }

        // Only PENDING or PROCESSING orders can be cancelled
        return order.getOrderStatus() == OrderStatus.PENDING ||
                order.getOrderStatus() == OrderStatus.PROCESSING;
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
