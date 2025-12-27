package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Voucher;
import com.bookstore.model.VoucherUsage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoucherDAO {

    private static final Logger LOGGER = Logger.getLogger(VoucherDAO.class.getName());

    public Voucher findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT v FROM Voucher v WHERE UPPER(v.code) = :code";
            TypedQuery<Voucher> query = em.createQuery(jpql, Voucher.class);
            query.setParameter("code", code.toUpperCase().trim());
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding voucher by code: " + code, e);
            return null;
        } finally {
            em.close();
        }
    }

    public List<Voucher> findAllActive() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT v FROM Voucher v WHERE v.isActive = true " +
                    "AND v.startDate <= CURRENT_TIMESTAMP AND v.endDate >= CURRENT_TIMESTAMP " +
                    "ORDER BY v.endDate ASC";
            return em.createQuery(jpql, Voucher.class).getResultList();
        } finally {
            em.close();
        }
    }

    public int getUserUsageCount(Integer voucherId, Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(u) FROM VoucherUsage u " +
                    "WHERE u.voucher.voucherId = :voucherId AND u.customer.userId = :customerId";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("voucherId", voucherId);
            query.setParameter("customerId", customerId);
            Long count = query.getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting user usage count", e);
            return 0;
        } finally {
            em.close();
        }
    }

    public void incrementUsage(Integer voucherId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            String jpql = "UPDATE Voucher v SET v.currentUses = v.currentUses + 1 " +
                    "WHERE v.voucherId = :voucherId";
            em.createQuery(jpql)
                    .setParameter("voucherId", voucherId)
                    .executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error incrementing voucher usage", e);
        } finally {
            em.close();
        }
    }

    public void recordUsage(VoucherUsage usage) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usage);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error recording voucher usage", e);
        } finally {
            em.close();
        }
    }

    public Voucher save(Voucher voucher) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (voucher.getVoucherId() == null) {
                em.persist(voucher);
            } else {
                voucher = em.merge(voucher);
            }
            em.getTransaction().commit();
            return voucher;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error saving voucher", e);
            throw new RuntimeException("Failed to save voucher", e);
        } finally {
            em.close();
        }
    }

    public Voucher findById(Integer voucherId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Voucher.class, voucherId);
        } finally {
            em.close();
        }
    }

    public List<Voucher> findAll() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT v FROM Voucher v ORDER BY v.createdAt DESC";
            return em.createQuery(jpql, Voucher.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Voucher update(Voucher voucher) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            voucher = em.merge(voucher);
            em.getTransaction().commit();
            return voucher;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error updating voucher", e);
            throw new RuntimeException("Failed to update voucher", e);
        } finally {
            em.close();
        }
    }

    public void delete(Voucher voucher) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Voucher managed = em.find(Voucher.class, voucher.getVoucherId());
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Error deleting voucher", e);
            throw new RuntimeException("Failed to delete voucher", e);
        } finally {
            em.close();
        }
    }
}
