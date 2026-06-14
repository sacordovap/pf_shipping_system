package com.ms3.shippingservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShippingStateHistory {
    private UUID id;
    private ShippingState state;
    private String changedBy;
    private LocalDateTime timestamp;

    public ShippingStateHistory() {}

    public ShippingStateHistory(UUID id, ShippingState state, String changedBy, LocalDateTime timestamp) {
        this.id = id;
        this.state = state;
        this.changedBy = changedBy;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ShippingState getState() { return state; }
    public void setState(ShippingState state) { this.state = state; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}