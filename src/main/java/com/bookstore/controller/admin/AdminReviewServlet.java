package com.bookstore.controller.admin;

import com.bookstore.model.Review;
import com.bookstore.service.ReviewServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet("/admin/reviews")
public class AdminReviewServlet extends HttpServlet {

    private final ReviewServices reviewServices = new ReviewServices();
    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Kiểm tra quyền admin
        HttpSession session = request.getSession();
        if (session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listReviews(request, response);
                    break;
                case "delete":
                    deleteReview(request, response);
                    return;
                case "toggleVerified":
                    toggleVerified(request, response);
                    return;
                default:
                    listReviews(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            listReviews(request, response);
        }

        request.getRequestDispatcher("/admin/review/list_review.jsp").forward(request, response);
    }

    private void listReviews(HttpServletRequest request, HttpServletResponse response) {
        String bookTitle = request.getParameter("bookTitle");
        String customerSearch = request.getParameter("customerSearch");
        String ratingStr = request.getParameter("rating");
        String verifiedStr = request.getParameter("verified");
        String pageStr = request.getParameter("page");

        Integer rating = (ratingStr != null && !ratingStr.isEmpty()) ? Integer.parseInt(ratingStr) : null;
        Boolean verified = null;
        if (verifiedStr != null && !verifiedStr.isEmpty()) {
            verified = Boolean.valueOf(verifiedStr);
        }

        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }

        List<Review> reviewList = reviewServices.getReviewsForAdmin(
                bookTitle, customerSearch, rating, verified, page - 1, PAGE_SIZE);

        long totalReviews = reviewServices.countReviewsForAdmin(bookTitle, customerSearch, rating, verified);
        int totalPages = (int) Math.ceil((double) totalReviews / PAGE_SIZE);

        request.setAttribute("reviewList", reviewList);
        request.setAttribute("totalReviews", totalReviews);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.setAttribute("bookTitle", bookTitle);
        request.setAttribute("customerSearch", customerSearch);
        request.setAttribute("rating", rating);
        request.setAttribute("verified", verified);
    }

    private void deleteReview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewIdStr = request.getParameter("reviewId");
        if (reviewIdStr != null && !reviewIdStr.isEmpty()) {
            try {
                int reviewId = Integer.parseInt(reviewIdStr);
                reviewServices.deleteReview(reviewId);
                request.getSession().setAttribute("successMessage", "Xóa đánh giá thành công!");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Không thể xóa đánh giá");
            }
        }
        redirectToListWithParams(request, response);
    }

    private void toggleVerified(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reviewIdStr = request.getParameter("reviewId");
        if (reviewIdStr != null && !reviewIdStr.isEmpty()) {
            try {
                int reviewId = Integer.parseInt(reviewIdStr);
                reviewServices.toggleVerified(reviewId);
                request.getSession().setAttribute("successMessage", "Thay đổi trạng thái duyệt thành công!");
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Không thể thay đổi trạng thái duyệt");
            }
        }
        redirectToListWithParams(request, response);
    }

    // Redirect giữ nguyên tất cả tham số tìm kiếm + trang hiện tại
    private void redirectToListWithParams(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        StringBuilder url = new StringBuilder(request.getContextPath() + "/admin/reviews?action=list");

        String bookTitle = request.getParameter("bookTitle");
        String customerSearch = request.getParameter("customerSearch");
        String rating = request.getParameter("rating");
        String verified = request.getParameter("verified");
        String page = request.getParameter("page");

        if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            url.append("&bookTitle=").append(urlEncode(bookTitle));
        }
        if (customerSearch != null && !customerSearch.trim().isEmpty()) {
            url.append("&customerSearch=").append(urlEncode(customerSearch));
        }
        if (rating != null && !rating.trim().isEmpty()) {
            url.append("&rating=").append(rating);
        }
        if (verified != null && !verified.trim().isEmpty()) {
            url.append("&verified=").append(verified);
        }
        if (page != null && !page.trim().isEmpty()) {
            url.append("&page=").append(page);
        }

        response.sendRedirect(url.toString());
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value; // fallback
        }
    }
}