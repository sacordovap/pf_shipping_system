package com.ms3.shippingservice.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Shipping {
    private UUID id;
    private String trackingNumber;

    private String dniRemitente;
    private String remitente;
    private String dniDestinatario;
    private String destinatario;

    private String description;
    private String originBranch;      // Lima, Arequipa, Cusco
    private String destinationBranch; // Sucursal destino
    private double weight;            // Peso en kg
    private BigDecimal declaredValue; // Valor declarado del paquete
    private BigDecimal shippingCost;  // Tarifa final calculada de forma automatizada
    private ShippingState currentState;
    private List<Category> categories = new ArrayList<>();
    private List<ShippingStateHistory> stateHistory = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Shipping() {
    }

    // ==========================================
    // CÁLCULO DE TARIFA
    // ==========================================
    public void calculateShippingCost(double baseCost, double weightFactor, double insureance) {
//        double baseCost = 10.0;
        double weightCost = this.weight * weightFactor;

        double insuranceCost = 0.0;
        if (this.declaredValue != null) {
            insuranceCost = this.declaredValue.doubleValue() * insureance;
        }

        double routeCost = 0.0;
        if (this.originBranch != null && !this.originBranch.equalsIgnoreCase(this.destinationBranch)) {
            routeCost = 15.0;
        }

        double total = baseCost + weightCost + insuranceCost + routeCost;
        this.shippingCost = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    // ==========================================
    // MÁQUINA DE ESTADOS
    // ==========================================
    public void updateState(ShippingState newState, String operatorUsername) {

        if (this.currentState == null) {
            throw new IllegalStateException("El envío no cuenta con un estado actual asignado en el sistema.");
        }
        if (this.currentState == newState) {
            return;
        }

        boolean isValid = switch (this.currentState) {
            case REGISTRADO     -> newState == ShippingState.EN_TRANSITO  || newState == ShippingState.ELIMINADO;
            case EN_TRANSITO    -> newState == ShippingState.EN_SUCURSAL || newState == ShippingState.ELIMINADO;
            case EN_SUCURSAL    -> newState == ShippingState.EN_RUTA_ENTREGA || newState == ShippingState.ELIMINADO;
            case EN_RUTA_ENTREGA-> newState == ShippingState.ENTREGADO || newState == ShippingState.REBOTADO  || newState == ShippingState.ELIMINADO;
            case REBOTADO       -> newState == ShippingState.EN_TRANSITO || newState == ShippingState.ELIMINADO;
            case ENTREGADO      -> false;
            case ELIMINADO      -> false;// Estado final inmutable
        };

        if (!isValid) {
            throw new IllegalStateException("Transición de estado inválida desde " + this.currentState + " hacia " + newState);
        }

        this.currentState = newState;
        this.updatedAt = LocalDateTime.now();

        ShippingStateHistory historyEntry = new ShippingStateHistory(
                null,
                newState,
                operatorUsername,
                LocalDateTime.now()
        );
        this.stateHistory.add(historyEntry);
    }

    public void initialize(ShippingState initialState, String operatorUsername) {

        if (this.currentState != null) {
            throw new IllegalStateException("El envío ya tiene un estado asignado.");
        }

        this.currentState = initialState;
        this.updatedAt = LocalDateTime.now();

        if (this.stateHistory == null) {
            this.stateHistory = new ArrayList<>();
        }

        this.stateHistory.add(new ShippingStateHistory(
                null,
                initialState,
                operatorUsername,
                LocalDateTime.now()
        ));
    }

    public void revertState(String operatorUsername) {

        ShippingState targetState = getPreviousState(this.currentState);

        if (targetState == null) {
            throw new IllegalStateException("No existe un estado anterior definido para " + this.currentState);
        }
        if (targetState == ShippingState.ELIMINADO) {
            throw new IllegalStateException("No se encontró el envío ");
        }

        this.currentState = targetState;
        this.updatedAt = LocalDateTime.now();
        this.stateHistory.add(new ShippingStateHistory(
                null,
                targetState,
                "REV-" + operatorUsername,
                LocalDateTime.now()
        ));
    }

    private ShippingState getPreviousState(ShippingState current) {
        return switch (current) {
            case EN_RUTA_ENTREGA -> ShippingState.EN_SUCURSAL;
            case EN_SUCURSAL     -> ShippingState.EN_TRANSITO;
            case EN_TRANSITO     -> ShippingState.REGISTRADO;
            case REBOTADO        -> ShippingState.EN_RUTA_ENTREGA;
            case REGISTRADO      -> null;
            case ENTREGADO       -> null;
            case ELIMINADO       -> null;
        };
    }

}
