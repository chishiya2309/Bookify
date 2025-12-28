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
import com.bookstore.model.Review;
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
            em.flush();
            trans.commit();
        } catch (Exception e) {
            System.err.println("ERROR: Failed to create book");
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
                    "LEFT JOIN FETCH b.publisher " +
                    "ORDER BY b.title ASC";
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            List<Book> books = q.getResultList();

            // Fetch images separately for each book to avoid MultipleBagFetchException
            for (Book book : books) {
                String imageQuery = "SELECT img FROM BookImage img WHERE img.book.bookId = :bookId ORDER BY img.isPrimary DESC, img.sortOrder ASC";
                TypedQuery<BookImage> imgQuery = em.createQuery(imageQuery, BookImage.class);
                imgQuery.setParameter("bookId", book.getBookId());
                List<BookImage> images = imgQuery.getResultList();
                book.setImages(images != null ? images : new ArrayList<>());
            }

            return books;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to get all books");
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list instead of null
        } finally {
            em.close();
        }
    }

    // Get books with pagination - optimized for admin list page
    public static List<Book> getAllBooksPaginated(int page, int size) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Step 1: Get paginated book IDs first
            String countQuery = "SELECT b.bookId FROM Book b ORDER BY b.title ASC";
            TypedQuery<Integer> idQuery = em.createQuery(countQuery, Integer.class);
            idQuery.setFirstResult(page * size);
            idQuery.setMaxResults(size);
            List<Integer> bookIds = idQuery.getResultList();

            if (bookIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Step 2: Fetch books with relationships in single query
            String qString = "SELECT DISTINCT b FROM Book b " +
                    "LEFT JOIN FETCH b.authors " +
                    "LEFT JOIN FETCH b.category " +
                    "LEFT JOIN FETCH b.publisher " +
                    "WHERE b.bookId IN :bookIds " +
                    "ORDER BY b.title ASC";
            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("bookIds", bookIds);
            List<Book> books = q.getResultList();

            // Step 3: Fetch ONLY primary images in a single batch query
            String imgQuery = "SELECT img FROM BookImage img WHERE img.book.bookId IN :bookIds AND img.isPrimary = true";
            TypedQuery<BookImage> imageQuery = em.createQuery(imgQuery, BookImage.class);
            imageQuery.setParameter("bookIds", bookIds);
            List<BookImage> primaryImages = imageQuery.getResultList();

            // Group primary images by book ID
            java.util.Map<Integer, List<BookImage>> imageMap = new java.util.HashMap<>();
            for (BookImage img : primaryImages) {
                imageMap.computeIfAbsent(img.getBook().getBookId(), k -> new ArrayList<>()).add(img);
            }

            // Assign primary images to books
            for (Book book : books) {
                book.setImages(imageMap.getOrDefault(book.getBookId(), new ArrayList<>()));
            }

            return books;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to get paginated books");
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // Count total books for pagination
    public static long countAllBooks() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT COUNT(b) FROM Book b";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    // Get a book by ID with all relationships fetched (except images - fetch
    // separately)
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

    public static Author findAuthorById(Integer authorId) {
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

    public static long countReviewsBook(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        String jpql = "SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("bookId", bookId);
        return query.getSingleResult();
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
                    "SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.book.bookId = :bookId");
            query.setParameter("bookId", bookId);
            return (Double) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public static List<Book> searchBooks(String keyword) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Validate keyword length and check for malicious patterns to prevent DoS
            // attacks
            if (!isValidSearchKeyword(keyword)) {
                return new ArrayList<>();
            }

            // Trim keyword to ensure consistency with validation
            String trimmedKeyword = keyword.trim();

            // 1. Tìm sách theo tiêu đề
            String qString = "SELECT DISTINCT b FROM Book b " +
                    "LEFT JOIN FETCH b.authors a " +
                    "WHERE lower(b.title) LIKE :keyword";

            TypedQuery<Book> q = em.createQuery(qString, Book.class);
            q.setParameter("keyword", "%" + trimmedKeyword.toLowerCase() + "%");

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
