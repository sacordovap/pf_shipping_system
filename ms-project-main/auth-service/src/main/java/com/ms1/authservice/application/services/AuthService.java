package com.ms1.authservice.application.services;

import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.domain.ports.in.AuthPortIn;
import com.ms1.authservice.domain.ports.out.JwtPortOut;
import com.ms1.authservice.domain.ports.out.UserPortOut;
import com.ms1.authservice.infraestructure.dto.response.AuthResponse;
import com.ms1.authservice.infraestructure.exception.ConflictException;
import com.ms1.authservice.infraestructure.exception.DuplicatedResourceException;
import com.ms1.authservice.infraestructure.exception.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService implements AuthPortIn {
    private static final String MSG_ERROR_AUTH = "Credenciales incorrectas";
    private final UserPortOut userPortOut;
    private final PasswordEncoder passwordEncoder;
    private final JwtPortOut jwtPortOut;

    public AuthService(UserPortOut userPortOut, PasswordEncoder passwordEncoder, JwtPortOut jwtPortOut) {
        this.userPortOut = userPortOut;
        this.passwordEncoder = passwordEncoder;
        this.jwtPortOut = jwtPortOut;
    }

    // --- OPERACIONES DE AUTENTICACIÓN ---
    @Override
    public AuthResponse register(User user) {
        if (userPortOut.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicatedResourceException("El correo electrónico ya se encuentra registrado");
        }
        if (userPortOut.existsByUsername(user.getUsername())) {
            throw new ConflictException("El nombre de usuario ya se encuentra registrado."); // 409
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encriptamos con BCrypt
        user.setActive(true);

        User savedUser = userPortOut.save(user);
        String token = jwtPortOut.generateToken(savedUser.getEmail(),savedUser.getUsername(),savedUser.getRole().name());

        return createAuthResponse(savedUser, token);
    }

    @Override
    public AuthResponse login(String email, String password) {
        User user = userPortOut.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(MSG_ERROR_AUTH));
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Error: El password registrado es nulo");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException(MSG_ERROR_AUTH);
        }
        String token = jwtPortOut.generateToken(user.getEmail(), user.getUsername(), user.getRole().name());
        return createAuthResponse(user, token);
    }

    // --- OPERACIONES CRUD ---
    @Override
    public User getUserById(UUID id) {
        return userPortOut.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID proporcionado"));
    }

    @Override
    public List<User> getAllUsers() {
        return userPortOut.findAll();
    }

    @Override
    public User updateUser(UUID id, User updatedData){
        User existingUser = userPortOut.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar. Usuario no encontrado"));

        userPortOut.findByEmail(updatedData.getEmail()).ifPresent(userWithEmail -> {
            if (!userWithEmail.getId().equals(id)) {
                throw new DuplicatedResourceException("El nuevo correo electrónico ya está siendo usado por otro usuario");
            }
        });
        existingUser.setNombre(updatedData.getNombre());
        existingUser.setEmail(updatedData.getEmail());
        existingUser.setRole(updatedData.getRole());

        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedData.getPassword()));
        }

        return userPortOut.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (userPortOut.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("No se puede eliminar. Usuario no encontrado");
        }
        userPortOut.deleteById(id);
    }

    private AuthResponse createAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getEmail(), user.getUsername(),user.getRole().name());
    }
}