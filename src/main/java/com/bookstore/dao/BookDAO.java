package com.bookstore.dao;
import com.bookstore.model.Book;
import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;

public class BookDAO {
    public Book findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }
}
