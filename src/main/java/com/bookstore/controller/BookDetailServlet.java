package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Review;
import com.bookstore.service.BookServices;
import com.bookstore.service.ReviewServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/view_book")
public class BookDetailServlet extends HttpServlet {

    private final BookServices bookServices = new BookServices();
    private final ReviewServices reviewServices = new ReviewServices(); // ← THÊM

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        // ==================== LOAD MORE REVIEWS (AJAX) ====================
        if ("loadMore".equals(action)) {
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");

            if (idParam == null || pageParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                int bookId = Integer.parseInt(idParam);
                int page = Integer.parseInt(pageParam);

                List<Review> reviews = reviewServices.getReviewsByBook(bookId, page); // ← DÙNG ReviewServices

                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();

                if (reviews != null && !reviews.isEmpty()) {
                    for (Review r : reviews) {
                        out.println("<div class=\"review-item\">");
                        out.println("<strong>" + escapeHtml(r.getCustomer().getFullName()) + "</strong> ");

                        out.print("<span class=\"rating\">");
                        for (int i = 0; i < r.getRating(); i++) {
                            out.print("★");
                        }
                        out.println("</span> ");

                        out.println("<span class=\"date\">(" + r.getReviewDate() + ")</span><br>");

                        if (r.getHeadline() != null && !r.getHeadline().trim().isEmpty()) {
                            out.println("<h4 class=\"headline\">" + escapeHtml(r.getHeadline()) + "</h4>");
                        }
                        out.println("<p>" + escapeHtml(r.getComment() != null ? r.getComment() : "") + "</p>");
                        out.println("</div><hr>");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }

        // ==================== HIỂN THỊ TRANG CHI TIẾT SÁCH ====================
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing book id parameter");
            return;
        }

        try {
            int bookId = Integer.parseInt(idParam);
            Book book = bookServices.getBookById(bookId);

            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Lấy review ban đầu (page 0, size 5)
            List<Review> reviews = reviewServices.getReviewsByBook(bookId, 0);
            long totalReviews = reviewServices.getTotalReviewsByBook(bookId);
            Double avgRating = bookServices.getAverageRating(bookId);
            if (avgRating == null)
                avgRating = 0.0;

            // ==================== LẤY THÔNG TIN CUSTOMER HIỆN TẠI ====================
            HttpSession session = request.getSession();
            Customer currentCustomer = (Customer) session.getAttribute("customer");

            // Kiểm tra xem customer đã review sách này chưa (nếu đã login)
            Review customerReview = null;
            if (currentCustomer != null) {
                customerReview = reviewServices.getCustomerReviewForBook(
                        currentCustomer.getUserId(), bookId);
            }

            // Đặt các attribute cho JSP
            request.setAttribute("book", book);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("reviews", reviews != null ? reviews : new java.util.ArrayList<>());
            request.setAttribute("totalReviews", totalReviews);
            request.setAttribute("loadedCount", reviews != null ? reviews.size() : 0);

            request.setAttribute("currentCustomer", currentCustomer); // ← Để JSP biết có đang login không
            request.setAttribute("customerReview", customerReview); // ← Đã review chưa + nội dung nếu có

            // Restore customer from JWT for header display
            com.bookstore.model.Customer customer = (com.bookstore.model.Customer) session.getAttribute("customer");
            if (customer == null) {
                customer = com.bookstore.service.JwtAuthHelper.restoreCustomerFromJwt(
                        request, session, com.bookstore.data.DBUtil.getEmFactory());
            }

            // Load categories for header
            com.bookstore.service.CustomerServices customerServices = new com.bookstore.service.CustomerServices();
            request.setAttribute("listCategories", customerServices.listAllCategories());

            // Forward đến book_detail.jsp
            request.getRequestDispatcher("/customer/book_detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while loading book details: " + e.getMessage());
        }
    }

    private String escapeHtml(String input) {
        if (input == null)
            return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}