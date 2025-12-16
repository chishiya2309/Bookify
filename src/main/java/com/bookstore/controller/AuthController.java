package com.bookstore.controller;

import com.bookstore.service.JwtUtil;
import com.bookstore.service.JwtAuthHelper;
import com.bookstore.service.ValidationUtil;
import com.bookstore.service.AppConfig;
import com.bookstore.service.ShoppingCartServices;
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
    private ShoppingCartServices cartService;
    private static final int LOCK_STRIPE_SIZE = 256;
    private static final Object[] sessionLocks = new Object[LOCK_STRIPE_SIZE];
    
    static {
        for (int i = 0; i < LOCK_STRIPE_SIZE; i++) {
            sessionLocks[i] = new Object();
        }
    }
    
    private Object getLockForEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        // Use bitwise AND to ensure positive hash value, avoiding Integer.MIN_VALUE issue
        int hash = (email.hashCode() & 0x7FFFFFFF) % LOCK_STRIPE_SIZE;
        return sessionLocks[hash];
    }
    
    @Override
    public void init() throws ServletException {
        try {
            emf = Persistence.createEntityManagerFactory("bookify_pu");
            cartService = new ShoppingCartServices();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize EntityManagerFactory", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Handle GET requests - only logout is supported via GET
        switch (pathInfo) {
            case "/logout":
                handleLogoutRedirect(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, 
                    "GET method is not supported for this endpoint");
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
            
            // Validate email format
            if (!ValidationUtil.isValidEmail(email)) {
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    createErrorResponse("Định dạng email không hợp lệ"));
                return;
            }
            
            // Check for SQL injection patterns
            if (ValidationUtil.containsSqlInjection(email) || ValidationUtil.containsSqlInjection(password)) {
                logger.warn("Potential SQL injection attempt detected during login");
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    createErrorResponse("Dữ liệu đầu vào không hợp lệ"));
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
            
            // Lưu thông tin user vào session với xử lý race condition
            // Sử dụng lock striping để chỉ lock cho cùng user đăng nhập đồng thời
            HttpSession session;
            synchronized (getLockForEmail(email)) {
                // Get or create session
                session = request.getSession(true);
                
                // Change session ID to prevent session fixation attack
                // This is better than invalidate() as it preserves session data if needed
                request.changeSessionId();
                
                // Set các thuộc tính session cơ bản
                session.setAttribute("userEmail", email);
                session.setAttribute("userRole", role);
                session.setAttribute("userName", user.getFullName());
                
                // Set thuộc tính theo loại user
                if (user instanceof Customer) {
                    Customer customer = (Customer) user;
                    session.setAttribute("customer", customer);
                    
                    // Merge guest cart với user cart khi đăng nhập (trong synchronized block)
                    ShoppingCartServlet.mergeCartOnLogin(session, customer, cartService);
                } else if (user instanceof Admin) {
                    session.setAttribute("admin", user);
                }
            }
            
            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đăng nhập thành công");
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
        
        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                createErrorResponse("Định dạng email không hợp lệ"));
            return;
        }
        
        // Check for SQL injection patterns
        if (ValidationUtil.containsSqlInjection(email) || ValidationUtil.containsSqlInjection(password) ||
            ValidationUtil.containsSqlInjection(fullName) || ValidationUtil.containsSqlInjection(phoneNumber)) {
            logger.warn("Potential SQL injection attempt detected during registration");
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                createErrorResponse("Dữ liệu đầu vào không hợp lệ"));
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
        if (!ValidationUtil.isValidPasswordBasic(password)) {
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
            // Clear JWT cookie
            setCookie(response, "jwt_token", "", 0);
            
            // Xóa session attributes (giữ lại guest cart nếu có)
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("customer");
                session.removeAttribute("admin");
                session.removeAttribute("userEmail");
                session.removeAttribute("userRole");
                session.removeAttribute("userName");
            }
            
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
    
    // Handle logout via GET request (from clicking logout link) - redirects appropriately
    private void handleLogoutRedirect(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            // Lấy role trước khi xóa token để biết redirect về đâu
            String token = JwtAuthHelper.extractJwtToken(request);
            String role = null;
            if (token != null) {
                role = JwtUtil.extractRole(token);
            }
            
            // Clear JWT cookie
            setCookie(response, "jwt_token", "", 0);
            
            // Xóa session attributes (giữ lại guest cart nếu có)
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("customer");
                session.removeAttribute("admin");
                session.removeAttribute("userEmail");
                session.removeAttribute("userRole");
                session.removeAttribute("userName");
            }
            
            // Redirect về trang phù hợp với role
            if ("ADMIN".equals(role)) {
                // Admin đăng xuất → về trang login admin
                response.sendRedirect(request.getContextPath() + "/admin/AdminLogin.jsp");
            } else {
                // Customer đăng xuất → về trang chủ
                response.sendRedirect(request.getContextPath() + "/");
            }
            
        } catch (Exception e) {
            logger.error("Error during logout redirect", e);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    
    // Helper methods
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(AppConfig.isSecureCookiesEnabled());
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