package com.ms3.shippingservice.infrastructure.persistency.repository;

import com.ms3.shippingservice.infrastructure.persistency.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);
}
