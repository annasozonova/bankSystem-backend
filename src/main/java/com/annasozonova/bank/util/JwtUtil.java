package com.annasozonova.bank.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Utility for generating and validating JWT tokens using HMAC SHA-512.
 * Stores user ID as the subject.
 */
@Component
public class JwtUtil {

    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expirationMs}") long jwtExpirationMs) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generates a signed JWT token with the user ID as the subject.
     *
     * @param userId authenticated user ID
     * @return signed JWT token
     */
    public String generateToken(UUID userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the user ID from the subject field of a valid JWT token.
     *
     * @param token JWT token
     * @return user ID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Validates the given JWT token's signature and expiration.
     *
     * @param token JWT token
     * @return true if token is valid; false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}