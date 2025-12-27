package com.bookstore.controller.customer;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.bookstore.model.Customer;
import com.bookstore.service.CustomerServices;
import com.bookstore.service.JwtAuthHelper;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/customer/profile")
public class CustomerProfileServlet extends HttpServlet {
    
    private final CustomerServices customerServices;
    private EntityManagerFactory emf;
    
    public CustomerProfileServlet() {
        this.customerServices = new CustomerServices();
    }
    
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("bookify_pu");
    }
    
    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    /**
     * Build redirect URL with current page as redirect parameter
     */
    private String buildLoginRedirectUrl(HttpServletRequest request) {
        String originalUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            originalUrl += "?" + queryString;
        }
        String encodedUrl = URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
        return request.getContextPath() + "/customer/login.jsp?redirect=" + encodedUrl;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try {
            HttpSession session = request.getSession();
            String action = request.getParameter("action");
            System.out.println("[ProfileServlet] Action: " + action);
            
            // Lấy customer từ session
            Customer sessionCustomer = (Customer) session.getAttribute("customer");
            System.out.println("[ProfileServlet] Session customer: " + (sessionCustomer != null ? sessionCustomer.getEmail() : "null"));
            
            // Nếu không có trong session, thử restore từ JWT
            if (sessionCustomer == null) {
                sessionCustomer = JwtAuthHelper.restoreCustomerFromJwt(request, session, emf);
                System.out.println("[ProfileServlet] After JWT restore: " + (sessionCustomer != null ? sessionCustomer.getEmail() : "null"));
            }
            
            if (sessionCustomer == null) {
                System.out.println("[ProfileServlet] No customer found, redirecting to login with redirect back");
                response.sendRedirect(buildLoginRedirectUrl(request));
                return;
            }
            
            Integer userId = sessionCustomer.getUserId();
            Customer customer = customerServices.getCustomerById(userId);
            if (customer == null) {
                System.out.println("[ProfileServlet] Customer not found in DB for userId: " + userId);
                response.sendRedirect(buildLoginRedirectUrl(request));
                return;
            }
            
            request.setAttribute("customer", customer);
            
            // Kiểm tra action
            if ("edit".equals(action)) {
                System.out.println("[ProfileServlet] Forwarding to editprofile.jsp");
                request.getRequestDispatcher("/customer/editprofile.jsp").forward(request, response);
            } else {
                System.out.println("[ProfileServlet] Forwarding to profile.jsp");
                request.getRequestDispatcher("/customer/profile.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("[ProfileServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("/customer/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        
        // Lấy customer từ session
        Customer sessionCustomer = (Customer) session.getAttribute("customer");
        
        // Nếu không có trong session, thử restore từ JWT
        if (sessionCustomer == null) {
            sessionCustomer = JwtAuthHelper.restoreCustomerFromJwt(request, session, emf);
        }
        
        if (sessionCustomer == null) {
            response.sendRedirect(buildLoginRedirectUrl(request));
            return;
        }
        
        Integer userId = sessionCustomer.getUserId();
        String action = request.getParameter("action");
        
        if ("update".equals(action)) {
            updateProfile(request, response, userId);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/profile");
        }
    }
    
    private void updateProfile(HttpServletRequest request, HttpServletResponse response, Integer userId) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        HttpSession session = request.getSession();
        
        // Validate password confirmation if password is provided
        if (password != null && !password.isEmpty() && !password.equals(confirmPassword)) {
            session.setAttribute("error", "Mật khẩu xác nhận không khớp");
            response.sendRedirect(request.getContextPath() + "/customer/profile");
            return;
        }
        
        try {
            customerServices.updateCustomer(userId, fullName, password, phoneNumber);
            session.setAttribute("success", "Cập nhật thông tin thành công");
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/customer/profile");
    }
}
