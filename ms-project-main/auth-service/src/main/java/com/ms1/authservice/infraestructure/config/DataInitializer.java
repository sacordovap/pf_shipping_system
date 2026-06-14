package com.ms1.authservice.infraestructure.config;


import com.ms1.authservice.domain.model.Role;
import com.ms1.authservice.infraestructure.persistency.entity.RoleEntity;
import com.ms1.authservice.infraestructure.persistency.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    final private RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for(Role role: Role.values()) {
            RoleEntity roleEntity = roleRepository.findByRoleName(role.name()).orElse(null);
            if(roleEntity == null) {
                RoleEntity roleCreated = new RoleEntity();
                roleCreated.setRoleName(role.name());
                roleRepository.save(roleCreated);
            }
        }
    }
}
