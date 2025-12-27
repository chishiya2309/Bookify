package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.service.ReviewServices;
import com.bookstore.service.BookServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private final ReviewServices reviewServices = new ReviewServices();
    private final BookServices bookServices = new BookServices();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        // Kiểm tra đăng nhập
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customer/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        // ==================== TẠO REVIEW MỚI ====================
        if ("create".equals(action)) {
            handleCreateReview(request, response, session, customer);
            return;
        }

        // ==================== XÓA REVIEW ====================
        if ("delete".equals(action)) {
            handleDeleteReview(request, response, session, customer);
            return;
        }

        // Nếu action không hợp lệ
        session.setAttribute("error", "Thao tác không hợp lệ");
        response.sendRedirect(request.getContextPath() + "/view_book");
    }

    // Xử lý tạo review
    private void handleCreateReview(HttpServletRequest request, HttpServletResponse response,
                                    HttpSession session, Customer customer) throws IOException {

        String bookIdParam = request.getParameter("bookId");
        if (bookIdParam == null || bookIdParam.isEmpty()) {
            session.setAttribute("error", "Không tìm thấy sách");
            response.sendRedirect(request.getContextPath() + "/view_book");
            return;
        }

        int bookId = Integer.parseInt(bookIdParam);
        Book book = bookServices.getBookById(bookId);
        if (book == null) {
            session.setAttribute("error", "Sách không tồn tại");
            response.sendRedirect(request.getContextPath() + "/view_book");
            return;
        }

        // Kiểm tra đã review chưa (unique constraint sẽ ngăn, nhưng kiểm tra trước cho UX tốt hơn)
        Review existingReview = reviewServices.getCustomerReviewForBook(customer.getUserId(), bookId);
        if (existingReview != null) {
            session.setAttribute("error", "Bạn đã đánh giá sách này rồi");
            response.sendRedirect(request.getContextPath() + "/view_book?id=" + bookId);
            return;
        }

        String ratingParam = request.getParameter("rating");
        String headline = request.getParameter("headline");
        String comment = request.getParameter("comment");

        if (ratingParam == null || ratingParam.isEmpty()) {
            session.setAttribute("error", "Vui lòng chọn số sao");
            response.sendRedirect(request.getContextPath() + "/view_book?id=" + bookId);
            return;
        }

        try {
            int rating = Integer.parseInt(ratingParam);

            Review review = new Review();
            review.setRating(rating);
            review.setHeadline(headline);
            review.setComment(comment);
            review.setCustomer(customer);
            review.setBook(book);

            reviewServices.createReview(review);
            session.setAttribute("success", "Gửi đánh giá thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi khi gửi đánh giá: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/view_book?id=" + bookId);
    }

    // Xử lý xóa review
    private void handleDeleteReview(HttpServletRequest request, HttpServletResponse response,
                                    HttpSession session, Customer customer) throws IOException {

        String reviewIdParam = request.getParameter("reviewId");
        if (reviewIdParam == null || reviewIdParam.isEmpty()) {
            session.setAttribute("error", "Không tìm thấy đánh giá để xóa");
            response.sendRedirect(request.getContextPath() + "/view_book");
            return;
        }

        try {
            int reviewId = Integer.parseInt(reviewIdParam);

            // Lấy review từ DB để kiểm tra quyền sở hữu
            Review review = reviewServices.getReviewById(reviewId);
            if (review == null) {
                session.setAttribute("error", "Đánh giá không tồn tại");
                response.sendRedirect(request.getContextPath() + "/view_book");
                return;
            }

            // Bảo mật: chỉ cho phép xóa review của chính mình
            if (!review.getCustomer().getUserId().equals(customer.getUserId())) {
                session.setAttribute("error", "Bạn không có quyền xóa đánh giá này");
                response.sendRedirect(request.getContextPath() + "/view_book");
                return;
            }

            int bookId = review.getBook().getBookId();

            reviewServices.deleteReview(reviewId);
            session.setAttribute("success", "Xóa đánh giá thành công!");

            response.sendRedirect(request.getContextPath() + "/view_book?id=" + bookId);

        } catch (NumberFormatException e) {
            session.setAttribute("error", "ID đánh giá không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/view_book");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Có lỗi khi xóa đánh giá");
            response.sendRedirect(request.getContextPath() + "/view_book");
        }
    }
}