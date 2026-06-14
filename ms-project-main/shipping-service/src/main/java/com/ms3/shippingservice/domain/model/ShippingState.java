package com.ms3.shippingservice.domain.model;

public enum ShippingState {
    REGISTRADO, EN_TRANSITO, EN_SUCURSAL, EN_RUTA_ENTREGA, ENTREGADO, REBOTADO, ELIMINADO;

    public boolean canTransitionTo(ShippingState newState) {
        return switch (this) {
            case REGISTRADO -> newState == EN_TRANSITO;
            case EN_TRANSITO -> newState == EN_SUCURSAL;
            case EN_SUCURSAL -> newState == EN_RUTA_ENTREGA;
            case EN_RUTA_ENTREGA -> newState == ENTREGADO || newState == REBOTADO;
            case REBOTADO -> newState == EN_TRANSITO;
            case ENTREGADO -> false;
            case ELIMINADO -> false;
        };
    }
}