package com.ms3.shippingservice.domain.ports.out;

public interface JwtPortOut {
    String generateToken(String username, String role);
    String extractEmail(String token);
}
