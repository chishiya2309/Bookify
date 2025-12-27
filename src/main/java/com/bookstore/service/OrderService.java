package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.dao.VoucherDAO;
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
     * Tạo một đơn hàng từ giỏ hàng
     * Đây là phương thức chính cho quá trình thanh toán
     * 
     * Sử dụng khóa PESSIMISTIC_WRITE để ngăn chặn các xung đột khi nhiều người
     * cùng mua cùng một sản phẩm. Tất cả các kiểm tra tồn kho và trừ hàng
     * xảy ra trong một giao dịch duy nhất.
     * 
     * @param customer        Khách hàng đặt hàng
     * @param shippingAddress Địa chỉ giao hàng
     * @param cart            Giỏ hàng chứa các sản phẩm
     * @param paymentMethod   Phương thức thanh toán
     * @return Đơn hàng đã tạo với OrderDetails
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws IllegalStateException    nếu không đủ hàng
     */
    public Order createOrderFromCart(
            Customer customer,
            Address shippingAddress,
            ShoppingCart cart,
            String paymentMethod) {
        return createOrderFromCart(customer, shippingAddress, cart, paymentMethod, null);
    }

    /**
     * Tạo một đơn hàng từ giỏ hàng với mã giảm giá tùy chọn
     * 
     * Sử dụng khóa PESSIMISTIC_WRITE để ngăn chặn các xung đột khi nhiều người
     * cùng mua cùng một sản phẩm. Tất cả các kiểm tra tồn kho và trừ hàng
     * xảy ra trong một giao dịch duy nhất.
     * 
     * @param customer        Khách hàng đặt hàng
     * @param shippingAddress Địa chỉ giao hàng
     * @param cart            Giỏ hàng chứa các sản phẩm
     * @param paymentMethod   Phương thức thanh toán
     * @param voucherCode     Mã giảm giá tùy chọn
     * @return Đơn hàng đã tạo với OrderDetails
     * @throws IllegalArgumentException nếu thông tin không hợp lệ
     * @throws IllegalStateException    nếu không đủ hàng
     */
    public Order createOrderFromCart(
            Customer customer,
            Address shippingAddress,
            ShoppingCart cart,
            String paymentMethod,
            String voucherCode) {

        // Kiểm tra thông tin đơn hàng (trước khi bắt đầu giao dịch)
        validateOrderCreation(customer, shippingAddress, cart);

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Order order = null;

        try {
            tx.begin();

            // Tạo đơn hàng
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

            // Xử lý mỗi mục trong giỏ hàng với khóa PESSIMISTIC_WRITE
            for (CartItem cartItem : cart.getItems()) {
                Integer bookId = cartItem.getBook().getBookId();
                Integer quantity = cartItem.getQuantity();

                // khóa hàng để cập nhật - ngăn chặn các giao dịch khác đọc/sửa đổi
                Book lockedBook = em.find(Book.class, bookId, LockModeType.PESSIMISTIC_WRITE);

                if (lockedBook == null) {
                    stockErrors.add("Sản phẩm \"" + cartItem.getBook().getTitle() + "\" không tồn tại");
                    continue;
                }

                // Kiểm tra tồn kho
                if (lockedBook.getQuantityInStock() < quantity) {
                    stockErrors.add("Sản phẩm \"" + lockedBook.getTitle() + "\" chỉ còn "
                            + lockedBook.getQuantityInStock() + " sản phẩm (yêu cầu: " + quantity + ")");
                    continue;
                }

                // Trừ hàng ngay lập tức (trong cùng một giao dịch)
                int newStock = lockedBook.getQuantityInStock() - quantity;
                lockedBook.setQuantityInStock(newStock);
                // Không cần gọi persist/merge - entity đã được quản lý

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

                order.addOrderDetail(orderDetail);
                totalAmount = totalAmount.add(orderDetail.getSubTotal());
            }

            if (!stockErrors.isEmpty()) {
                tx.rollback();
                throw new IllegalStateException("Lỗi kiểm tra tồn kho:\n• " + String.join("\n• ", stockErrors));
            }

            // Tính phí vận chuyển dựa trên tỉnh thành
            BigDecimal subtotal = totalAmount;
            BigDecimal shippingFee = ShippingConfig.calculateShippingFee(
                    shippingAddress.getProvince(), subtotal);

            // Áp dụng voucher nếu có
            BigDecimal voucherDiscount = BigDecimal.ZERO;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                VoucherDAO voucherDAO = new VoucherDAO();
                VoucherService voucherService = new VoucherService();

                VoucherService.ValidationResult vResult = voucherService.validateVoucher(
                        voucherCode, subtotal, shippingFee, customer);

                if (vResult.isValid()) {
                    Voucher voucher = vResult.getVoucher();
                    voucherDiscount = vResult.getDiscount();

                    // Với FREE_SHIPPING: giữ nguyên shippingFee gốc để hiển thị
                    // Đặt voucherDiscount = shippingFee để tính tổng đúng
                    if (voucher.getDiscountType() == Voucher.DiscountType.FREE_SHIPPING) {
                        voucherDiscount = shippingFee; // Giảm giá = phí vận chuyển
                        // KHÔNG đặt shippingFee = 0 - giữ để hiển thị
                    }

                    order.setVoucherId(voucher.getVoucherId());
                    order.setVoucherCode(voucher.getCode());
                    order.setVoucherDiscount(voucherDiscount);

                    // Tăng số lần sử dụng
                    voucherDAO.incrementUsage(voucher.getVoucherId());

                    LOGGER.log(Level.INFO, "Voucher {0} applied. Discount: {1}",
                            new Object[] { voucherCode, voucherDiscount });
                } else {
                    LOGGER.log(Level.WARNING, "Voucher {0} validation failed: {1}",
                            new Object[] { voucherCode, vResult.getMessage() });
                }
            }

            // Tổng = tạm tính + phí ship - giảm giá
            // Với FREE_SHIPPING: tạm tính + 15000 - 15000 = tạm tính ✓
            BigDecimal grandTotal = subtotal.add(shippingFee).subtract(voucherDiscount);

            order.setSubtotal(subtotal);
            order.setShippingFee(shippingFee); // Phí ship gốc để hiển thị
            order.setTotalAmount(grandTotal);

            LOGGER.log(Level.INFO, "Order pricing - Subtotal: {0}, Shipping: {1}, Voucher: -{2}, Total: {3}",
                    new Object[] { subtotal, shippingFee, order.getVoucherDiscount(), grandTotal });

            // Lưu đơn hàng (cascade saves OrderDetails)
            em.persist(order);

            // Commit giao dịch - giải phóng tất cả locks
            tx.commit();

            // Ghi lại việc sử dụng voucher sau khi commit (đơn hàng phải tồn tại trong DB
            // để tạo khóa ngoại)
            if (order.getVoucherId() != null) {
                VoucherDAO voucherDAO2 = new VoucherDAO();
                Voucher voucher = voucherDAO2.findById(order.getVoucherId());
                if (voucher != null) {
                    VoucherUsage usage = new VoucherUsage(voucher, customer, order, order.getVoucherDiscount());
                    voucherDAO2.recordUsage(usage);
                    LOGGER.log(Level.INFO, "Recorded voucher usage for order {0}, discount: {1}",
                            new Object[] { order.getOrderId(), order.getVoucherDiscount() });
                }
            }

            LOGGER.log(Level.INFO, "Order created successfully with locking. OrderId: {0}, Total: {1}",
                    new Object[] { order.getOrderId(), grandTotal });

            // Gửi email xác nhận đơn hàng (ngoài giao dịch)
            try {
                emailService.sendOrderConfirmation(order);
                LOGGER.log(Level.INFO, "Order confirmation email sent for order: {0}", order.getOrderId());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to send order confirmation email for order: " + order.getOrderId(),
                        e);
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

        if (!shippingAddress.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new IllegalArgumentException("Địa chỉ không thuộc về khách hàng này");
        }

        // Kiểm tra giỏ hàng có sản phẩm hợp lệ
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

    public Order getOrderById(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }
        return orderDAO.findById(orderId);
    }

    public Order getOrderByIdWithDetails(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID không được null");
        }
        return orderDAO.findByIdWithDetails(orderId);
    }

    public List<Order> getCustomerOrders(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.findByCustomerId(customerId);
    }

    public List<Order> getCustomerOrdersWithDetails(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.findByCustomerIdWithDetails(customerId);
    }

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
        }
    }

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

    public long getCustomerOrderCount(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.countByCustomerId(customerId);
    }

    public BigDecimal getCustomerTotalSpent(Integer customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        return orderDAO.getTotalRevenueByCustomer(customerId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status không được null");
        }
        return orderDAO.findByStatus(status);
    }

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
