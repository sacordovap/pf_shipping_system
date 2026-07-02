package com.ms3.shippingservice.infrastructure.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SecurityUtils {


    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof Claims claims) {
            String userIdStr = claims.get("userId", String.class);
            if (userIdStr != null) {
                System.out.print("MI UUID0: " + userIdStr);
                return UUID.fromString(userIdStr);
            }
        }
        throw new IllegalStateException("No se pudo obtener el userId de los details del token");
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof Claims claims) {
            return claims.get("role", String.class);
        }
        throw new IllegalStateException("No se pudo obtener el rol del token");
    }
}
