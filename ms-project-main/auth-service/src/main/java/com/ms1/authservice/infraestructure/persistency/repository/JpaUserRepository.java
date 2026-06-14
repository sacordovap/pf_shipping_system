package com.ms1.authservice.infraestructure.persistency.repository;

import com.ms1.authservice.infraestructure.persistency.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    // LOGIN Y REGISTRO
    Optional<UserEntity> findByEmail(String email);

    // Validar si un email
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}