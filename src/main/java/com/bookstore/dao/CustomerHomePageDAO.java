package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class CustomerHomePageDAO {

    // --- 1. NEW BOOKS ---
    public static List<Book> listNewBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT DISTINCT b FROM Book b " +
                             "LEFT JOIN FETCH b.authors " +
                             "ORDER BY b.publishDate DESC";
            
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setMaxResults(4);
            return q.getResultList();
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
            
            // Ép tải danh sách tác giả trước khi đóng kết nối
            for (Book b : list) {
                b.getAuthors().size(); 
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
            
            // Ép tải danh sách tác giả trước khi đóng kết nối
            for (Book b : list) {
                b.getAuthors().size(); 
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