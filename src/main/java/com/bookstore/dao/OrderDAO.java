package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class OrderDAO {

 private EntityManager getEntityManager() {
    return DBUtil.getEmFactory().createEntityManager();
}   

    /* ================= CUSTOMER ================= */

// My Order History (CUSTOMER)
public List<Order> findOrdersByCustomer(Customer customer) {
    EntityManager em = getEntityManager();
    try {
        return em.createQuery("""
            SELECT DISTINCT o
            FROM Order o
            LEFT JOIN FETCH o.orderDetails
            WHERE o.customer = :customer
            ORDER BY o.orderDate DESC
        """, Order.class)
        .setParameter("customer", customer)
        .getResultList();
    } finally {
        em.close();
    }
}



    // Order Detail (Customer)
    public Order findOrderDetail(Integer orderId, Customer customer) {
        EntityManager em = getEntityManager();
        try {
            List<Order> list = em.createQuery("""
                SELECT DISTINCT o
                FROM Order o
                LEFT JOIN FETCH o.orderDetails od
                LEFT JOIN FETCH od.book b
                LEFT JOIN FETCH b.authors
                LEFT JOIN FETCH o.shippingAddress
                LEFT JOIN FETCH o.payment
                WHERE o.orderId = :orderId
                  AND o.customer = :customer
            """, Order.class)
            .setParameter("orderId", orderId)
            .setParameter("customer", customer)
            .getResultList();

            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    /* ================= ADMIN ================= */

    // All orders
    public List<Order> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                SELECT o
                FROM Order o
                ORDER BY o.orderDate DESC
            """, Order.class)
            .getResultList();
        } finally {
            em.close();
        }
    }

    // Admin view / edit order
    public Order findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            List<Order> list = em.createQuery("""
                SELECT DISTINCT o
                FROM Order o
                LEFT JOIN FETCH o.orderDetails od
                LEFT JOIN FETCH od.book
                LEFT JOIN FETCH o.shippingAddress
                LEFT JOIN FETCH o.payment
                WHERE o.orderId = :id
            """, Order.class)
            .setParameter("id", id)
            .getResultList();

            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    public void update(Order order) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(order);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Order order = em.find(Order.class, id);
            if (order != null) {
                em.remove(order);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
