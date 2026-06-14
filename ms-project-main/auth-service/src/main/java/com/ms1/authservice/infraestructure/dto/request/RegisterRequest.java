package com.ms1.authservice.infraestructure.dto.request;

import com.ms1.authservice.domain.model.Role;
import com.ms1.authservice.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    private String email;

    @NotBlank(message = "El Usuario es requerido")
    @Size(min = 6, max = 20, message = "El nombre debe tener entre 6 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%\\^&\\*/\\-_\\.]).{8,}$",
            message = "La contraseña debe incluir mayúsculas, minúsculas, números y un carácter especial"
    )
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMIN|OPERADOR|CLIENTE)$", message = "El rol proporcionado no es válido")
    private String role;

    public User toDomain() {
        User user = new User();
        user.setNombre(this.nombre);
        user.setEmail(this.email);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRole(Role.valueOf(this.role.toUpperCase()));
        return user;
    }
}