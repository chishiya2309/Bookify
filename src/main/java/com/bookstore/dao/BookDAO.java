package com.bookstore.dao;
import com.bookstore.model.Book;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import com.bookstore.data.DBUtil;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.model.BookImage;
import com.bookstore.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static com.bookstore.service.ValidationUtil.isValidSearchKeyword;

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

    // Get a book by ID with all relationships fetched (except images - fetch separately)
    public static Book getBookById(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String qString = "SELECT DISTINCT b FROM Book b " +
                    "LEFT JOIN FETCH b.category " +
                    "LEFT JOIN FETCH b.authors " +
                    "LEFT JOIN FETCH b.publisher " +
                    "WHERE b.bookId = :id";
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("id", bookId);
            Book book = q.getSingleResult();
            
            // Fetch images separately to avoid MultipleBagFetchException
            if (book != null) {
                String imageQuery = "SELECT img FROM BookImage img WHERE img.book.bookId = :bookId ORDER BY img.isPrimary DESC, img.sortOrder ASC";
                TypedQuery<BookImage> imgQuery = em.createQuery(imageQuery, BookImage.class);
                imgQuery.setParameter("bookId", bookId);
                List<BookImage> images = imgQuery.getResultList();
                // Ensure images list is properly set (handle null case)
                if (images != null) {
                    book.setImages(images);
                } else {
                    book.setImages(new ArrayList<>());
                }
            }
            
            return book;
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
    
    public static List<Book> searchBooks(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Validate keyword length and check for malicious patterns to prevent DoS attacks
            if (!isValidSearchKeyword(keyword)) {
                return new ArrayList<>();
            }
            
            // 1. Tìm sách theo tiêu đề 
            String qString = "SELECT DISTINCT b FROM Book b " +
                             "LEFT JOIN FETCH b.authors a " +
                             "WHERE lower(b.title) LIKE :keyword";

            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("keyword", "%" + keyword.toLowerCase() + "%");

            List<Book> list = q.getResultList();

            // 2. KHẮC PHỤC LỖI LAZY LOADING CHO ẢNH
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

    public static List<Book> listBooksByCategory(int categoryId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // 1. Lấy sách + Tác giả (JOIN FETCH để tránh lỗi tác giả)
            String qString = "SELECT b FROM Book b " +
                             "LEFT JOIN FETCH b.authors " +
                             "WHERE b.category.categoryId = :catId " +
                             "ORDER BY b.publishDate DESC";

            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("catId", categoryId);

            List<Book> list = q.getResultList();

            // 2. KHẮC PHỤC LỖI ẢNH 
            for (Book b : list) {
                b.getImages().size(); // Kích hoạt tải ảnh
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
