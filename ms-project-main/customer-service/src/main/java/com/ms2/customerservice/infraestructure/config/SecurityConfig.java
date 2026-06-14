package com.ms2.customerservice.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        // Captura el Error 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{" +
                                    "\"status\": 401," +
                                    "\"success\": false," +
//                                    "\"error\": \"Unauthorized\"," +
                                    "\"message\": \"Unauthorized: Token faltante o inválido. Debe iniciar sesión.\"," +
//                                    "\"path\": \"" + request.getRequestURI() + "\"" +
                                    "}");
                        })
                        // Captura el Error 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{" +
                                    "\"status\": 403," +
                                    "\"success\": false," +
//                                    "\"error\": \"Unauthorized\"," +
                                    "\"message\": \"Unauthorized: El rol no tiene los permisos necesario.\"," +
//                                    "\"path\": \"" + request.getRequestURI() + "\"" +
                                    "}");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                         // rutas con filtros específicos
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/dni/**").authenticated()

                         // Rutas exactas o acciones específicas
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/customers/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers").hasAnyRole("ADMIN", "OPERADOR")

                         // generales '**'
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/**").hasAnyRole("ADMIN", "OPERADOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}