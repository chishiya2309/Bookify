package com.bookstore.dao;

import java.util.ArrayList;
import java.util.List;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Data Access Object for Admin entity.
 * Provides CRUD operations and query methods for admin management.
 */
public class AdminDAO {

    /**
     * Find all admins with pagination.
     * 
     * @param offset the starting position
     * @param limit the maximum number of results
     * @return list of admins
     */
    public List<Admin> findAll(int offset, int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT a FROM Admin a ORDER BY a.userId ASC";
            TypedQuery<Admin> query = em.createQuery(jpql, Admin.class);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Find admin by ID.
     * 
     * @param id the admin ID
     * @return the admin or null if not found
     */
    public Admin findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Admin.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Find admin by email.
     * 
     * @param email the email address
     * @return the admin or null if not found
     */
    public Admin findByEmail(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT a FROM Admin a WHERE a.email = :email";
            TypedQuery<Admin> query = em.createQuery(jpql, Admin.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Save a new admin to the database.
     * 
     * @param admin the admin to save
     */
    public void save(Admin admin) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(admin);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing admin.
     * 
     * @param admin the admin to update
     */
    public void update(Admin admin) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(admin);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Delete an admin by ID.
     * 
     * @param id the admin ID to delete
     */
    public void delete(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Admin admin = em.find(Admin.class, id);
            if (admin != null) {
                em.remove(admin);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Search admins by email or fullName with pagination.
     * 
     * @param keyword the search keyword (case-insensitive)
     * @param offset the starting position
     * @param limit the maximum number of results
     * @return list of matching admins
     */
    public List<Admin> search(String keyword, int offset, int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT a FROM Admin a WHERE LOWER(a.email) LIKE LOWER(:keyword) " +
                          "OR LOWER(a.fullName) LIKE LOWER(:keyword) ORDER BY a.userId ASC";
            TypedQuery<Admin> query = em.createQuery(jpql, Admin.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Count total number of admins.
     * 
     * @return the total count
     */
    public long countAll() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Admin a";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Count admins matching search keyword.
     * 
     * @param keyword the search keyword (case-insensitive)
     * @return the count of matching admins
     */
    public long countBySearch(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Admin a WHERE LOWER(a.email) LIKE LOWER(:keyword) " +
                          "OR LOWER(a.fullName) LIKE LOWER(:keyword)";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Check if an email already exists in the admin table.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Admin a WHERE a.email = :email";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Check if an email exists excluding a specific admin ID.
     * Used for update validation to allow keeping the same email.
     * 
     * @param email the email to check
     * @param excludeId the admin ID to exclude from the check
     * @return true if email exists for another admin, false otherwise
     */
    public boolean existsByEmailExcluding(String email, Integer excludeId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Admin a WHERE a.email = :email AND a.userId != :excludeId";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("email", email);
            query.setParameter("excludeId", excludeId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}
