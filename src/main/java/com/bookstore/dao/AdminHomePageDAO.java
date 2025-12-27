package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Order;
import com.bookstore.model.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class AdminHomePageDAO {

    // --- 1. CÁC HÀM THỐNG KÊ (COUNT) ---
    public static long countAllBooks() {
        return count("SELECT COUNT(b) FROM Book b");
    }

    public static long countAllUsers() {
        return count("SELECT COUNT(u) FROM User u");
    }

    public static long countAllCustomers() {
        return count("SELECT COUNT(c) FROM Customer c");
    }

    public static long countAllReviews() {
        return count("SELECT COUNT(r) FROM Review r");
    }

    public static long countAllOrders() {
        return count("SELECT COUNT(o) FROM Order o");
    }

    // Hàm phụ trợ count gọn gàng
    private static long count(String query) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return (long) em.createQuery(query).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    // --- 2. CÁC HÀM LẤY DANH SÁCH (RECENT LISTS) ---

    public static List<Order> findRecentSales() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Lấy 3 đơn hàng mới nhất, kèm thông tin Khách, Địa chỉ, Chi tiết
            String qString = "SELECT DISTINCT o FROM Order o " +
                             "LEFT JOIN FETCH o.customer " +
                             "LEFT JOIN FETCH o.shippingAddress " + 
                             "LEFT JOIN FETCH o.orderDetails " +
                             "ORDER BY o.orderDate DESC";
            
            TypedQuery<Order> q = em.createQuery(qString, Order.class);
            q.setMaxResults(3);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close(); // Bắt buộc phải có để trả kết nối
        }
    }

    public static List<Review> findRecentReviews() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Lấy 3 đánh giá mới nhất, kèm Sách và Khách
            String qString = "SELECT r FROM Review r " +
                             "LEFT JOIN FETCH r.book " +
                             "LEFT JOIN FETCH r.customer " +
                             "ORDER BY r.reviewDate DESC";
                             
            TypedQuery<Review> q = em.createQuery(qString, Review.class);
            q.setMaxResults(3);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close(); // Bắt buộc phải có
        }
    }
}