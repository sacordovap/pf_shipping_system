package com.ms3.shippingservice.infrastructure.dto.request;

import com.ms3.shippingservice.domain.model.ShippingState;
import jakarta.validation.constraints.NotNull;

public class UpdateStateRequest {

    @NotNull(message = "El nuevo estado del envío es obligatorio.")
    private ShippingState newState;

    public ShippingState getNewState() { return newState; }
    public void setNewState(ShippingState newState) { this.newState = newState; }
}