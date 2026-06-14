package com.ms3.shippingservice.infrastructure.config.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Recuperamos la autenticación de la petición HTTP
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si el usuario está autenticado y tiene el token guardado en sus credenciales
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();

            //Inyectamos de forma automática el Bearer token en la llamada saliente de Feign
            template.header("Authorization", "Bearer " + token);
        }
    }
}