package com.bookstore.service;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@WebFilter("/*")
public class JwtFilter implements Filter {
    
    // Compiled regex patterns for redirect URL validation (for efficiency)
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+]*:.*");
    private static final Pattern BACKSLASH_AT_PATTERN = Pattern.compile("^/[\\\\@].*");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^/[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(?:/.*)?$");
    
    // 1. Cập nhật đường dẫn cho chạy ko cần đăng nhập
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "",
        "/",                             // Root path
        "/customer/CustomerHomePage.jsp", // Trang chủ customer (không cần đăng nhập)
        "/customer/book_detail.jsp",     // Trang chi tiết sách (không cần đăng nhập)
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
        "/customer/cart",                // Cart servlet cho guest
        "/view_book"                     // Xem chi tiết sách (không cần đăng nhập)
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
        
        // Bỏ qua các URL không cần xác thực (kiểm tra TRƯỚC khi kiểm tra token)
        // Đảm bảo path "/" luôn được phép truy cập để hiển thị CustomerHomePage
        if (shouldExclude(path)) {
            // Lấy token để kiểm tra nếu là ADMIN thì redirect
            String token = extractToken(httpRequest);
            
            // Kiểm tra nếu là ADMIN cố vào trang customer hoặc trang chủ
            if (token != null && JwtUtil.validateToken(token)) {
                String role = JwtUtil.extractRole(token);
                // Nếu là admin và đang vào trang customer hoặc trang chủ, redirect về admin
                if ("ADMIN".equals(role) && (path.equals("/") || path.startsWith("/customer/"))) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/");
                    return;
                }
            }
            
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
            
            // Validate redirect URL to prevent open redirect vulnerability
            String contextPath = httpRequest.getContextPath();
            String redirectParam = "";
            if (isValidRedirectUrl(originalUrl, contextPath)) {
                // Only include redirect parameter if URL is valid
                String encodedUrl = java.net.URLEncoder.encode(originalUrl, "UTF-8");
                redirectParam = "?redirect=" + encodedUrl;
            }
            
            // Phân chia luồng: Nếu đang vào Admin thì về AdminLogin, còn lại về Customer Login
            if (path.startsWith("/admin")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/AdminLogin.jsp" + redirectParam);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/login.jsp" + redirectParam);
            }
        }
    }
    
    private boolean shouldExclude(String path) {
        // Kiểm tra exact match
        if (EXCLUDED_PATHS.contains(path) || EXCLUDED_SERVLETS.contains(path)) {
            return true;
        }
        
        // Kiểm tra prefix match (chỉ với excluded paths có chiều dài > 1)
        for (String excluded : EXCLUDED_PATHS) {
            if (excluded.length() > 1 && path.startsWith(excluded)) {
                return true;
            }
        }
        
        for (String excluded : EXCLUDED_SERVLETS) {
            if (excluded.length() > 1 && path.startsWith(excluded)) {
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
    
    /**
     * Validates that a redirect URL is a safe internal path.
     * Prevents open redirect vulnerabilities by ensuring the URL:
     * - Is a relative URL (starts with / but not //)
     * - Does not contain a protocol scheme at the beginning
     * - Starts with the application context path or is root
     * 
     * @param url The URL to validate
     * @param contextPath The application context path
     * @return true if the URL is a valid internal redirect, false otherwise
     */
    private boolean isValidRedirectUrl(String url, String contextPath) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        // Must be a relative URL (starts with / but not //)
        if (!url.startsWith("/") || url.startsWith("//")) {
            return false;
        }
        
        // Must not contain protocol scheme at the beginning (http://, https://, javascript:, etc.)
        // Use precompiled pattern for efficiency
        if (PROTOCOL_PATTERN.matcher(url).matches()) {
            return false;
        }
        
        // Handle empty context path case
        String appContextPath = (contextPath != null) ? contextPath : "";
        
        // Validate context path format if not empty
        if (!appContextPath.isEmpty() && !appContextPath.startsWith("/")) {
            // Invalid context path format, reject for safety
            return false;
        }
        
        // Must start with context path or be root
        if (!appContextPath.isEmpty()) {
            // When contextPath is set, URL must be root or start with contextPath
            if (!url.equals("/") && !url.startsWith(appContextPath + "/")) {
                return false;
            }
        } else {
            // When contextPath is empty, enforce additional validation
            // URL must not look like an external redirect to a domain
            // Check for patterns like '//domain.com', '/\domain.com', or '/@domain.com'
            // which are common bypass techniques for redirect vulnerabilities
            if (BACKSLASH_AT_PATTERN.matcher(url).matches()) {
                return false;
            }
            
            // Check for simple domain-like patterns (e.g., '/evil.com' or '/evil.com/path')
            // This pattern is more targeted to avoid rejecting valid paths with multiple dots
            if (DOMAIN_PATTERN.matcher(url).matches()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void destroy() {
    }
}