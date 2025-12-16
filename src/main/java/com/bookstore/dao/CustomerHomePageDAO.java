package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class CustomerHomePageDAO {
    
    // --- CATEGORIES ---
    public static List<Category> listAllCategories() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT c FROM Category c ORDER BY c.name ASC";
            TypedQuery<Category> q = em.createQuery(qString, Category.class);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // --- 1. NEW BOOKS ---
    public static List<Book> listNewBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT DISTINCT b FROM Book b " +
                             "LEFT JOIN FETCH b.authors " +
                             "ORDER BY b.publishDate DESC";
            
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setMaxResults(4);
            List<Book> list = q.getResultList();
            
            // Ép tải images trước khi đóng EntityManager
            for (Book b : list) {
                b.getImages().size();
            }
            
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // --- 2. BEST-SELLING BOOKS ---
    public static List<Book> listBestSellingBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT od.book FROM OrderDetail od " +
                             "GROUP BY od.book " +
                             "ORDER BY SUM(od.quantity) DESC";
            
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setMaxResults(4);
            
            List<Book> list = q.getResultList();
            
            // Ép tải danh sách tác giả và images trước khi đóng kết nối
            for (Book b : list) {
                b.getAuthors().size();
                b.getImages().size();
            }
            
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // --- 3. MOST-FAVORED BOOKS ---
    public static List<Book> listMostFavoredBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT r.book FROM Review r " +
                             "GROUP BY r.book " +
                             "ORDER BY AVG(r.rating) DESC";
            
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setMaxResults(4);
            
            List<Book> list = q.getResultList();
            
            // Ép tải danh sách tác giả và images trước khi đóng kết nối
            for (Book b : list) {
                b.getAuthors().size();
                b.getImages().size();
            }
            
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}