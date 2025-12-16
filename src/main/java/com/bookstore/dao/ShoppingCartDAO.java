/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.dao;
import com.bookstore.model.ShoppingCart;
import com.bookstore.model.CartItem;
import jakarta.persistence.EntityManager;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;


public class ShoppingCartDAO {
    public ShoppingCart findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(ShoppingCart.class, id);
        } finally {
            em.close();
        }
    }
    
    public ShoppingCart findByCustomer(Customer customer) {
        return findByCustomerId(customer.getUserId());
    }
    
    public ShoppingCart findByCustomerId(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Lấy cart với items và books
            TypedQuery<ShoppingCart> query = em.createQuery(
                "SELECT DISTINCT c FROM ShoppingCart c " +
                "LEFT JOIN FETCH c.items i " +
                "LEFT JOIN FETCH i.book b " +
                "WHERE c.customer.userId = :customerId",
                ShoppingCart.class
            );
            query.setParameter("customerId", customerId);
            ShoppingCart cart = query.getSingleResult();
            
            // Fetch book images và authors (tránh MultipleBagFetchException)
            if (cart != null && !cart.getItems().isEmpty()) {
                for (CartItem item : cart.getItems()) {
                    // Trigger lazy loading trong transaction
                    item.getBook().getImages().size();
                    item.getBook().getAuthors().size();
                }
            }
            return cart;
        } catch(NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public void save(ShoppingCart cart) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Re-attach customer nếu đang detached
            if (cart.getCustomer() != null && cart.getCustomer().getUserId() != null) {
                Customer managedCustomer = em.find(Customer.class, cart.getCustomer().getUserId());
                if (managedCustomer != null) {
                    cart.setCustomer(managedCustomer);
                }
            }
            
            em.persist(cart);
            em.getTransaction().commit();
        }catch(Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }finally {
            em.close();
        }
    }
    
    public void update(ShoppingCart cart) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cart);
            em.getTransaction().commit();
        }catch(Exception e) {
            em.getTransaction().rollback();
            throw e;
        }finally {
            em.close();
        }
    }
    
    public void delete(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            ShoppingCart cart = em.find(ShoppingCart.class, id);
            if(cart != null) {
                em.remove(cart);
            }
            em.getTransaction().commit();
        }catch(Exception e) {
            em.getTransaction().rollback();
            throw e;
        }finally {
            em.close();
        }
    }
}
