package com.ms3.shippingservice.infrastructure.persistency.entity;

import com.ms3.shippingservice.domain.model.ShippingState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_state_history")
@Getter
@Setter
public class ShippingStateHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "changed_state", nullable = false)
    private ShippingState state;

    @Column(name = "changed_by", nullable = false, length = 150)
    private String changedBy; // Almacena el usuario del token

    @Column(name = "change_timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", nullable = false)
    private ShippingEntity shipping;
}
