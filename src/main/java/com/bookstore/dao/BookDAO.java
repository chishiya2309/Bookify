package com.bookstore.dao;

import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class BookDAO {
    
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