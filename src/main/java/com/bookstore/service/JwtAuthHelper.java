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
     * Extract JWT token from request cookies or Authorization header.
     * 
     * @param request The HTTP request
     * @return JWT token string, or null if not found
     */
    public static String extractJwtToken(HttpServletRequest request) {
        // Check Authorization header first
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Check cookies
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
     * @param emf EntityManagerFactory for database access
     * @return Customer object if successfully restored, null otherwise
     */
    public static Customer restoreCustomerFromJwt(HttpServletRequest request, HttpSession session, EntityManagerFactory emf) {
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
                    
                    System.out.println("[DEBUG] Restored customer from JWT: " + email);
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
        
        if (token != null && JwtUtil.validateToken(token)) {
            try {
                String email = JwtUtil.extractEmail(token);
                String role = JwtUtil.extractRole(token);
                
                // Always set user info
                request.setAttribute("userEmail", email);
                request.setAttribute("userRole", role);
                
                // Only set isLoggedIn=true for CUSTOMER role
                // Admin should not be able to access customer pages
                if ("CUSTOMER".equals(role)) {
                    request.setAttribute("isLoggedIn", true);
                } else {
                    // Admin is logged in but should not access customer pages
                    request.setAttribute("isLoggedIn", false);
                }
            } catch (Exception e) {
                // Token invalid, user not logged in
                request.setAttribute("isLoggedIn", false);
            }
        } else {
            request.setAttribute("isLoggedIn", false);
        }
    }
}
