package com.bookstore.dao;

import com.bookstore.model.Review;
import com.bookstore.data.DBUtil;

import jakarta.persistence.*;
import java.util.List;

public class ReviewDAO {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int ADMIN_PAGE_SIZE = 20; // Dùng cho trang admin, có thể thay đổi sau

    // ========================
    // Tạo review mới (bởi customer) - GIỮ NGUYÊN
    // ========================
    public void createReview(Review review) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            em.persist(review);
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Không thể tạo đánh giá: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ========================
    // Xóa review (dùng cho cả customer và admin) - GIỮ NGUYÊN
    // ========================
    public void deleteReview(Integer reviewId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                em.remove(review);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Không thể xóa đánh giá: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ========================
    // Lấy review theo ID (có fetch customer và book nếu cần) - GIỮ NGUYÊN
    // ========================
    public Review getReviewById(Integer reviewId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.customer " +
                    "LEFT JOIN FETCH r.book " +
                    "WHERE r.reviewId = :id";
            TypedQuery<Review> query = em.createQuery(jpql, Review.class);
            query.setParameter("id", reviewId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // ========================
    // Lấy tất cả review của một sản phẩm (book) - GIỮ NGUYÊN
    // ========================
    public List<Review> getReviewsByBook(Integer bookId, int page, int size) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Review> getReviewsByBook(Integer bookId, int page) {
        return getReviewsByBook(bookId, page, DEFAULT_PAGE_SIZE);
    }

    // ========================
    // Lấy review của customer cho một sách cụ thể - GIỮ NGUYÊN
    // ========================
    public Review getReviewByCustomerAndBook(Integer customerId, Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.customer " +
                    "LEFT JOIN FETCH r.book " +
                    "WHERE r.customer.userId = :customerId AND r.book.bookId = :bookId";

            TypedQuery<Review> query = em.createQuery(jpql, Review.class);
            query.setParameter("customerId", customerId);
            query.setParameter("bookId", bookId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // ========================
    // Lấy tất cả review của một customer - GIỮ NGUYÊN
    // ========================
    public List<Review> getReviewsByCustomer(Integer customerId, int page, int size) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            String jpql = "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.book b " +
                    "LEFT JOIN FETCH b.category " +
                    "WHERE r.customer.userId = :customerId " +
                    "ORDER BY r.reviewDate DESC";

            TypedQuery<Review> query = em.createQuery(jpql, Review.class);
            query.setParameter("customerId", customerId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Review> getReviewsByCustomer(Integer customerId, int page) {
        return getReviewsByCustomer(customerId, page, DEFAULT_PAGE_SIZE);
    }

    // ========================
    // Đếm review của customer - GIỮ NGUYÊN
    // ========================
    public long countReviewsByCustomer(Integer customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.customer.userId = :customerId");
            query.setParameter("customerId", customerId);
            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // ========================
    // Đếm review của sách - GIỮ NGUYÊN
    // ========================
    public long countReviewsByBook(Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Query query = em.createQuery(
                    "SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId");
            query.setParameter("bookId", bookId);
            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // ========================
    // MỚI: Toggle trạng thái verified của review (admin dùng)
    // ========================
    public void toggleVerified(Integer reviewId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        trans.begin();
        try {
            Review review = em.find(Review.class, reviewId);
            if (review != null) {
                review.setVerified(!review.getVerified());
                em.merge(review);
            }
            trans.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) {
                trans.rollback();
            }
            throw new RuntimeException("Không thể thay đổi trạng thái duyệt đánh giá", e);
        } finally {
            em.close();
        }
    }

    // ========================
    // MỚI: Lấy danh sách review cho admin với tìm kiếm, lọc và phân trang
    // ========================
    public List<Review> getReviewsForAdmin(
            String bookTitle,
            String customerSearch, // tìm theo tên hoặc email
            Integer rating,
            Boolean verified,
            int page,
            int size) {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT r FROM Review r " +
                            "LEFT JOIN FETCH r.customer c " +
                            "LEFT JOIN FETCH r.book b " +
                            "WHERE 1=1 ");

            if (bookTitle != null && !bookTitle.trim().isEmpty()) {
                jpql.append("AND LOWER(b.title) LIKE LOWER(:bookTitle) ");
            }
            if (customerSearch != null && !customerSearch.trim().isEmpty()) {
                jpql.append(
                        "AND (LOWER(c.fullName) LIKE LOWER(:customerSearch) OR LOWER(c.email) LIKE LOWER(:customerSearch)) ");
            }
            if (rating != null) {
                jpql.append("AND r.rating = :rating ");
            }
            if (verified != null) {
                jpql.append("AND r.isVerified = :verified ");
            }

            jpql.append("ORDER BY r.reviewDate DESC");

            TypedQuery<Review> query = em.createQuery(jpql.toString(), Review.class);

            if (bookTitle != null && !bookTitle.trim().isEmpty()) {
                query.setParameter("bookTitle", "%" + bookTitle.trim() + "%");
            }
            if (customerSearch != null && !customerSearch.trim().isEmpty()) {
                query.setParameter("customerSearch", "%" + customerSearch.trim() + "%");
            }
            if (rating != null) {
                query.setParameter("rating", rating);
            }
            if (verified != null) {
                query.setParameter("verified", verified);
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    // Overload dùng size mặc định cho admin
    public List<Review> getReviewsForAdmin(
            String bookTitle,
            String customerSearch,
            Integer rating,
            Boolean verified,
            int page) {
        return getReviewsForAdmin(bookTitle, customerSearch, rating, verified, page, ADMIN_PAGE_SIZE);
    }

    // ========================
    // MỚI: Đếm tổng số review theo điều kiện tìm kiếm (dùng cho phân trang admin)
    // ========================
    public long countReviewsForAdmin(
            String bookTitle,
            String customerSearch,
            Integer rating,
            Boolean verified) {

        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(r) FROM Review r WHERE 1=1 ");

            if (bookTitle != null && !bookTitle.trim().isEmpty()) {
                jpql.append("AND LOWER(r.book.title) LIKE LOWER(:bookTitle) ");
            }
            if (customerSearch != null && !customerSearch.trim().isEmpty()) {
                jpql.append(
                        "AND (LOWER(r.customer.fullName) LIKE LOWER(:customerSearch) OR LOWER(r.customer.email) LIKE LOWER(:customerSearch)) ");
            }
            if (rating != null) {
                jpql.append("AND r.rating = :rating ");
            }
            if (verified != null) {
                jpql.append("AND r.isVerified = :verified ");
            }

            Query query = em.createQuery(jpql.toString());

            if (bookTitle != null && !bookTitle.trim().isEmpty()) {
                query.setParameter("bookTitle", "%" + bookTitle.trim() + "%");
            }
            if (customerSearch != null && !customerSearch.trim().isEmpty()) {
                query.setParameter("customerSearch", "%" + customerSearch.trim() + "%");
            }
            if (rating != null) {
                query.setParameter("rating", rating);
            }
            if (verified != null) {
                query.setParameter("verified", verified);
            }

            return (Long) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public boolean hasPurchasedAndDelivered(Integer customerId, Integer bookId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            // Dùng Native SQL để đảm bảo đúng column names
            String sql = "SELECT COUNT(*) FROM order_details od " +
                    "INNER JOIN orders o ON od.order_id = o.order_id " +
                    "WHERE o.customer_id = :customerId " +
                    "AND od.book_id = :bookId " +
                    "AND o.order_status = 'DELIVERED'";

            jakarta.persistence.Query query = em.createNativeQuery(sql);
            query.setParameter("customerId", customerId);
            query.setParameter("bookId", bookId);

            Object result = query.getSingleResult();
            long count = ((Number) result).longValue();

            System.out.println("[ReviewDAO] hasPurchasedAndDelivered: customerId=" + customerId +
                    ", bookId=" + bookId + ", count=" + count);

            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}