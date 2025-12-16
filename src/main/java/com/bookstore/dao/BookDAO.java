package com.bookstore.dao;
import com.bookstore.model.Book;
import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;

import jakarta.persistence.Query;

public class BookDAO {

    public Book findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }
    
    public long count() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Query query = em.createQuery("SELECT COUNT(b) FROM Book b");
            return (long) query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
