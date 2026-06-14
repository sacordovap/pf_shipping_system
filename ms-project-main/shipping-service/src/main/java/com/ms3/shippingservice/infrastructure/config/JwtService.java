package com.ms3.shippingservice.infrastructure.config;


import com.ms3.shippingservice.domain.ports.out.JwtPortOut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService implements JwtPortOut {

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }

    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}