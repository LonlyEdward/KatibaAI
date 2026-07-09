package com.katibaai.backend.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.katibaai.backend.user.entity.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}