package com.katibaai.backend.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean success;

    private String message;

    private String token;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private String username;

    private String email;

    private String role;


    public static AuthResponse success(
            String token,
            String refreshToken,
            Long expiresIn,
            String username,
            String email,
            String role
    ) {
        return AuthResponse.builder()
                .success(true)
                .message("Authentication successful")
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .username(username)
                .email(email)
                .role(role)
                .build();
    }


    public static AuthResponse failure(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}