package com.bookstore.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {
    
    // IMPORTANT: Change this in production! Use environment variable
    private static final String SECRET_KEY = "your-very-secure-256-bit-secret-key-change-this-in-production-please-make-it-long-enough";
    
    // Token validity: 24 hours
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    
    // Refresh token validity: 7 days
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;
    
    private static Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Generate token for user (email-based)
    public static String generateToken(String email) {
        return createToken(new HashMap<>(), email, JWT_TOKEN_VALIDITY);
    }
    
    // Generate token with role
    public static String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email, JWT_TOKEN_VALIDITY);
    }
    
    // Generate token with custom claims
    public static String generateToken(String email, Map<String, Object> claims) {
        return createToken(claims, email, JWT_TOKEN_VALIDITY);
    }
    
    // Generate refresh token
    public static String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, email, REFRESH_TOKEN_VALIDITY);
    }
    
    // Create token
    private static String createToken(Map<String, Object> claims, String subject, long validity) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Extract email from token
    public static String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Alias for backward compatibility
    public static String extractUsername(String token) {
        return extractEmail(token);
    }
    
    // Extract role from token
    public static String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Extract expiration date
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // Extract claim
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    // Extract all claims
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // Check if token is expired
    public static Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    // Validate token with email
    public static Boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Validate token (general)
    public static Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Refresh token
    public static String refreshToken(String refreshToken) {
        if (validateToken(refreshToken)) {
            String email = extractEmail(refreshToken);
            return generateToken(email);
        }
        return null;
    }
}