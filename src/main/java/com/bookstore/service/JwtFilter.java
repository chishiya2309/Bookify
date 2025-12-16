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
        "",                              // Root path (trang chủ)
        "/",                             // Root path alternative
        "/customer/CustomerHomePage.jsp", // Trang chủ customer (không cần đăng nhập)
        "/customer/login.jsp",           
        "/customer/register.jsp",
        "/customer/cart.jsp",            // Giỏ hàng JSP (khách có thể xem)
        "/customer/cart",                // Giỏ hàng Servlet (query database)
        "/css/auth-style.css",
        "/admin/AdminLogin.jsp"
    );
    
    // 2. Cập nhật đường dẫn cho Servlet (API) ko cần đăng nhập
    private static final List<String> EXCLUDED_SERVLETS = Arrays.asList(
        "/auth/login",     
        "/auth/register", 
        "/auth/logout",    
        "/auth/refresh",
        "/customer/cart"                 // Cart servlet cho guest
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
            // Token hợp lệ, lấy username và role và set vào request
            String username = JwtUtil.extractUsername(token);
            String role = JwtUtil.extractRole(token);
            httpRequest.setAttribute("username", username);
            httpRequest.setAttribute("userRole", role);

            // Extract roles from token
            List<String> roles = JwtUtil.extractRoles(token); // Assumes roles are stored as a claim in the token

            // Determine required role based on path
            boolean isAdminPath = path.startsWith("/admin");
            boolean isCustomerPath = path.startsWith("/customer");
            boolean isAdmin = roles.contains("ADMIN");
            boolean isCustomer = roles.contains("CUSTOMER");

            boolean hasAccess = false;
            if (isAdminPath && isAdmin) {
                // Admin chỉ được vào trang admin
                hasAccess = true;
            } else if (isCustomerPath && isCustomer) {
                // Customer chỉ được vào trang customer
                hasAccess = true;
            } else if (!isAdminPath && !isCustomerPath) {
                // Các trang khác (shared), cho phép nếu có role
                hasAccess = !roles.isEmpty();
            }

            if (hasAccess) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                
                // Redirect về trang phù hợp với role của user
                if (isAdmin) {
                    // Admin cố vào trang customer -> về dashboard admin
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/dashboard.jsp");
                } else if (isCustomer) {
                    // Customer cố vào trang admin -> về trang customer
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/index.jsp");
                } else {
                    // Không có role hợp lệ -> về login
                    if (isAdminPath) {
                        httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/AdminLogin.jsp");
                    } else {
                        httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/login.jsp");
                    }
                }
            }
        } else {
            // Token không hợp lệ hoặc không tồn tại
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            // Lưu URL gốc để redirect sau khi đăng nhập
            String originalUrl = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                originalUrl += "?" + queryString;
            }
            
            // Encode URL để truyền qua parameter
            String encodedUrl = java.net.URLEncoder.encode(originalUrl, "UTF-8");
            
            // Phân chia luồng: Nếu đang vào Admin thì về AdminLogin, còn lại về Customer Login
            if (path.startsWith("/admin")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/AdminLogin.jsp?redirect=" + encodedUrl);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/login.jsp?redirect=" + encodedUrl);
            }
        }
    }
    
    private boolean shouldExclude(String path) {
        // Kiểm tra exact match
        if (EXCLUDED_PATHS.contains(path) || EXCLUDED_SERVLETS.contains(path)) {
            return true;
        }
        
        // Kiểm tra prefix match (bỏ qua empty string để tránh match tất cả)
        for (String excluded : EXCLUDED_PATHS) {
            if (!excluded.isEmpty() && path.startsWith(excluded)) {
                return true;
            }
        }
        
        for (String excluded : EXCLUDED_SERVLETS) {
            if (!excluded.isEmpty() && path.startsWith(excluded)) {
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