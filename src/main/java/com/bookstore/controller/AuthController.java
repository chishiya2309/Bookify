package com.bookstore.controller;

import com.bookstore.service.JwtUtil;
import com.bookstore.dao.UserRepository;
import com.bookstore.model.User;
import com.bookstore.model.Customer;
import com.bookstore.model.Admin;
import com.google.gson.Gson;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private Gson gson = new Gson();
    private EntityManagerFactory emf;
    
    @Override
    public void init() throws ServletException {
        try {
            emf = Persistence.createEntityManagerFactory("bookify_pu");
        } catch (Exception e) {
            throw new ServletException("Failed to initialize EntityManagerFactory", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        EntityManager em = emf.createEntityManager();
        String email = "unknown";
        
        try {
            email = request.getParameter("email");
            String password = request.getParameter("password");
            
            // Validate input
            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    createErrorResponse("Email và mật khẩu không được để trống"));
                return;
            }
            
            UserRepository userRepo = new UserRepository(em);
            Optional<User> optionalUser = userRepo.findByEmail(email);
            
            if (optionalUser.isEmpty()) {
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    createErrorResponse("Email hoặc mật khẩu không đúng"));
                return;
            }
            
            User user = optionalUser.get();
            
            // Verify password
            if (!BCrypt.checkpw(password, user.getPassword())) {
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    createErrorResponse("Email hoặc mật khẩu không đúng"));
                return;
            }
            
            // Determine user role
            String role;
            if (user instanceof Admin) {
                role = "ADMIN";
            } else if (user instanceof Customer) {
                role = "CUSTOMER";
            } else {
                // Unexpected user type - log and reject
                logger.error("Unexpected user type for email: {}", email);
                sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    createErrorResponse("Lỗi hệ thống: loại người dùng không hợp lệ"));
                return;
            }
            
            // Generate JWT token with role (NO REFRESH TOKEN)
            String accessToken = JwtUtil.generateToken(email, role);
            
            // Set cookie (ONLY access token)
            setCookie(response, "jwt_token", accessToken, 24 * 60 * 60); // 24 hours
            
            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đăng nhập thành công");
            result.put("accessToken", accessToken);
            result.put("email", email);
            result.put("fullName", user.getFullName());
            result.put("userId", user.getUserId());
            
            // Add user type
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                result.put("userType", "CUSTOMER");
                result.put("phoneNumber", customer.getPhoneNumber());
            } else if (user instanceof Admin) {
                result.put("userType", "ADMIN");
            }
            
            sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            
        } catch (Exception e) {
            logger.error("Error during login for email: {}", email, e);
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                createErrorResponse("Đã xảy ra lỗi hệ thống"));
        } finally {
            em.close();
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    
    EntityManager em = emf.createEntityManager();
    String email = "unknown";
    
    try {
        // Get parameters
        email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        
        // Validate required fields
        if (email == null || password == null || fullName == null || phoneNumber == null ||
            email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                createErrorResponse("Vui lòng điền đầy đủ thông tin bắt buộc"));
            return;
        }
        
        UserRepository userRepo = new UserRepository(em);
        
        // Check if email already exists
        if (userRepo.existsByEmail(email)) {
            sendJsonResponse(response, HttpServletResponse.SC_CONFLICT,
                createErrorResponse("Email đã được sử dụng"));
            return;
        }
        
        // Check if phone already exists
        if (userRepo.existsByPhoneNumber(phoneNumber)) {
            sendJsonResponse(response, HttpServletResponse.SC_CONFLICT,
                createErrorResponse("Số điện thoại đã được sử dụng"));
            return;
        }
        
        // Validate password strength
        if (password.length() < 6) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                createErrorResponse("Mật khẩu phải có ít nhất 6 ký tự"));
            return;
        }
        
        // Hash password
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Create customer
        Customer customer = new Customer(email, passwordHash, fullName, phoneNumber);
        
        // Save to database
        em.getTransaction().begin();
        userRepo.saveCustomer(customer);
        em.getTransaction().commit();
        
        // ❌ REMOVED: Auto-login logic
        // ❌ REMOVED: Generate tokens
        // ❌ REMOVED: Set cookies
        
        // ✅ Send success response WITHOUT tokens
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        result.put("email", email);  // Send email để có thể pre-fill login form
        
        sendJsonResponse(response, HttpServletResponse.SC_CREATED, result);
        
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        logger.error("Error during registration for email: {}", email, e);
        sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            createErrorResponse("Đã xảy ra lỗi hệ thống"));
    } finally {
        em.close();
    }
}
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // Simply clear cookie (NO database operation)
            setCookie(response, "jwt_token", "", 0);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đăng xuất thành công");
            
            sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            
        } catch (Exception e) {
            logger.error("Error during logout", e);
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                createErrorResponse("Đã xảy ra lỗi hệ thống"));
        }
    }
    
    // Helper methods
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
    
    private void sendJsonResponse(HttpServletResponse response, int status, Object data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }
    
    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}