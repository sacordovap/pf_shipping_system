package com.ms3.shippingservice.infrastructure.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Exception ex) {
        super(message);
    }

}
