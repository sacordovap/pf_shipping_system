package com.ms1.authservice.infraestructure.dto.response;

import com.ms1.authservice.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
//    private UUID id;
    private String nombre;
    private String email;
    private String username;
    private Role role;
    private boolean active;

}
