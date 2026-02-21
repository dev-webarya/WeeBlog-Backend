package com.blogapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility to generate and validate JWT tokens for user authentication.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expiryMs;

    public JwtTokenProvider(
            @Value("${blog.jwt.secret}") String base64Secret,
            @Value("${blog.jwt.expiry-hours:24}") int expiryHours) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expiryMs = (long) expiryHours * 60 * 60 * 1000;
    }

    /**
     * Generate a JWT containing userId and email.
     */
    public String generateToken(String userId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiryMs))
                .signWith(key)
                .compact();
    }

    /**
     * Extract the user ID (subject) from a valid token.
     * Returns null if the token is invalid or expired.
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Validate the token signature and expiry.
     */
    public boolean isValid(String token) {
        return getUserIdFromToken(token) != null;
    }
}
