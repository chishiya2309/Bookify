package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.List;

public class BookDAO {

    public Book findById(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT DISTINCT b FROM Book b " +
                    "LEFT JOIN FETCH b.category " +
                    "LEFT JOIN FETCH b.publisher " +
                    "LEFT JOIN FETCH b.authors " +
                    "WHERE b.bookId = :bookId";

            TypedQuery<Book> query = em.createQuery(jpql, Book.class);
            query.setParameter("bookId", bookId);
            Book book = query.getSingleResult();

            // Initialize images in the same session
            Hibernate.initialize(book.getImages());

            return book;
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public long countReviews(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Query q = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId");
            q.setParameter("bookId", bookId);
            return (Long) q.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Review> getReviews(Integer bookId, int page, int size) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.customer " +
                    "WHERE r.book.bookId = :bookId " +
                    "ORDER BY r.reviewDate DESC";

            TypedQuery<Review> query = em.createQuery(jpql, Review.class);
            query.setParameter("bookId", bookId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // New method: calculate average rating without touching lazy reviews collection
    public Double getAverageRating(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.book.bookId = :bookId"
            );
            query.setParameter("bookId", bookId);
            return (Double) query.getSingleResult();
        } finally {
            em.close();
        }
    }
}