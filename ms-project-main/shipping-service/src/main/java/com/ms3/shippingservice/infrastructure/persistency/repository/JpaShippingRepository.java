package com.ms3.shippingservice.infrastructure.persistency.repository;

import com.ms3.shippingservice.domain.model.ShippingState;
import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingEntity;
import feign.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaShippingRepository extends JpaRepository<ShippingEntity, UUID> {

    Optional<ShippingEntity> findByTrackingNumber(String trackingNumber);

    // Filtro combinado por sucursal y estado actual
    @Query(value = "SELECT * FROM shippings s WHERE (s.origin_branch = :branch OR s.destination_branch = :branch) AND s.current_state = :state ORDER BY s.created_at DESC", nativeQuery = true)
    List<ShippingEntity> findByBranchAndState(@Param("branch") String branch, @Param("state") String state);

    // Búsqueda por texto parcial del número de seguimiento
    @Query(value = "SELECT * FROM shippings s WHERE LOWER(s.tracking_number) LIKE LOWER(CONCAT('%', :term, '%')) ORDER BY s.created_at DESC", nativeQuery = true)
    List<ShippingEntity> searchByTrackingPartial(@Param("term") String term);

    // Filtro combinado por categoria y estado actual
    @Query("SELECT DISTINCT s FROM ShippingEntity s JOIN s.categories c WHERE c.name = :categoryName AND s.currentState = :state ORDER BY s.createdAt DESC" )
    List<ShippingEntity> findByStateAndCategory(@Param("state") ShippingState state, @Param("categoryName") String categoryName);

    // Filtro para busqueda de nombre en remitente o destinatario
    @Query("SELECT s FROM ShippingEntity s " +
            "WHERE LOWER(s.remitente) LIKE CONCAT('%', LOWER(:name), '%') " +
            "OR LOWER(s.destinatario) LIKE CONCAT('%', LOWER(:name), '%') ORDER BY s.createdAt DESC")
    List<ShippingEntity> findByName(@Param("name") String name);

    List<ShippingEntity> findAllByOrderByCreatedAtDesc();

    Page<ShippingEntity> findAll(Pageable pageable);

    @Query("SELECT s FROM ShippingEntity s WHERE s.createdBy = :userId ORDER BY s.createdAt DESC")
    List<ShippingEntity> findShippingsByUserOrdered(@Param("userId") UUID userId);

}
