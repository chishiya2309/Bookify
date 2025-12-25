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
     * 
     * @param orderId ID of the order
     * @return Order with details or null if not found
     */
    public Order findByIdWithDetails(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderId = :orderId";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
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
     * 
     * @param customerId ID of the customer
     * @return List of Orders with details
     */
    public List<Order> findByCustomerIdWithDetails(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderDetails " +
                    "WHERE o.customer.userId = :customerId " +
                    "ORDER BY o.orderDate DESC";
            TypedQuery<Order> query = em.createQuery(jpql, Order.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
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
}
