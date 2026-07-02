package com.ms3.shippingservice.infrastructure.persistency.entity;

import com.ms3.shippingservice.domain.model.ShippingState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shippings")
@Getter
@Setter
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shipping_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 50)
    private String trackingNumber;

    @Column(name = "dni_remitente", nullable = false)
    private String dniRemitente;

    @Column(nullable = false, length = 150)
    private String remitente;

    @Column(name = "dni_destinatario", nullable = false)
    private String dniDestinatario;

    @Column(nullable = false, length = 150)
    private String destinatario;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "origin_branch", nullable = false, length = 100)
    private String originBranch;

    @Column(name = "destination_branch", nullable = false, length = 100)
    private String destinationBranch;

    @Column(nullable = false)
    private double weight;

    @Column(name = "declared_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal declaredValue;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false, length = 30)
    private ShippingState currentState;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    // --- RELACIÓN MUCHOS A MUCHOS  ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "shipping_categories",
            joinColumns = @JoinColumn(name = "shipping_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    // --- HISTORIAL DE ESTADOS ---
    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShippingStateHistoryEntity> stateHistory = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();;

}