package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticsDAO {

    private static final Logger LOGGER = Logger.getLogger(StatisticsDAO.class.getName());

    // ================= REVENUE METRICS =================

    /**
     * Get total revenue from DELIVERED orders (all time)
     */
    public BigDecimal getTotalRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT COALESCE(SUM(o.totalAmount), 0)
                        FROM Order o
                        WHERE o.orderStatus = :status
                    """;
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Get today's revenue from DELIVERED orders
     */
    public BigDecimal getTodayRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            String jpql = """
                        SELECT COALESCE(SUM(o.totalAmount), 0)
                        FROM Order o
                        WHERE o.orderStatus = :status
                          AND o.orderDate BETWEEN :start AND :end
                    """;
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("start", startOfDay)
                    .setParameter("end", endOfDay)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting today revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Get this month's revenue from DELIVERED orders
     */
    public BigDecimal getThisMonthRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = LocalDate.now().atTime(LocalTime.MAX);

            String jpql = """
                        SELECT COALESCE(SUM(o.totalAmount), 0)
                        FROM Order o
                        WHERE o.orderStatus = :status
                          AND o.orderDate BETWEEN :start AND :end
                    """;
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("start", startOfMonth)
                    .setParameter("end", endOfMonth)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting this month revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Get this year's revenue from DELIVERED orders
     */
    public BigDecimal getThisYearRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            LocalDateTime startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay();
            LocalDateTime endOfYear = LocalDate.now().atTime(LocalTime.MAX);

            String jpql = """
                        SELECT COALESCE(SUM(o.totalAmount), 0)
                        FROM Order o
                        WHERE o.orderStatus = :status
                          AND o.orderDate BETWEEN :start AND :end
                    """;
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("start", startOfYear)
                    .setParameter("end", endOfYear)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting this year revenue", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    /**
     * Get average order value from DELIVERED orders
     */
    public BigDecimal getAverageOrderValue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT COALESCE(AVG(o.totalAmount), 0)
                        FROM Order o
                        WHERE o.orderStatus = :status
                    """;
            Double avg = em.createQuery(jpql, Double.class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .getSingleResult();
            return BigDecimal.valueOf(avg != null ? avg : 0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting average order value", e);
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    // ================= ORDER STATISTICS =================

    /**
     * Get order count by status
     * Returns: [status, count]
     */
    public List<Object[]> getOrderCountByStatus() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT o.orderStatus, COUNT(o)
                        FROM Order o
                        GROUP BY o.orderStatus
                    """;
            return em.createQuery(jpql, Object[].class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting order count by status", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Get order count by payment method
     * Returns: [paymentMethod, count]
     */
    public List<Object[]> getOrderCountByPaymentMethod() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT o.paymentMethod, COUNT(o)
                        FROM Order o
                        GROUP BY o.paymentMethod
                    """;
            return em.createQuery(jpql, Object[].class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting order count by payment method", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Get total order count
     */
    public long getTotalOrderCount() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(o) FROM Order o";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total order count", e);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Get total customer count
     */
    public long getTotalCustomerCount() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Customer c";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total customer count", e);
            return 0;
        } finally {
            em.close();
        }
    }

    /**
     * Get total book count
     */
    public long getTotalBookCount() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(b) FROM Book b";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total book count", e);
            return 0;
        } finally {
            em.close();
        }
    }

    // ================= TOP ANALYTICS =================

    /**
     * Get top selling books
     * Returns: [bookId, title, mainImageUrl, quantitySold, revenue]
     */
    public List<Object[]> getTopSellingBooks(int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT b.bookId, b.title,
                               (SELECT bi.url FROM BookImage bi WHERE bi.book = b AND bi.isPrimary = true),
                               SUM(od.quantity),
                               SUM(od.subTotal)
                        FROM OrderDetail od
                        JOIN od.book b
                        JOIN od.order o
                        WHERE o.orderStatus = :status
                        GROUP BY b.bookId, b.title
                        ORDER BY SUM(od.quantity) DESC
                    """;
            return em.createQuery(jpql, Object[].class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting top selling books", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    /**
     * Get top customers by total spent
     * Returns: [customerId, fullName, email, orderCount, totalSpent]
     */
    public List<Object[]> getTopCustomers(int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT c.userId, c.fullName, c.email,
                               COUNT(o),
                               SUM(o.totalAmount)
                        FROM Order o
                        JOIN o.customer c
                        WHERE o.orderStatus = :status
                        GROUP BY c.userId, c.fullName, c.email
                        ORDER BY SUM(o.totalAmount) DESC
                    """;
            return em.createQuery(jpql, Object[].class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting top customers", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // ================= REVENUE TRENDS =================

    /**
     * Get revenue by month for a specific year
     * Returns: [month, revenue]
     */
    public List<Object[]> getRevenueByMonth(int year) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT EXTRACT(MONTH FROM o.orderDate), SUM(o.totalAmount)
                        FROM Order o
                        WHERE o.orderStatus = :status
                          AND EXTRACT(YEAR FROM o.orderDate) = :year
                        GROUP BY EXTRACT(MONTH FROM o.orderDate)
                        ORDER BY EXTRACT(MONTH FROM o.orderDate)
                    """;
            return em.createQuery(jpql, Object[].class)
                    .setParameter("status", Order.OrderStatus.DELIVERED)
                    .setParameter("year", year)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting revenue by month", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // ================= INVENTORY ALERTS =================

    /**
     * Get books with low stock (quantity <= threshold)
     * Returns: [bookId, title, mainImageUrl, quantityInStock]
     */
    public List<Object[]> getLowStockBooks(int threshold) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT b.bookId, b.title,
                               (SELECT bi.url FROM BookImage bi WHERE bi.book = b AND bi.isPrimary = true),
                               b.quantityInStock
                        FROM Book b
                        WHERE b.quantityInStock <= :threshold
                        ORDER BY b.quantityInStock ASC
                    """;
            return em.createQuery(jpql, Object[].class)
                    .setParameter("threshold", threshold)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting low stock books", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // ================= RECENT ORDERS =================

    /**
     * Get recent orders for dashboard
     */
    public List<Order> getRecentOrders(int limit) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = """
                        SELECT o FROM Order o
                        JOIN FETCH o.customer
                        ORDER BY o.orderDate DESC
                    """;
            return em.createQuery(jpql, Order.class)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting recent orders", e);
            return List.of();
        } finally {
            em.close();
        }
    }

    // ================= LEGACY METHODS =================

    /**
     * Get category revenue (existing method)
     */
    public List<Object[]> getCategoryRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            String jpql = """
                        SELECT c.categoryId, c.name,
                               SUM(od.quantity),
                               SUM(od.subTotal)
                        FROM OrderDetail od
                        JOIN od.book b
                        JOIN b.category c
                        JOIN od.order o
                        WHERE o.orderStatus = :status
                        GROUP BY c.categoryId, c.name
                        ORDER BY SUM(od.subTotal) DESC
                    """;

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            query.setParameter("status", Order.OrderStatus.DELIVERED);

            List<Object[]> result = query.getResultList();

            transaction.commit();
            return result;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error fetching category revenue statistics", e);
            return List.of();

        } finally {
            em.close();
        }
    }

    /**
     * Get book revenue (existing method)
     */
    public List<Object[]> getBookRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            String jpql = """
                        SELECT b.bookId,
                               b.title,
                               FUNCTION('string_agg', a.name, ', '),
                               SUM(od.quantity),
                               SUM(od.subTotal)
                        FROM OrderDetail od
                        JOIN od.book b
                        JOIN b.authors a
                        JOIN od.order o
                        WHERE o.orderStatus = :status
                        GROUP BY b.bookId, b.title
                        ORDER BY SUM(od.subTotal) DESC
                    """;

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            query.setParameter("status", Order.OrderStatus.DELIVERED);

            List<Object[]> result = query.getResultList();

            transaction.commit();
            return result;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error fetching book revenue statistics", e);
            return List.of();

        } finally {
            em.close();
        }
    }

    /**
     * Get monthly revenue (existing method)
     */
    public List<Object[]> getMonthlyRevenue() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            String jpql = """
                        SELECT EXTRACT(MONTH FROM o.orderDate),
                               SUM(o.totalAmount)
                        FROM Order o
                        WHERE o.orderStatus = :status
                        GROUP BY EXTRACT(MONTH FROM o.orderDate)
                        ORDER BY EXTRACT(MONTH FROM o.orderDate)
                    """;

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            query.setParameter("status", Order.OrderStatus.DELIVERED);

            List<Object[]> result = query.getResultList();

            transaction.commit();
            return result;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Error fetching monthly revenue statistics", e);
            return List.of();

        } finally {
            em.close();
        }
    }
}
