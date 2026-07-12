package com.ms3.shippingservice.infrastructure.config;

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
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/tracking/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/tracking/*/history").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/shippings/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/shippings/*/state").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/searchName").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/my-shippings").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/search").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/paged").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/user/paged").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/filter").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings//filter/search").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/filter/category").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/shippings").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shippings/detail").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}