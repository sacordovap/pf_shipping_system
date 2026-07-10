package com.ms3.shippingservice.infrastructure.exception;

public class InvalidTrackingFormatException extends RuntimeException {
    public InvalidTrackingFormatException(String message) {
        super(message);
    }
}
