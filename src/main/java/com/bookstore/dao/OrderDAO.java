package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Order;
import com.bookstore.model.Order.OrderStatus;
import com.bookstore.model.Order.PaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OrderDAO - Data Access Object for Order entity
 * Handles CRUD operations, customer queries, and order statistics
 */
public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    /**
     * Save a new order to database (cascade saves OrderDetails)
     * 
     * @param order Order to save
     */
    public void save(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(order);
            transaction.commit();
            LOGGER.log(Level.INFO, "Order saved successfully: {0}", order.getOrderId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error saving Order", e);
            throw new RuntimeException("Failed to save Order", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find order by ID
     * 
     * @param orderId ID of the order
     * @return Order or null if not found
     */
    public Order findById(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            return em.find(Order.class, orderId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Order by ID: " + orderId, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Find order by ID with OrderDetails eagerly loaded
     * Uses two-step fetch to avoid MultipleBagFetchException
     * Forces initialization of lazy collections to prevent
     * LazyInitializationException
     * 
     * @param orderId ID of the order
     * @return Order with details or null if not found
     */
    public Order findByIdWithDetails(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            // Step 1: Fetch Order with OrderDetails, Books, Customer, and ShippingAddress
            String jpql1 = "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.customer " +
                    "LEFT JOIN FETCH o.orderDetails od " +
                    "LEFT JOIN FETCH od.book " +
                    "LEFT JOIN FETCH o.shippingAddress " +
                    "WHERE o.orderId = :orderId";
            TypedQuery<Order> query1 = em.createQuery(jpql1, Order.class);
            query1.setParameter("orderId", orderId);
            Order order = query1.getSingleResult();

            // Step 2: Fetch Authors and Images for each book
            if (order != null && order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                // Get book IDs
                List<Integer> bookIds = order.getOrderDetails().stream()
                        .map(od -> od.getBook().getBookId())
                        .distinct()
                        .toList();

                if (!bookIds.isEmpty()) {
                    // Fetch Authors
                    String jpql2 = "SELECT DISTINCT b FROM Book b " +
                            "LEFT JOIN FETCH b.authors " +
                            "WHERE b.bookId IN :bookIds";
                    TypedQuery<com.bookstore.model.Book> query2 = em.createQuery(jpql2, com.bookstore.model.Book.class);
                    query2.setParameter("bookIds", bookIds);
                    List<com.bookstore.model.Book> booksWithAuthors = query2.getResultList();

                    // Fetch Images
                    String jpql3 = "SELECT DISTINCT b FROM Book b " +
                            "LEFT JOIN FETCH b.images " +
                            "WHERE b.bookId IN :bookIds";
                    TypedQuery<com.bookstore.model.Book> query3 = em.createQuery(jpql3, com.bookstore.model.Book.class);
                    query3.setParameter("bookIds", bookIds);
                    List<com.bookstore.model.Book> booksWithImages = query3.getResultList();

                    // Force initialize all lazy collections to prevent LazyInitializationException
                    for (com.bookstore.model.Book book : booksWithAuthors) {
                        org.hibernate.Hibernate.initialize(book.getAuthors());
                    }
                    for (com.bookstore.model.Book book : booksWithImages) {
                        org.hibernate.Hibernate.initialize(book.getImages());
                    }
                }
            }

            return order;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Order with details by ID: " + orderId, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing order
     * 
     * @param order Order to update
     */
    public void update(Order order) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.merge(order);
            transaction.commit();
            LOGGER.log(Level.INFO, "Order updated successfully: {0}", order.getOrderId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating Order", e);
            throw new RuntimeException("Failed to update Order", e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete an order by ID (cascade deletes OrderDetails)
     * 
     * @param orderId ID of the order to delete
     */
    public void delete(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Order order = em.find(Order.class, orderId);
            if (order != null) {
                em.remove(order);
                LOGGER.log(Level.INFO, "Order deleted successfully: {0}", orderId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting Order: " + orderId, e);
            throw new RuntimeException("Failed to delete Order", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find all orders for a specific customer
     * 
     * @param customerId ID of the customer
     * @return List of Orders sorted by date (newest first)
     */
    public List<Order> findByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT o FROM Order o WHERE o.customer.userId = :customerId ORDER BY o.orderDate DESC";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Orders by customerId: " + customerId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Find all orders for a customer with OrderDetails eagerly loaded
     * Uses two-step fetch to avoid MultipleBagFetchException
     * 
     * @param customerId ID of the customer
     * @return List of Orders with details
     */
    public List<Order> findByCustomerIdWithDetails(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            // Step 1: Fetch Orders with OrderDetails and Books
            String jpql1 = "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderDetails od " +
                    "LEFT JOIN FETCH od.book " +
                    "WHERE o.customer.userId = :customerId " +
                    "ORDER BY o.orderDate DESC";
            TypedQuery<Order> query1 = em.createQuery(jpql1, Order.class);
            query1.setParameter("customerId", customerId);
            List<Order> orders = query1.getResultList();

            // Step 2: Fetch Authors for all books
            if (!orders.isEmpty()) {
                // Collect all unique book IDs
                List<Integer> bookIds = orders.stream()
                        .flatMap(o -> o.getOrderDetails().stream())
                        .map(od -> od.getBook().getBookId())
                        .distinct()
                        .toList();

                if (!bookIds.isEmpty()) {
                    String jpql2 = "SELECT DISTINCT b FROM Book b " +
                            "LEFT JOIN FETCH b.authors " +
                            "WHERE b.bookId IN :bookIds";
                    TypedQuery<com.bookstore.model.Book> query2 = em.createQuery(jpql2, com.bookstore.model.Book.class);
                    query2.setParameter("bookIds", bookIds);
                    query2.getResultList(); // Populate authors in persistence context

                    // Step 3: Fetch Images for all books
                    String jpql3 = "SELECT DISTINCT b FROM Book b " +
                            "LEFT JOIN FETCH b.images " +
                            "WHERE b.bookId IN :bookIds";
                    TypedQuery<com.bookstore.model.Book> query3 = em.createQuery(jpql3, com.bookstore.model.Book.class);
                    query3.setParameter("bookIds", bookIds);
                    query3.getResultList(); // Populate images in persistence context
                }
            }

            return orders;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Orders with details by customerId: " + customerId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Find orders by order status
     * 
     * @param status OrderStatus to filter by
     * @return List of Orders with given status
     */
    public List<Order> findByStatus(OrderStatus status) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT o FROM Order o WHERE o.orderStatus = :status ORDER BY o.orderDate DESC";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Orders by status: " + status, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Find orders by payment status
     * 
     * @param paymentStatus PaymentStatus to filter by
     * @return List of Orders with given payment status
     */
    public List<Order> findByPaymentStatus(PaymentStatus paymentStatus) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus ORDER BY o.orderDate DESC";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("paymentStatus", paymentStatus);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding Orders by paymentStatus: " + paymentStatus, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Count total orders for a customer
     * 
     * @param customerId ID of the customer
     * @return Number of orders
     */
    public long countByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT COUNT(o) FROM Order o WHERE o.customer.userId = :customerId";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("customerId", customerId);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting Orders by customerId: " + customerId, e);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Get total revenue (sum of all order amounts) for a customer
     * 
     * @param customerId ID of the customer
     * @return Total amount spent
     */
    public BigDecimal getTotalRevenueByCustomer(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                    "WHERE o.customer.userId = :customerId " +
                    "AND o.paymentStatus = :paymentStatus";
            TypedQuery<BigDecimal> query = em.createQuery(jpql, BigDecimal.class);
            query.setParameter("customerId", customerId);
            query.setParameter("paymentStatus", PaymentStatus.PAID);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue by customerId: " + customerId, e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Get all orders (for admin)
     * 
     * @return List of all orders
     */
    public List<Order> findAll() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT o FROM Order o ORDER BY o.orderDate DESC";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding all Orders", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Find expired pending orders for automatic cancellation
     * 
     * @param paymentMethod Payment method (e.g., "BANK_TRANSFER")
     * @param cutoffTime    Orders older than this time are considered expired
     * @return List of expired orders with details loaded
     */
    public List<Order> findExpiredPendingOrders(String paymentMethod, java.time.LocalDateTime cutoffTime) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderDetails od " +
                    "LEFT JOIN FETCH od.book " +
                    "LEFT JOIN FETCH o.customer " +
                    "WHERE o.paymentMethod = :paymentMethod " +
                    "AND o.paymentStatus = :paymentStatus " +
                    "AND o.orderStatus = :orderStatus " +
                    "AND o.orderDate < :cutoffTime";

            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("paymentMethod", paymentMethod);
            query.setParameter("paymentStatus", PaymentStatus.UNPAID);
            query.setParameter("orderStatus", OrderStatus.PENDING);
            query.setParameter("cutoffTime", cutoffTime);

            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding expired pending orders", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Kiểm tra nếu khách hàng đã mua sách và đơn hàng có trạng thái DELIVERED
     * Dùng để xác định khách hàng có quyền viết review không
     * 
     * @param customerId ID of the customer
     * @param bookId     ID of the book
     * @return true if customer has a delivered order containing this book
     */
    public boolean hasCustomerPurchasedBookWithDelivered(Integer customerId, Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            // Dùng native query để đảm bảo chính xác
            String sql = "SELECT COUNT(*) FROM order_details od " +
                    "INNER JOIN orders o ON od.order_id = o.order_id " +
                    "WHERE o.customer_id = :customerId " +
                    "AND od.book_id = :bookId " +
                    "AND o.order_status = 'DELIVERED'";

            jakarta.persistence.Query query = em.createNativeQuery(sql);
            query.setParameter("customerId", customerId);
            query.setParameter("bookId", bookId);

            Object result = query.getSingleResult();
            long count = ((Number) result).longValue();

            LOGGER.log(Level.INFO, "Check purchase: customerId={0}, bookId={1}, count={2}",
                    new Object[] { customerId, bookId, count });

            return count > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    "Error checking if customer purchased book with DELIVERED status: " + e.getMessage(), e);
            return false;
        } finally {
            em.close();
        }
    }
}
