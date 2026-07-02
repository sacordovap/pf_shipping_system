package com.ms1.authservice.domain.ports.out;

import java.util.UUID;

public interface JwtPortOut {
    String generateToken(UUID userId, String email, String username, String role);
    String extractEmail(String token);
}
