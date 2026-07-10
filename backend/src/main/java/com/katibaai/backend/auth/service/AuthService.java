package com.katibaai.backend.auth.service;

import com.katibaai.backend.auth.dto.*;
import com.katibaai.backend.auth.entity.RefreshToken;
import com.katibaai.backend.auth.exception.*;
import com.katibaai.backend.auth.repository.RefreshTokenRepository;
import com.katibaai.backend.user.entity.User;
import com.katibaai.backend.user.enums.Role;
import com.katibaai.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-days}")
    private int refreshDays;

    // create a new user
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return issueAuthResponse(user);
    }

    // authenticate an existing user
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return issueAuthResponse(user);
    }

    // get a new access token
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken existing = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (existing.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }
        if (existing.getExpiryDate().isBefore(OffsetDateTime.now())) {
            throw new RefreshTokenExpiredException("Refresh token has expired, please log in again");
        }

        User user = existing.getUser();

        // rotation: replace the token value + expiry on the same row
        existing.setToken(UUID.randomUUID().toString());
        existing.setExpiryDate(OffsetDateTime.now().plusDays(refreshDays));
        refreshTokenRepository.save(existing);

        String newAccessToken = jwtService.generateToken(user.getEmail());

        return AuthResponse.success(
                newAccessToken,
                existing.getToken(),
                jwtService.getExpiration(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private AuthResponse issueAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = createOrReplaceRefreshToken(user);

        return AuthResponse.success(
                token,
                refreshToken.getToken(),
                jwtService.getExpiration(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private RefreshToken createOrReplaceRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(RefreshToken.builder().user(user).build());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(OffsetDateTime.now().plusDays(refreshDays));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    // logout a user
    @Transactional
    public void logout(RefreshRequest request) {
        RefreshToken existing = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
    }
}