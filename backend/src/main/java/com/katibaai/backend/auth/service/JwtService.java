package com.katibaai.backend.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {


    private final String SECRET_KEY = "";


    private final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );
    }


    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + ACCESS_TOKEN_EXPIRATION)
                )
                .signWith(getSigningKey())
                .compact();
    }


    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    public boolean isTokenValid(String token, String email) {

        String tokenEmail = extractEmail(token);

        return tokenEmail.equals(email)
                && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token){

        return extractExpiration(token)
                .before(new Date());
    }


    private Date extractExpiration(String token){

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }


    private <T> T extractClaim(
            String token,
            Function<Claims,T> resolver
    ){

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }


    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public long getExpiration(){

        return ACCESS_TOKEN_EXPIRATION;
    }
}