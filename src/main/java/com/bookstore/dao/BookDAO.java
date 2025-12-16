package com.bookstore.dao;
import com.bookstore.model.Book;
import com.bookstore.data.DBUtil;
import jakarta.persistence.EntityManager;

import jakarta.persistence.Query;

import com.bookstore.data.DBUtil;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Create a new book
    public static void createBook(Book book) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(book);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }
        
    public Book findById(Integer id) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    // Get all books sorted by title in ascending order
    public static List<Book> getAllBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT DISTINCT b FROM Book b " +
                             "LEFT JOIN FETCH b.authors " +
                             "LEFT JOIN FETCH b.category " +
                             //"LEFT JOIN FETCH b.images " +
                             "ORDER BY b.title ASC";
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            List<Book> books = q.getResultList();
            return books;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // Get a book by ID
    public static Book getBookById(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT b FROM Book b " +
                    "LEFT JOIN FETCH b.category " +
                    "LEFT JOIN FETCH b.authors " +
                    //"LEFT JOIN FETCH b.images " +
                    "WHERE b.bookId = :id";
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("id", bookId);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // Update a book
    public static void updateBook(Book book) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.merge(book);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }

    // Delete a book
    public static void deleteBook(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            Book book = em.find(Book.class, bookId);
            if (book != null) {
                em.remove(book);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
        } finally {
            em.close();
        }
    }
    public static List<Category> getAllCategories() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String category = "SELECT c FROM Category c ORDER BY c.name";
            TypedQuery<Category> query = em.createQuery(category, Category.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    public static List<Author> getAllAuthors() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String author = "SELECT a FROM Author a ORDER BY a.name";
            TypedQuery<Author> query = em.createQuery(author, Author.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
    public static Category findCategoryById(Integer categoryId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Category.class, categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    public static  Author findAuthorById(Integer authorId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            return em.find(Author.class, authorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
