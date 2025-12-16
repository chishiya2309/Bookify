package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Category;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
    private CustomerServices customerService;

    public CustomerHomeServlet() {
        super();
    }


    @Override
    public void init() throws ServletException {
        super.init();
        customerService = new CustomerServices();
    }

    @Override
    public void destroy() {
        // If CustomerServices needs explicit cleanup, do it here.
        // For example: customerService.close(); if such a method exists.
        customerService = null;
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 0. Kiểm tra trạng thái đăng nhập từ JWT token
        JwtAuthHelper.checkLoginStatus(request);
        
        // 0.1. Kiểm tra nếu là ADMIN thì redirect về trang admin
        String userRole = (String) request.getAttribute("userRole");
        if ("ADMIN".equals(userRole)) {
            // Admin không được phép truy cập trang customer
            response.sendRedirect(request.getContextPath() + "/admin/");
            return;
        }
        
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
    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}