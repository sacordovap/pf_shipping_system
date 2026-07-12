package com.ms3.shippingservice.infrastructure.persistency.repository;

import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaShippingRepositoryFilter extends JpaRepository<ShippingEntity, UUID>, JpaSpecificationExecutor<ShippingEntity> {
}