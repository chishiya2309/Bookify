package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookServices;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper; // Import Helper của bạn
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search_book")
public class SearchBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private BookServices bookServices = new BookServices();
    private CustomerServices customerServices = new CustomerServices();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // --- 1. KIỂM TRA ĐĂNG NHẬP (Dùng JwtAuthHelper) ---
        // Hàm này sẽ tự động set các attribute: "isLoggedIn", "userEmail", "userRole"
        JwtAuthHelper.checkLoginStatus(request);
        
        // --- 2. LẤY DANH MỤC SÁCH CHO HEADER ---

        request.setAttribute("listCategories", customerServices.listCategories());

        // --- 3. XỬ LÝ TÌM KIẾM ---
        String keyword = request.getParameter("keyword");
        List<Book> result = null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            result = bookServices.searchBooks(keyword.trim());
        } else {
            keyword = "";
        }

        request.setAttribute("keyword", keyword);
        request.setAttribute("listResult", result);
        
        // --- 4. CHUYỂN HƯỚNG SANG JSP ---
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/search_result.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}