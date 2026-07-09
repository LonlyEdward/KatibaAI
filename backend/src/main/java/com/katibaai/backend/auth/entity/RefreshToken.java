package com.katibaai.backend.auth.entity;

import com.katibaai.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {


    @Id
    @GeneratedValue
    private UUID id;


    @Column(nullable = false, unique = true, length = 500)
    private String token;


    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    @Column(nullable = false)
    private OffsetDateTime expiryDate;


    @Column(nullable = false)
    private boolean revoked;


    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;


    @PrePersist
    protected void onCreate(){
        createdAt = OffsetDateTime.now();
        revoked = false;
    }
}