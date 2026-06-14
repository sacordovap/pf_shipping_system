package com.ms1.authservice.infraestructure.adapter;

import com.ms1.authservice.domain.model.User;
import com.ms1.authservice.domain.ports.out.UserPortOut;
import com.ms1.authservice.infraestructure.dto.mapper.UserMapper;
import com.ms1.authservice.infraestructure.persistency.entity.RoleEntity;
import com.ms1.authservice.infraestructure.persistency.entity.UserEntity;
import com.ms1.authservice.infraestructure.persistency.repository.JpaUserRepository;
import com.ms1.authservice.infraestructure.persistency.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserPortOut {

    private final JpaUserRepository jpaUserRepository;
    private final RoleRepository roleRepository;

    public UserPersistenceAdapter(JpaUserRepository jpaUserRepository, RoleRepository roleRepository) {
        this.jpaUserRepository = jpaUserRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);

        RoleEntity roleEntity = roleRepository.findByRoleName(user.getRole().name())
                .orElseThrow(() -> new RuntimeException("Error: El rol " + user.getRole().name() + " no está inicializado en la BD."));
        entity.setRole(roleEntity);

        UserEntity savedEntity = jpaUserRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }
}