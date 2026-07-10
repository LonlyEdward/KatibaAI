package com.katibaai.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;

    private SecretKey signingKey;

    @PostConstruct
    private void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("app.jwt.secret is not configured (set JWT_SECRET env var)");
        }
        byte[] keyBytes = secretKey.getBytes();
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes for HS256 (got " + keyBytes.length + ")"
            );
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    //generate access token
    public String generateToken(String email) {
        Date now = new Date();
        long expirationMillis = accessTokenExpirationMinutes * 60_000;
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiration() {
        return accessTokenExpirationMinutes * 60;
    }
}