package com.ms2.customerservice.domain.ports.out;

public interface JwtPortOut {
    String generateToken(String username, String role);
    String extractEmail(String token);
}
