package com.ms1.authservice.domain.ports.in;


import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.infraestructure.dto.response.AuthResponse;

import java.util.List;
import java.util.UUID;

public interface AuthPortIn {

    AuthResponse register(User user);
    AuthResponse login(String email, String password);

    User getUserById(UUID id);
    List<User> getAllUsers();
    User updateUser(UUID id, User updatedData);

    void deleteUser(UUID id);
}