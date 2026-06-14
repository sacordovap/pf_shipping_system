package com.ms2.customerservice.infraestructure.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Exception ex) {
        super(message);
    }

}
