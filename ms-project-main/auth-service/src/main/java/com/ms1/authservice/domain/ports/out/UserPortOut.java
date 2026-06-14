package com.ms1.authservice.domain.ports.out;

import com.ms1.authservice.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPortOut {

    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
    boolean existsByUsername(String username);
}