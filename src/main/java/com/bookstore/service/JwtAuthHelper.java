package com.bookstore.service;

import com.bookstore.dao.UserRepository;
import com.bookstore.model.Customer;
import com.bookstore.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

/**
 * Utility class for JWT authentication operations.
 * Centralizes JWT token extraction and customer restoration logic.
 * 
 * Note: This class uses System.out.println for debug logging to maintain
 * consistency with the existing codebase. In production, consider migrating
 * to a proper logging framework like SLF4J.
 */
public class JwtAuthHelper {
    /**
     * Kiểm tra JWT token từ request cookies hoặc Authorization header.
     * 
     * @param request The HTTP request
     * @return JWT token string, hoặc null nếu không tìm thấy
     */
    public static String extractJwtToken(HttpServletRequest request) {
        // Kiểm tra Authorization header first
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

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

    /**
     * Restore Customer from JWT token and update session attributes.
     * Only restores customers with CUSTOMER role (not ADMIN).
     * 
     * @param request The HTTP request
     * @param session The HTTP session
     * @param emf     EntityManagerFactory for database access
     * @return Customer object if successfully restored, null otherwise
     */

    public static Customer restoreCustomerFromJwt(HttpServletRequest request, HttpSession session,
            EntityManagerFactory emf) {
        String token = extractJwtToken(request);

        if (token == null || !JwtUtil.validateToken(token)) {
            return null;
        }

        try {
            String email = JwtUtil.extractEmail(token);
            String role = JwtUtil.extractRole(token);
            // Only restore for CUSTOMER, not ADMIN
            if (!"CUSTOMER".equals(role)) {
                return null;
            }
            // Find customer from database
            EntityManager em = emf.createEntityManager();
            try {
                UserRepository userRepo = new UserRepository(em);
                Optional<User> optionalUser = userRepo.findByEmail(email);

                if (optionalUser.isPresent() && optionalUser.get() instanceof Customer) {
                    Customer customer = (Customer) optionalUser.get();
                    // Restore session attributes
                    session.setAttribute("customer", customer);
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userRole", role);
                    session.setAttribute("userName", customer.getFullName());
                    System.out.println(
                            "[DEBUG] Restored customer from JWT: " + email + ", userId: " + customer.getUserId());
                    return customer;
                }
            } finally {
                em.close();
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Failed to restore customer from JWT: " + e.getMessage());
        }

        return null;

    }

    /**
     * Check login status from JWT token and set request attributes.
     * Sets isLoggedIn, userEmail, and userRole attributes.
     * IMPORTANT: Only sets isLoggedIn=true for CUSTOMER role, not ADMIN.
     * This prevents admin from accessing customer pages.
     * 
     * @param request The HTTP request
     */
    public static void checkLoginStatus(HttpServletRequest request) {
        String token = extractJwtToken(request);

        // Default to false
        request.setAttribute("isLoggedIn", Boolean.FALSE);

        if (token == null || !JwtUtil.validateToken(token))

        {
            return;
        }

        try {
            String email = JwtUtil.extractEmail(token);
            String role = JwtUtil.extractRole(token);

            // Always set user info
            request.setAttribute("userEmail", email);
            request.setAttribute("userRole", role);

            // Only set isLoggedIn=true for CUSTOMER role
            // Admin should not be able to access customer pages
            if ("CUSTOMER".equals(role)) {
                request.setAttribute("isLoggedIn", Boolean.TRUE);
            }
        } catch (Exception e) {
            // Token parsing failed, keep isLoggedIn as false
        }
    }

    /**
     * Check if user is logged in as CUSTOMER from JWT token.
     * This is a lightweight check that doesn't require database access.
     * 
     * @param request The HTTP request
     * @return Customer placeholder if logged in as customer, null otherwise
     */
    public static Customer getCustomerFromJwt(HttpServletRequest request) {
        String token = extractJwtToken(request);

        if (token == null || !JwtUtil.validateToken(token)) {
            return null;
        }

        try {
            String role = JwtUtil.extractRole(token);

            // Only return non-null for CUSTOMER role
            if ("CUSTOMER".equals(role)) {
                // Return a placeholder customer to indicate logged in
                // The actual customer data should be fetched from session or database if needed
                Customer placeholder = new Customer();
                placeholder.setEmail(JwtUtil.extractEmail(token));
                return placeholder;
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Failed to check customer from JWT: " + e.getMessage());
        }

        return null;
    }
}
