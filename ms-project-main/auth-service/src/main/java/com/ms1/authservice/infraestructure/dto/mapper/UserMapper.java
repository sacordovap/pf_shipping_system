package com.ms1.authservice.infraestructure.dto.mapper;

import com.ms1.authservice.domain.model.Role;
import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.infraestructure.dto.response.UserResponse;
import com.ms1.authservice.infraestructure.persistency.entity.UserEntity;

import java.util.UUID;

public class UserMapper {

    // Transforma de Entidad a Modelo
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setNombre(entity.getNombre());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setRole(Role.valueOf(entity.getRole().getRoleName()));
        user.setActive(entity.getActive());
        return user;
    }

    public static User toDomainView(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
//        user.setId(entity.getId());
        user.setNombre(entity.getNombre());
        user.setEmail(entity.getEmail());
        user.setUsername(entity.getUsername());
        user.setRole(Role.valueOf(entity.getRole().getRoleName()));
        user.setActive(entity.getActive());
        return user;
    }

    // Transforma de Modelo a Entidad
    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setEmail(domain.getEmail());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setActive(domain.getActive());
        return entity;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        UserResponse response = new UserResponse();
//        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setActive(user.getActive());

        return response;
    }
}