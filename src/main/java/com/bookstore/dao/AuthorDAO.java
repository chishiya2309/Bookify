package com.bookstore.dao;

import com.bookstore.model.Author;
import com.bookstore.data.DBUtil;

import jakarta.persistence.*;
import java.util.List;

public class AuthorDAO {

    private static final int ADMIN_PAGE_SIZE = 20;

    public void createAuthor(Author author) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(author);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw new RuntimeException("Không thể tạo tác giả", e);
        } finally {
            em.close();
        }
    }

    public void updateAuthor(Author author) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.merge(author);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw new RuntimeException("Không thể cập nhật tác giả", e);
        } finally {
            em.close();
        }
    }

    public void deleteAuthor(Integer authorId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            Author author = em.find(Author.class, authorId);
            if (author != null) {
                em.remove(author);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            throw new RuntimeException("Không thể xóa tác giả (có thể có sách liên kết)", e);
        } finally {
            em.close();
        }
    }

    public Author getAuthorById(Integer authorId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Author.class, authorId);
        } finally {
            em.close();
        }
    }

    public List<Author> getAuthorsForAdmin(String name, int page, int size) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE 1=1");

            if (name != null && !name.trim().isEmpty()) {
                jpql.append(" AND LOWER(a.name) LIKE LOWER(:name)");
            }

            jpql.append(" ORDER BY a.name ASC");

            TypedQuery<Author> query = em.createQuery(jpql.toString(), Author.class);

            if (name != null && !name.trim().isEmpty()) {
                query.setParameter("name", "%" + name.trim() + "%");
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countAuthorsForAdmin(String name) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(a) FROM Author a WHERE 1=1");

            if (name != null && !name.trim().isEmpty()) {
                jpql.append(" AND LOWER(a.name) LIKE LOWER(:name)");
            }

            Query query = em.createQuery(jpql.toString());

            if (name != null && !name.trim().isEmpty()) {
                query.setParameter("name", "%" + name.trim() + "%");
            }

            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }
}