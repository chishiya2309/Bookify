package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.OrderDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OrderDetailDAO - Data Access Object for OrderDetail entity
 * Handles CRUD operations and queries for order details
 */
public class OrderDetailDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDetailDAO.class.getName());

    /**
     * Save a new order detail to database
     * 
     * @param orderDetail OrderDetail to save
     */
    public void save(OrderDetail orderDetail) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(orderDetail);
            transaction.commit();
            LOGGER.log(Level.INFO, "OrderDetail saved successfully: {0}", orderDetail.getOrderDetailId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error saving OrderDetail", e);
            throw new RuntimeException("Failed to save OrderDetail", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find order detail by ID
     * 
     * @param orderDetailId ID of the order detail
     * @return OrderDetail or null if not found
     */
    public OrderDetail findById(Integer orderDetailId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            return em.find(OrderDetail.class, orderDetailId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding OrderDetail by ID: " + orderDetailId, e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing order detail
     * 
     * @param orderDetail OrderDetail to update
     */
    public void update(OrderDetail orderDetail) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.merge(orderDetail);
            transaction.commit();
            LOGGER.log(Level.INFO, "OrderDetail updated successfully: {0}", orderDetail.getOrderDetailId());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating OrderDetail", e);
            throw new RuntimeException("Failed to update OrderDetail", e);
        } finally {
            em.close();
        }
    }

    /**
     * Delete an order detail by ID
     * 
     * @param orderDetailId ID of the order detail to delete
     */
    public void delete(Integer orderDetailId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            OrderDetail orderDetail = em.find(OrderDetail.class, orderDetailId);
            if (orderDetail != null) {
                em.remove(orderDetail);
                LOGGER.log(Level.INFO, "OrderDetail deleted successfully: {0}", orderDetailId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting OrderDetail: " + orderDetailId, e);
            throw new RuntimeException("Failed to delete OrderDetail", e);
        } finally {
            em.close();
        }
    }

    /**
     * Find all order details for a specific order
     * 
     * @param orderId ID of the order
     * @return List of OrderDetails
     */
    public List<OrderDetail> findByOrderId(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId";
            TypedQuery<OrderDetail> query = em.createQuery(jpql, OrderDetail.class);
            query.setParameter("orderId", orderId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding OrderDetails by orderId: " + orderId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Find all order details for a specific book (for statistics)
     * 
     * @param bookId ID of the book
     * @return List of OrderDetails containing this book
     */
    public List<OrderDetail> findByBookId(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT od FROM OrderDetail od WHERE od.book.bookId = :bookId";
            TypedQuery<OrderDetail> query = em.createQuery(jpql, OrderDetail.class);
            query.setParameter("bookId", bookId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding OrderDetails by bookId: " + bookId, e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Count how many times a book has been ordered
     * 
     * @param bookId ID of the book
     * @return Total quantity sold
     */
    public long countByBookId(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();

        try {
            String jpql = "SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.book.bookId = :bookId";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("bookId", bookId);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting OrderDetails by bookId: " + bookId, e);
            return 0;
        } finally {
            em.close();
        }
    }
}
