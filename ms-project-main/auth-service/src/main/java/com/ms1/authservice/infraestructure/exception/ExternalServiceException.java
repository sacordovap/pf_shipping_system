package com.ms1.authservice.infraestructure.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Exception ex) {
        super(message);
    }

}
