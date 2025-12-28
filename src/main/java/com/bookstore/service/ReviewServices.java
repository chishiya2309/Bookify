package com.bookstore.service;

import com.bookstore.dao.ReviewDAO;
import com.bookstore.model.Review;

import java.util.List;

public class ReviewServices {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int ADMIN_PAGE_SIZE = 20; // Có thể thay đổi sau, dùng cho trang admin

    // ========================
    // Các phương thức cũ - GIỮ NGUYÊN HOÀN TOÀN
    // ========================

    // Tạo review mới
    public void createReview(Review review) {
        reviewDAO.createReview(review);
    }

    // Xóa review
    public void deleteReview(Integer reviewId) {
        reviewDAO.deleteReview(reviewId);
    }

    // Lấy review của customer cho một sách cụ thể (kiểm tra đã review chưa)
    public Review getCustomerReviewForBook(Integer customerId, Integer bookId) {
        return reviewDAO.getReviewByCustomerAndBook(customerId, bookId);
    }

    // Lấy danh sách review của customer hiện tại (lịch sử đánh giá)
    public List<Review> getReviewsByCustomer(Integer customerId, int page, int size) {
        return reviewDAO.getReviewsByCustomer(customerId, page, size);
    }

    public List<Review> getReviewsByCustomer(Integer customerId, int page) {
        return getReviewsByCustomer(customerId, page, DEFAULT_PAGE_SIZE);
    }

    public long getTotalReviewsByCustomer(Integer customerId) {
        return reviewDAO.countReviewsByCustomer(customerId);
    }

    // Lấy review của một sách (dùng cho trang chi tiết sách)
    public List<Review> getReviewsByBook(Integer bookId, int page, int size) {
        return reviewDAO.getReviewsByBook(bookId, page, size);
    }

    public List<Review> getReviewsByBook(Integer bookId, int page) {
        return getReviewsByBook(bookId, page, DEFAULT_PAGE_SIZE);
    }

    public long getTotalReviewsByBook(Integer bookId) {
        return reviewDAO.countReviewsByBook(bookId);
    }

    // Lấy một review cụ thể theo ID
    public Review getReviewById(Integer reviewId) {
        return reviewDAO.getReviewById(reviewId);
    }

    // ========================
    // MỚI: Các phương thức dành cho ADMIN
    // ========================

    /**
     * Admin: Thay đổi trạng thái duyệt (verified) của một review
     */
    public void toggleVerified(Integer reviewId) {
        reviewDAO.toggleVerified(reviewId);
    }

    /**
     * Admin: Lấy danh sách review với tìm kiếm, lọc và phân trang
     *
     * @param bookTitle      Tên sách (tìm gần đúng, không phân biệt hoa thường)
     * @param customerSearch Tên hoặc email khách hàng (tìm gần đúng)
     * @param rating         Lọc theo số sao (1-5), null = tất cả
     * @param verified       Lọc theo trạng thái duyệt (true/false), null = tất cả
     * @param page           Trang hiện tại (bắt đầu từ 0)
     * @param size           Số review mỗi trang
     * @return Danh sách review đã fetch customer + book
     */
    public List<Review> getReviewsForAdmin(
            String bookTitle,
            String customerSearch,
            Integer rating,
            Boolean verified,
            int page,
            int size) {

        return reviewDAO.getReviewsForAdmin(bookTitle, customerSearch, rating, verified, page, size);
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

    /**
     * Admin: Đếm tổng số review theo điều kiện tìm kiếm/lọc (dùng cho phân trang)
     */
    public long countReviewsForAdmin(
            String bookTitle,
            String customerSearch,
            Integer rating,
            Boolean verified) {

        return reviewDAO.countReviewsForAdmin(bookTitle, customerSearch, rating, verified);
    }
}
