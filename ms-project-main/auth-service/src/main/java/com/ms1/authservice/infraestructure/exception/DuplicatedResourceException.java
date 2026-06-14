package com.ms1.authservice.infraestructure.exception;

public class DuplicatedResourceException extends RuntimeException {
    public DuplicatedResourceException(String message) {
        super(message);
    }
}
