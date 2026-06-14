package com.ms3.shippingservice.infrastructure.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrors = new HashMap<>();
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        for(FieldError error: errors) {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();
            fieldErrors
                    .computeIfAbsent(field, k->new ArrayList<>())
                    .add(defaultMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Error en validacion", fieldErrors));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException e) {
        String errorMessage = "Error en la comunicación con el servicio externo.";

        if (e.responseBody().isPresent()) {
            ByteBuffer body = e.responseBody().get();
            String bodyStr = StandardCharsets.UTF_8.decode(body).toString();

            if (bodyStr.contains("\"message\":\"")) {
                errorMessage = bodyStr.substring(bodyStr.indexOf("\"message\":\"") + 11, bodyStr.lastIndexOf("\""));
                errorMessage = errorMessage.split("\"")[0];
            }
        } else {
            errorMessage = e.getMessage();
        }
        ApiResponse<Void> response = new ApiResponse<>(false, errorMessage,null);

        HttpStatus status = HttpStatus.resolve(e.status());
        return new ResponseEntity<>(response, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(ExternalServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY) // significa servicio externo fallo
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(ServiceUnavailableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE); // HTTP 503
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "El formato del JSON es incorrecto o contiene valores no permitidos.";
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            Class<?> targetType = cause.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                Object[] enumConstants = targetType.getEnumConstants();
                String allowedValues = String.join(", ", Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .toArray(String[]::new));
                message = String.format("El valor '%s' no es válido. Los valores permitidos son: [%s]",
                        cause.getValue(), allowedValues);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "El valor proporcionado es inválido para el campo '" + ex.getName() + "'.";

        // mostramos los valores permitidos
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            java.util.StringJoiner joiner = new java.util.StringJoiner(", ");

            for (Object constant : ex.getRequiredType().getEnumConstants()) {
                joiner.add(constant.toString());
            }

            String allowedValues = joiner.toString();

            message = String.format("El valor '%s' no es válido para '%s'. Los valores permitidos son: [%s]",
                    ex.getValue(), ex.getName(), allowedValues);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, null));
    }

}
