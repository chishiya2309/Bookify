package com.bookstore.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookstore.dao.UserRepository;
import com.bookstore.model.Customer;
import com.bookstore.model.User;
import com.bookstore.service.EmailService;
import com.bookstore.service.ValidationUtil;
import com.google.gson.Gson;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling forgot password functionality
 * URL: /auth/forgot-password
 */
@WebServlet("/auth/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServlet.class);
    private static final Gson gson = new Gson();
    private static final SecureRandom random = new SecureRandom();
    
    // Store OTP codes with expiration (email -> OtpData)
    private static final ConcurrentHashMap<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    // OTP validity: 5 minutes
    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;
    
    // Rate limiting: max 3 requests per email per 10 minutes
    private static final ConcurrentHashMap<String, RateLimitData> rateLimitStore = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 3;
    private static final long RATE_LIMIT_WINDOW_MS = 10 * 60 * 1000;
    
    private EntityManagerFactory emf;
    private EmailService emailService;
    
    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("bookify_pu");
        emailService = new EmailService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        
        String action = request.getParameter("action");
        
        if (action == null) {
            sendError(response, "Action không hợp lệ");
            return;
        }

        switch (action) {
            case "sendOtp":
                handleSendOtp(request, response);
                break;
            case "verifyOtp":
                handleVerifyOtp(request, response);
                break;
            case "resetPassword":
                handleResetPassword(request, response);
                break;
            default:
                sendError(response, "Action không hợp lệ");
        }
    }
    
    private void handleSendOtp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            sendError(response, "Vui lòng nhập email");
            return;
        }
        
        email = email.trim().toLowerCase();
        
        if (!ValidationUtil.isValidEmail(email)) {
            sendError(response, "Email không hợp lệ");
            return;
        }
        
        // Rate limiting check
        if (isRateLimited(email)) {
            sendError(response, "Bạn đã gửi quá nhiều yêu cầu. Vui lòng thử lại sau 10 phút.");
            return;
        }
        
        // Check if email exists
        EntityManager em = emf.createEntityManager();
        try {
            UserRepository userRepo = new UserRepository(em);
            Optional<User> optionalUser = userRepo.findByEmail(email);
            
            if (optionalUser.isEmpty()) {
                // Don't reveal if email exists or not for security
                sendSuccess(response, "Nếu email tồn tại, mã xác nhận sẽ được gửi đến hộp thư của bạn.");
                return;
            }
            
            User user = optionalUser.get();
            
            // Only allow password reset for Customer, not Admin
            if (!(user instanceof Customer)) {
                sendError(response, "Tài khoản Admin vui lòng liên hệ quản trị viên để đặt lại mật khẩu.");
                return;
            }
            
            // Generate 6-digit OTP
            String otp = generateOtp();
            
            // Store OTP
            otpStore.put(email, new OtpData(otp, System.currentTimeMillis()));
            
            // Update rate limit
            updateRateLimit(email);
            
            // Send email
            boolean sent = emailService.sendOtpEmail(email, otp);
            
            if (sent) {
                logger.info("OTP sent to email: {}", email);
                sendSuccess(response, "Mã xác nhận đã được gửi đến email của bạn.");
            } else {
                sendError(response, "Không thể gửi email. Vui lòng thử lại sau.");
            }
            
        } finally {
            em.close();
        }
    }
    
    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");
        
        if (email == null || otp == null) {
            sendError(response, "Thiếu thông tin");
            return;
        }
        
        email = email.trim().toLowerCase();
        otp = otp.trim();
        
        OtpData otpData = otpStore.get(email);
        
        if (otpData == null) {
            sendError(response, "Mã xác nhận không tồn tại hoặc đã hết hạn. Vui lòng gửi lại mã mới.");
            return;
        }
        
        // Check expiration
        if (System.currentTimeMillis() - otpData.createdAt > OTP_VALIDITY_MS) {
            otpStore.remove(email);
            sendError(response, "Mã xác nhận đã hết hạn. Vui lòng gửi lại mã mới.");
            return;
        }
        
        // Verify OTP
        if (!otpData.code.equals(otp)) {
            sendError(response, "Mã xác nhận không đúng");
            return;
        }
        
        // Mark as verified (keep in store for password reset step)
        otpData.verified = true;
        sendSuccess(response, "Xác nhận thành công");
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");
        
        if (email == null || otp == null || newPassword == null) {
            sendError(response, "Thiếu thông tin");
            return;
        }
        
        email = email.trim().toLowerCase();
        
        // Validate password
        if (newPassword.length() < 6) {
            sendError(response, "Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        
        // Verify OTP again
        OtpData otpData = otpStore.get(email);
        
        if (otpData == null || !otpData.verified || !otpData.code.equals(otp)) {
            sendError(response, "Phiên xác nhận không hợp lệ. Vui lòng thực hiện lại từ đầu.");
            return;
        }
        
        // Check expiration (extended to 10 minutes for password reset step)
        if (System.currentTimeMillis() - otpData.createdAt > OTP_VALIDITY_MS * 2) {
            otpStore.remove(email);
            sendError(response, "Phiên đã hết hạn. Vui lòng thực hiện lại từ đầu.");
            return;
        }
        
        // Update password
        EntityManager em = emf.createEntityManager();
        try {
            UserRepository userRepo = new UserRepository(em);
            Optional<User> optionalUser = userRepo.findByEmail(email);
            
            if (optionalUser.isEmpty()) {
                sendError(response, "Không tìm thấy tài khoản");
                return;
            }
            
            User user = optionalUser.get();
            
            // Hash new password
            String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            
            em.getTransaction().begin();
            user.setPassword(passwordHash);
            em.merge(user);
            em.getTransaction().commit();
            
            // Remove OTP from store
            otpStore.remove(email);
            
            logger.info("Password reset successful for email: {}", email);
            sendSuccess(response, "Đặt lại mật khẩu thành công! Đang chuyển đến trang đăng nhập...");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error("Error resetting password for {}: {}", email, e.getMessage());
            sendError(response, "Đã xảy ra lỗi. Vui lòng thử lại.");
        } finally {
            em.close();
        }
    }
    
    private String generateOtp() {
        int otp = 100000 + random.nextInt(900000); // 6-digit number
        return String.valueOf(otp);
    }
    
    private boolean isRateLimited(String email) {
        RateLimitData data = rateLimitStore.get(email);
        if (data == null) return false;
        
        // Reset if window expired
        if (System.currentTimeMillis() - data.windowStart > RATE_LIMIT_WINDOW_MS) {
            rateLimitStore.remove(email);
            return false;
        }
        
        return data.count >= MAX_REQUESTS;
    }
    
    private void updateRateLimit(String email) {
        rateLimitStore.compute(email, (k, v) -> {
            if (v == null || System.currentTimeMillis() - v.windowStart > RATE_LIMIT_WINDOW_MS) {
                return new RateLimitData();
            }
            v.count++;
            return v;
        });
    }
    
    private void sendSuccess(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
    
    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    // Inner classes for OTP and Rate Limit data
    private static class OtpData {
        String code;
        long createdAt;
        boolean verified;
        
        OtpData(String code, long createdAt) {
            this.code = code;
            this.createdAt = createdAt;
            this.verified = false;
        }
    }
    
    private static class RateLimitData {
        long windowStart;
        int count;
        
        RateLimitData() {
            this.windowStart = System.currentTimeMillis();
            this.count = 1;
        }
    }
}
