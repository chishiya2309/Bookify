package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.model.Review;
import com.bookstore.service.AdminServices;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/")
public class AdminHomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Khởi tạo Service (Service này sẽ gọi DAO Static)
    private final AdminServices adminService = new AdminServices();

    public AdminHomeServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 2. Lấy action từ tham số URL 
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "dashboard"; // Mặc định vào Dashboard
        }

        String url = "/admin/AdminHomePage.jsp"; 

        try {
    
            switch (action) {
                case "dashboard":
                    showDashboard(request, response);
                    url = "/admin/AdminHomePage.jsp";
                    break;
                
                default:
                    showDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }


    private void showDashboard(HttpServletRequest request, HttpServletResponse response) {
        
        // --- 1. Lấy Danh Sách (Recent Lists) ---
        // findRecentSales() trong DAO đã JOIN FETCH Customer và OrderDetail
        List<Order> recentSales = adminService.findRecentSales();
        
        // findRecentReviews() trong DAO đã JOIN FETCH Customer và Book
        List<Review> recentReviews = adminService.findRecentReviews();
        
        // --- 2. Lấy Số Liệu Thống Kê 
        long totalUsers = adminService.countAllUsers();
        long totalBooks = adminService.countAllBooks();
        long totalCustomers = adminService.countAllCustomers();
        long totalReviews = adminService.countAllReviews();
        long totalOrders = adminService.countAllOrders();

        // - 3. Đẩy dữ liệu vào Request để JSP hiển thị ---
        request.setAttribute("listMostRecentSales", recentSales);
        request.setAttribute("listMostRecentReviews", recentReviews);
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalReviews", totalReviews);
        request.setAttribute("totalOrders", totalOrders);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Chuyển mọi request GET về POST để xử lý tập trung
        doPost(request, response);
    }
}