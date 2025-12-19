package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class PaymentDAO {

    /**
     * Tạo một thanh toán mới
     * 
     * @param payment Payment object to save
     */
    public void save(Payment payment) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(payment);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm thanh toán theo ID
     * 
     * @param paymentId Payment ID
     * @return Payment object or null if not found
     */
    public Payment findById(Integer paymentId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Payment.class, paymentId);
        } finally {
            em.close();
        }
    }

    /**
     * Tìm thanh toán theo ID với quan hệ Order được tải ngay
     * 
     * @param paymentId Payment ID
     * @return Payment object with Order or null if not found
     */
    public Payment findByIdWithOrder(Integer paymentId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p " +
                            "JOIN FETCH p.order o " +
                            "WHERE p.paymentId = :paymentId",
                    Payment.class);
            query.setParameter("paymentId", paymentId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Cập nhật thanh toán hiện có
     * 
     * @param payment Payment object to update
     */
    public void update(Payment payment) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(payment);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Xóa thanh toán theo ID
     * 
     * @param paymentId Payment ID to delete
     */
    public void delete(Integer paymentId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Payment payment = em.find(Payment.class, paymentId);
            if (payment != null) {
                em.remove(payment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Tìm thanh toán theo ID đơn hàng (1-1 relationship)
     * 
     * @param orderId Order ID
     * @return Payment object or null if not found
     */
    public Payment findByOrderId(Integer orderId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.order.orderId = :orderId",
                    Payment.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
