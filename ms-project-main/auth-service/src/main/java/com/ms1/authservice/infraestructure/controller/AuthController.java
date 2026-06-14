package com.ms1.authservice.infraestructure.controller;

import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.domain.ports.in.AuthPortIn;
import com.ms1.authservice.infraestructure.dto.mapper.UserMapper;
import com.ms1.authservice.infraestructure.dto.request.LoginRequest;
import com.ms1.authservice.infraestructure.dto.request.RegisterRequest;
import com.ms1.authservice.infraestructure.dto.response.AuthResponse;
import com.ms1.authservice.infraestructure.dto.response.UserResponse;
import com.ms1.authservice.infraestructure.rest.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthPortIn authPortIn;

    public AuthController(AuthPortIn authPortIn) {
        this.authPortIn = authPortIn;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true, "Registro exitoso", authPortIn.register(request.toDomain())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Login exitoso", authPortIn.login(request.getEmail(), request.getPassword())
        ));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        User user = authPortIn.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Usuario encontrado", UserMapper.toResponse(user)
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> list = authPortIn.getAllUsers().stream()
                .map(UserMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista de usuarios", list));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id, @Valid @RequestBody RegisterRequest request) {
        User updatedUser = authPortIn.updateUser(id, request.toDomain());
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Usuario actualizado", UserMapper.toResponse(updatedUser)
        ));
    }
}