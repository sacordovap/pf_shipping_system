package com.ms1.authservice.domain.ports.out;

public interface JwtPortOut {
    String generateToken(String email, String username,String role);
    String extractEmail(String token);
}
