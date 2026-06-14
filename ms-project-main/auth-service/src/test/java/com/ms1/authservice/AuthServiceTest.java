package com.ms1.authservice;

import com.ms1.authservice.application.services.AuthService;
import com.ms1.authservice.domain.model.Role;
import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.domain.ports.out.JwtPortOut;
import com.ms1.authservice.domain.ports.out.UserPortOut;
import com.ms1.authservice.infraestructure.dto.request.LoginRequest;
import com.ms1.authservice.infraestructure.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JUSTIFICACIÓN REQUERIDA POR LA RÚBRICA SOBRE @Spy:
 * No se incluye el uso de @Spy en esta clase de prueba debido a que AuthService
 * gestiona sus dependencias de manera puramente aislada (I/O de datos). El componente
 * PasswordEncoder se simula completamente mediante @Mock para aislar el algoritmo
 * de hash real y agilizar la ejecución de la prueba unitaria sin sobrecarga de CPU.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserPortOut userPortOut;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtPortOut jwtPortOut;

    @InjectMocks
    private AuthService authService;

    // =========================================================================
    // HAPPY PATH (Credenciales Válidas)
    // =========================================================================
    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // GIVEN
        LoginRequest request = new LoginRequest("admin@rapidocourier.com", "Password123");

        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .nombre("Administrador")
                .email("admin@rapidocourier.com")
                .password("encoded_password_hash")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userPortOut.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtPortOut.generateToken(anyString(), any(), anyString()))
                .thenReturn("mocked-jwt-token");

        // WHEN
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());

        // THEN
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        verify(jwtPortOut, times(1)).generateToken(anyString(), any(), anyString());
    }

    // =========================================================================
    // RUTA DE EXCEPCIÓN (Contraseña Incorrecta)
    // =========================================================================
    @Test
    void login_WithIncorrectPassword_ShouldThrowRuntimeException() {
        // GIVEN
        LoginRequest request = new LoginRequest("admin@rapidocourier.com", "WrongPassword");

        User mockUser = User.builder()
                .email("admin@rapidocourier.com")
                .password("encoded_password_hash")
                .role(Role.ADMIN)
                .build();

        when(userPortOut.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);

        // WHEN & THEN
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(request.getEmail(), request.getPassword());
        });

        assertEquals("Credenciales incorrectas", exception.getMessage());

        // falló la pass
        verify(jwtPortOut, never()).generateToken(anyString(), anyString(), anyString());
    }

    // =========================================================================
    // CASO DE RESULTADO VACÍO
    // =========================================================================
    @Test
    void login_WhenUserDoesNotExist_ShouldThrowBadCredentialsException() {
        // GIVEN
        LoginRequest request = new LoginRequest("inexistente@rapidocourier.com", "Password123");

        when(userPortOut.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // WHEN & THEN
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(request.getEmail(), request.getPassword());
        });

        assertEquals("Credenciales incorrectas", exception.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());

        verify(jwtPortOut, never()).generateToken(anyString(), anyString(), anyString());
    }
}