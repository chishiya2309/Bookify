package com.bookstore.scheduler;

import com.bookstore.config.SepayConfig;
import com.bookstore.dao.BookDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.model.OrderDetail;
import com.bookstore.service.EmailService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bookstore.config.VietnamTimeConfig;

/**
 * OrderTimeoutScheduler - Automatically cancels expired pending orders
 * 
 * Runs as a background scheduled task that:
 * 1. Finds BANK_TRANSFER orders with UNPAID status older than timeout
 * 2. Cancels them and restores stock
 * 3. Sends cancellation email to customer
 * 
 * Schedule: Runs every 5 minutes
 * Timeout: Configurable via SepayConfig.PAYMENT_TIMEOUT_MINUTES (default: 15
 * minutes)
 */
@WebListener
public class OrderTimeoutScheduler implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(OrderTimeoutScheduler.class.getName());

    // How often to check for expired orders (in minutes)
    private static final int CHECK_INTERVAL_MINUTES = 5;

    private ScheduledExecutorService scheduler;
    private OrderDAO orderDAO;
    private EmailService emailService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "OrderTimeoutScheduler starting...");

        orderDAO = new OrderDAO();
        emailService = new EmailService();

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OrderTimeoutScheduler");
            t.setDaemon(true); // Daemon thread - won't prevent JVM shutdown
            return t;
        });

        // Run first check after 1 minute, then every CHECK_INTERVAL_MINUTES
        scheduler.scheduleAtFixedRate(
                this::cancelExpiredOrders,
                1, // initial delay
                CHECK_INTERVAL_MINUTES,
                TimeUnit.MINUTES);

        LOGGER.log(Level.INFO, "OrderTimeoutScheduler started. Checking every {0} minutes, timeout: {1} minutes",
                new Object[] { CHECK_INTERVAL_MINUTES, SepayConfig.PAYMENT_TIMEOUT_MINUTES });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "OrderTimeoutScheduler shutting down...");

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                // Wait up to 10 seconds for tasks to complete
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        LOGGER.log(Level.INFO, "OrderTimeoutScheduler stopped");
    }

    /**
     * Find and cancel expired pending orders
     */
    private void cancelExpiredOrders() {
        try {
            LOGGER.log(Level.FINE, "Checking for expired pending orders...");

            // Calculate cutoff time using Vietnam timezone
            LocalDateTime cutoffTime = VietnamTimeConfig.now()
                    .minusMinutes(SepayConfig.PAYMENT_TIMEOUT_MINUTES);

            // Find expired BANK_TRANSFER orders
            List<Order> expiredOrders = orderDAO.findExpiredPendingOrders("BANK_TRANSFER", cutoffTime);

            if (expiredOrders.isEmpty()) {
                LOGGER.log(Level.FINE, "No expired orders found");
                return;
            }

            LOGGER.log(Level.INFO, "Found {0} expired orders to cancel", expiredOrders.size());

            for (Order order : expiredOrders) {
                try {
                    cancelExpiredOrder(order);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error cancelling expired order: " + order.getOrderId(), e);
                    // Continue with other orders
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in cancelExpiredOrders task", e);
        }
    }

    /**
     * Cancel a single expired order
     * - Update status to CANCELLED
     * - Restore stock
     * - Send cancellation email
     */
    private void cancelExpiredOrder(Order order) {
        LOGGER.log(Level.INFO, "Cancelling expired order: {0}, created at: {1}",
                new Object[] { order.getOrderId(), order.getOrderDate() });

        // Restore stock for each item
        BookDAO bookDAO = new BookDAO();
        for (OrderDetail detail : order.getOrderDetails()) {
            Book book = detail.getBook();
            int restoredQty = detail.getQuantity();

            // Reload book to get current stock
            Book currentBook = bookDAO.findById(book.getBookId());
            if (currentBook != null) {
                int newStock = currentBook.getQuantityInStock() + restoredQty;
                currentBook.setQuantityInStock(newStock);
                BookDAO.updateBook(currentBook);

                LOGGER.log(Level.INFO, "Restored {0} units of book {1}. New stock: {2}",
                        new Object[] { restoredQty, book.getBookId(), newStock });
            }
        }

        // Update order status - keep as UNPAID since payment was never completed
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        // PaymentStatus stays UNPAID (no FAILED status exists)
        orderDAO.update(order);

        LOGGER.log(Level.INFO, "Order {0} cancelled due to payment timeout", order.getOrderId());

        // Send cancellation email
        try {
            String reason = "Đơn hàng đã bị huỷ tự động do quá thời gian thanh toán ("
                    + SepayConfig.PAYMENT_TIMEOUT_MINUTES + " phút)";
            emailService.sendOrderCancellation(order, reason);
            LOGGER.log(Level.INFO, "Cancellation email sent for order: {0}", order.getOrderId());
        } catch (Exception emailEx) {
            LOGGER.log(Level.WARNING, "Failed to send cancellation email for order: " + order.getOrderId(), emailEx);
            // Don't fail the cancellation if email fails
        }
    }
}
