package com.bookstore.service;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class JwtFilter implements Filter {
    
    // 1. Cập nhật đường dẫn cho chạy ko cần đăng nhập
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/customer/login.jsp",      // Đã sửa đúng
        "/customer/register.jsp",   // Đã sửa đúng
        "/css/auth-style.css",
        "/admin/AdminLogin.jsp"
    );
    
    // 2. Cập nhật đường dẫn cho Servlet (API) ko cần dăng nhập
    private static final List<String> EXCLUDED_SERVLETS = Arrays.asList(
        "/auth/login",     
        "/auth/register", 
        "/auth/logout",    
        "/auth/refresh"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        
        // Bỏ qua các static resources
        if (path.startsWith("/css/") || path.startsWith("/js/") || 
            path.startsWith("/images/") || path.startsWith("/resources/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Bỏ qua các URL không cần xác thực
        if (shouldExclude(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Lấy token từ header hoặc cookie
        String token = extractToken(httpRequest);
        
        if (token != null && JwtUtil.validateToken(token)) {
            // Token hợp lệ, lấy username và set vào request
            String username = JwtUtil.extractUsername(token);
            httpRequest.setAttribute("username", username);
            chain.doFilter(request, response);
        } else {
            // Token không hợp lệ hoặc không tồn tại
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            // --- ĐÂY LÀ PHẦN BẠN CẦN SỬA ---
            // Phân chia luồng: Nếu đang vào Admin thì về AdminLogin, còn lại về Customer Login
            if (path.startsWith("/admin")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/AdminLogin.jsp");
            } else {
                // SỬA TỪ "/login.jsp" THÀNH "/customer/login.jsp"
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/login.jsp");
            }
        }
    }
    
    private boolean shouldExclude(String path) {
        // Kiểm tra exact match
        if (EXCLUDED_PATHS.contains(path) || EXCLUDED_SERVLETS.contains(path)) {
            return true;
        }
        
        // Kiểm tra prefix match
        for (String excluded : EXCLUDED_PATHS) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        
        for (String excluded : EXCLUDED_SERVLETS) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        
        return false;
    }
    
    private String extractToken(HttpServletRequest request) {
        // Ưu tiên lấy từ Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Nếu không có, lấy từ cookie
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }

    @Override
    public void destroy() {
    }
}