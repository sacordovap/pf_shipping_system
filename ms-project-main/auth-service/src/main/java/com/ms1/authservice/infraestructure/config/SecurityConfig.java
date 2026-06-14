package com.ms1.authservice.infraestructure.config;


import com.ms1.authservice.application.services.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .sessionManagement(session
                        ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        // Restricciones específicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/auth/users/**").hasAnyRole("ADMIN", "OPERADOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/users/**").hasAnyRole("ADMIN", "OPERADOR", "CLIENTE")
                        // Cualquier otra ruta
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());
        return http.build();
    }
}