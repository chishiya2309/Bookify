package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.service.BookServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/view_book")  // ← ĐÃ SỬA: từ "/book_detail" thành "/view_book"
public class BookDetailServlet extends HttpServlet {  // Giữ nguyên tên class

    private final BookServices bookServices = new BookServices();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set encoding UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("loadMore".equals(action)) {
            // ← ĐÃ SỬA: dùng "id" thay vì "bookId"
            String idParam = request.getParameter("id");
            String pageParam = request.getParameter("page");

            if (idParam == null || pageParam == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            try {
                int bookId = Integer.parseInt(idParam);
                int page = Integer.parseInt(pageParam);

                List<Review> reviews = bookServices.getReviews(bookId, page);

                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();

                if (reviews != null) {
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
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading reviews");
            }
            return;
        }

        // ← ĐÃ SỬA: dùng "id" thay vì "bookId"
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing book id parameter");
            return;
        }

        try {
            int bookId = Integer.parseInt(idParam);
            Book book = bookServices.getBookById(bookId);

            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found with ID: " + bookId);
                return;
            }

            List<Review> reviews = bookServices.getReviews(bookId, 0);
            long totalReviews = bookServices.getTotalReviews(bookId);
            Double avgRating = bookServices.getAverageRating(bookId);
            
            // Đảm bảo avgRating không null
            if (avgRating == null) {
                avgRating = 0.0;
            }

            request.setAttribute("book", book);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("reviews", reviews != null ? reviews : new java.util.ArrayList<>());
            request.setAttribute("totalReviews", totalReviews);
            request.setAttribute("loadedCount", reviews != null ? reviews.size() : 0);

            // Forward đến book_detail.jsp
            request.getRequestDispatcher("/customer/book_detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading book details: " + e.getMessage());
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}