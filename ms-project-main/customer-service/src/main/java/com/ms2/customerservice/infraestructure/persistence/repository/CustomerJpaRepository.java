package com.ms2.customerservice.infraestructure.persistence.repository;

import com.ms2.customerservice.infraestructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByDni(String dni);
    Optional<CustomerEntity> findByEmail(String email);
    List<CustomerEntity> findByActiveTrue();

}