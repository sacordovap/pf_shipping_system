package com.ms1.authservice.infraestructure.persistency.repository;

import com.ms1.authservice.infraestructure.persistency.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleName(String roleName);

}