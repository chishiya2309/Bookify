package com.bookstore.dao;

import java.util.ArrayList;
import java.util.List;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Data Access Object for Customer entity.
 * Provides CRUD operations and query methods for customer management.
 */
public class CustomerDAO {

    /**
     * Find all customers with pagination and addresses loaded.
     * 
     * @param offset the starting position
     * @param limit the maximum number of results
     * @return list of customers with addresses
     */
    public List<Customer> findAll(int offset, int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // First get customer IDs with pagination
            String idJpql = "SELECT c.userId FROM Customer c ORDER BY c.userId ASC";
            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class);
            idQuery.setFirstResult(offset);
            idQuery.setMaxResults(limit);
            List<Integer> customerIds = idQuery.getResultList();
            
            if (customerIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Then fetch customers with addresses using the IDs
            String jpql = "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.addresses WHERE c.userId IN :ids ORDER BY c.userId ASC";
            TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
            query.setParameter("ids", customerIds);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Find customer by ID.
     * 
     * @param id the customer ID
     * @return the customer or null if not found
     */
    public Customer findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Customer.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Find customer by ID with all details (addresses, orders, reviews) eagerly loaded.
     * Use this method when you need to access lazy collections outside of transaction.
     * 
     * @param id the customer ID
     * @return the customer with all collections initialized, or null if not found
     */
    public Customer findByIdWithDetails(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT DISTINCT c FROM Customer c " +
                          "LEFT JOIN FETCH c.addresses " +
                          "WHERE c.userId = :id";
            TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
            query.setParameter("id", id);
            Customer customer = query.getResultStream().findFirst().orElse(null);
            
            if (customer != null) {
                // Initialize orders and reviews counts separately to avoid cartesian product
                customer.getOrders().size();
                customer.getReviews().size();
            }
            return customer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Find customer by email.
     * 
     * @param email the email address
     * @return the customer or null if not found
     */
    public Customer findByEmail(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT c FROM Customer c WHERE c.email = :email";
            TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
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
     * Save a new customer to the database.
     * 
     * @param customer the customer to save
     */
    public void save(Customer customer) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(customer);
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
     * Update an existing customer.
     * 
     * @param customer the customer to update
     */
    public void update(Customer customer) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(customer);
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
     * Delete a customer by ID using native SQL to avoid JPA cascade issues.
     * This will delete all associated data in correct order.
     * 
     * @param id the customer ID to delete
     */
    public void delete(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            // Use native SQL to delete in correct order to avoid foreign key constraints.
            // All deletions are wrapped in a transaction to ensure atomicity - if any
            // deletion fails, the entire transaction will be rolled back, preventing
            // database inconsistencies such as orphaned child records.
            
            // 1. Delete payments for customer's orders
            em.createNativeQuery("DELETE FROM payments WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = ?)")
              .setParameter(1, id)
              .executeUpdate();
            
            // 2. Delete order details for customer's orders
            em.createNativeQuery("DELETE FROM order_details WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = ?)")
              .setParameter(1, id)
              .executeUpdate();
            
            // 3. Delete orders (must be before addresses since orders reference addresses)
            em.createNativeQuery("DELETE FROM orders WHERE customer_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
            // 4. Delete reviews
            em.createNativeQuery("DELETE FROM reviews WHERE customer_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
            // 5. Delete shopping cart items
            em.createNativeQuery("DELETE FROM cart_items WHERE cart_id IN (SELECT cart_id FROM shopping_carts WHERE customer_id = ?)")
              .setParameter(1, id)
              .executeUpdate();
            
            // 6. Delete shopping cart
            em.createNativeQuery("DELETE FROM shopping_carts WHERE customer_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
            // 7. Delete addresses (after orders since orders reference addresses)
            em.createNativeQuery("DELETE FROM addresses WHERE customer_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
            // 8. Delete customer from customers table
            em.createNativeQuery("DELETE FROM customers WHERE user_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
            // 9. Delete from users table (parent table)
            em.createNativeQuery("DELETE FROM users WHERE user_id = ?")
              .setParameter(1, id)
              .executeUpdate();
            
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
     * Search customers by email, fullName, or phoneNumber with pagination and addresses loaded.
     * 
     * @param keyword the search keyword (case-insensitive)
     * @param offset the starting position
     * @param limit the maximum number of results
     * @return list of matching customers with addresses
     */
    public List<Customer> search(String keyword, int offset, int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // First get customer IDs with pagination
            String idJpql = "SELECT c.userId FROM Customer c WHERE LOWER(c.email) LIKE LOWER(:keyword) " +
                          "OR LOWER(c.fullName) LIKE LOWER(:keyword) " +
                          "OR LOWER(c.phoneNumber) LIKE LOWER(:keyword) ORDER BY c.userId ASC";
            TypedQuery<Integer> idQuery = em.createQuery(idJpql, Integer.class);
            idQuery.setParameter("keyword", "%" + keyword + "%");
            idQuery.setFirstResult(offset);
            idQuery.setMaxResults(limit);
            List<Integer> customerIds = idQuery.getResultList();
            
            if (customerIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Then fetch customers with addresses using the IDs
            String jpql = "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.addresses WHERE c.userId IN :ids ORDER BY c.userId ASC";
            TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
            query.setParameter("ids", customerIds);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Count total number of customers.
     * 
     * @return the total count
     */
    public long countAll() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Customer c";
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
     * Count customers matching search keyword.
     * 
     * @param keyword the search keyword (case-insensitive)
     * @return the count of matching customers
     */
    public long countBySearch(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Customer c WHERE LOWER(c.email) LIKE LOWER(:keyword) " +
                          "OR LOWER(c.fullName) LIKE LOWER(:keyword) " +
                          "OR LOWER(c.phoneNumber) LIKE LOWER(:keyword)";
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
     * Check if an email already exists in the system (across all users).
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
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
}
