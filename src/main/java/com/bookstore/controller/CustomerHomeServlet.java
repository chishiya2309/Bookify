package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// URL Pattern rỗng "" nghĩa là trang chủ gốc (ví dụ: localhost:8080/Bookify/)
@WebServlet("")
public class CustomerHomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Gọi Service (đã khớp tên file CustomerServices.java trong project của bạn)
    private final CustomerServices customerService = new CustomerServices();

    public CustomerHomeServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 0. Kiểm tra trạng thái đăng nhập từ JWT token
        checkLoginStatus(request);
        
        // 1. Lấy dữ liệu từ Service
        List<Book> listNewBooks = customerService.listNewBooks();
        List<Book> listBestSellingBooks = customerService.listBestSellingBooks();
        List<Book> listFavoredBooks = customerService.listMostFavoredBooks();
        List<Category> listCategories = customerService.listAllCategories();

        // 2. Đẩy dữ liệu ra Request để JSP hiển thị
        request.setAttribute("listNewBooks", listNewBooks);
        request.setAttribute("listBestSellingBooks", listBestSellingBooks);
        request.setAttribute("listFavoredBooks", listFavoredBooks);
        request.setAttribute("listCategories", listCategories);

        // 3. Forward sang trang JSP
        // ĐƯỜNG DẪN QUAN TRỌNG: Phải khớp với thư mục 'customer' trong ảnh bạn gửi
        String homepage = "customer/CustomerHomePage.jsp"; 
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(homepage);
        dispatcher.forward(request, response);
    }
    
    /**
     * Kiểm tra JWT token và set thông tin user vào request attribute
     */
    private void checkLoginStatus(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (token != null && JwtUtil.validateToken(token)) {
            try {
                String email = JwtUtil.extractEmail(token);
                String role = JwtUtil.extractRole(token);
                
                // Set thông tin user vào request để JSP sử dụng
                request.setAttribute("isLoggedIn", true);
                request.setAttribute("userEmail", email);
                request.setAttribute("userRole", role);
            } catch (Exception e) {
                // Token không hợp lệ, user chưa đăng nhập
                request.setAttribute("isLoggedIn", false);
            }
        } else {
            request.setAttribute("isLoggedIn", false);
        }
    }
    
    /**
     * Lấy JWT token từ cookie
     */
    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}