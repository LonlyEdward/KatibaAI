package com.katibaai.backend.auth.service;


import com.katibaai.backend.auth.dto.*;
import com.katibaai.backend.auth.entity.RefreshToken;
import com.katibaai.backend.auth.repository.RefreshTokenRepository;
import com.katibaai.backend.user.entity.User;
import com.katibaai.backend.user.enums.Role;
import com.katibaai.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.OffsetDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;



    public AuthResponse register(RegisterRequest request){


        if(userRepository.existsByEmail(request.email())){
            return AuthResponse.failure(
                    "Email already exists"
            );
        }


        if(userRepository.existsByUsername(request.username())){
            return AuthResponse.failure(
                    "Username already exists"
            );
        }



        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )
                .role(Role.USER)
                .build();



        userRepository.save(user);



        String token =
                jwtService.generateToken(
                        user.getEmail()
                );


        RefreshToken refreshToken =
                createRefreshToken(user);



        return AuthResponse.success(
                token,
                refreshToken.getToken(),
                jwtService.getExpiration(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }





    public AuthResponse login(LoginRequest request){


        User user =
                userRepository
                        .findByEmail(request.email())
                        .orElse(null);



        if(user == null){

            return AuthResponse.failure(
                    "Invalid credentials"
            );
        }



        if(!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )){

            return AuthResponse.failure(
                    "Invalid credentials"
            );
        }



        String token =
                jwtService.generateToken(
                        user.getEmail()
                );



        RefreshToken refreshToken =
                createRefreshToken(user);



        return AuthResponse.success(
                token,
                refreshToken.getToken(),
                jwtService.getExpiration(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }





    private RefreshToken createRefreshToken(User user){


        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(
                                UUID.randomUUID().toString()
                        )
                        .user(user)
                        .expiryDate(
                                OffsetDateTime.now()
                                        .plusDays(7)
                        )
                        .build();



        return refreshTokenRepository.save(
                refreshToken
        );
    }

}