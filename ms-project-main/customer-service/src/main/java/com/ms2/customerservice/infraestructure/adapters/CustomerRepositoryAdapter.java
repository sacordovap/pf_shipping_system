package com.ms2.customerservice.infraestructure.adapters;

import com.ms2.customerservice.domain.model.Address;
import com.ms2.customerservice.domain.model.Customer;
import com.ms2.customerservice.domain.ports.out.CustomerRepositoryPortOut;
import com.ms2.customerservice.infraestructure.persistence.entity.CustomerAddressEntity;
import com.ms2.customerservice.infraestructure.persistence.entity.CustomerEntity;
import com.ms2.customerservice.infraestructure.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPortOut {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity;

        if (customer.getId() == null) {
            entity = new CustomerEntity();
        } else {
            entity = customerJpaRepository.findById(customer.getId())
                    .orElseGet(CustomerEntity::new);
            entity.setId(customer.getId());
        }

        entity.setDni(customer.getDni());
        entity.setFullName(customer.getFullName());
        entity.setEmail(customer.getEmail());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setActive(customer.isActive());

        entity.getAddresses().clear();

        if (customer.getAddresses() != null) {
            for (Address domainAddr : customer.getAddresses()) {
                CustomerAddressEntity addrEntity = new CustomerAddressEntity();
                addrEntity.setStreet(domainAddr.getStreet());
                addrEntity.setCity(domainAddr.getCity());
                addrEntity.setDepartment(domainAddr.getDepartment());

                entity.getAddresses().add(addrEntity);
            }
        }

        if (customer.getId() != null && customerJpaRepository.existsById(customer.getId())) {
            entity.setUpdatedAt(LocalDateTime.now());
        } else {
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
        }

        CustomerEntity savedEntity = customerJpaRepository.save(entity);

        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Customer> findByDni(String dni) {
        return customerJpaRepository.findByDni(dni).map(this::mapToDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerJpaRepository.findByEmail(email).map(this::mapToDomain);
    }

    @Override
    public List<Customer> findAll() {
        List<CustomerEntity> entities = customerJpaRepository.findAllByOrderByIdAsc();
        List<Customer> domainList = new ArrayList<>();
        for (CustomerEntity entity : entities) {
            domainList.add(mapToDomain(entity));
        }
        return domainList;
    }

    @Override
    public void deleteById(UUID id) {
        customerJpaRepository.deleteById(id);
    }

    @Override
    public List<Customer> findAllActive() {
        return customerJpaRepository.findByActiveTrue().stream()
                .map(this::mapToDomain)
                .toList();
    }

    private Customer mapToDomain(CustomerEntity entity) {
        List<Address> domainAddresses = (entity.getAddresses() == null) ? new ArrayList<>() :
                entity.getAddresses().stream()
                        .map(entityAddr -> {
                            Address address = new Address();
                            address.setStreet(entityAddr.getStreet());
                            address.setCity(entityAddr.getCity());
                            address.setDepartment(entityAddr.getDepartment());
                            return address;
                        })
                        .toList();

        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setDni(entity.getDni());
        customer.setFullName(entity.getFullName());
        customer.setEmail(entity.getEmail());
        customer.setPhoneNumber(entity.getPhoneNumber());
        customer.setAddresses(domainAddresses);
        customer.setActive(entity.isActive());
        customer.setCreatedAt(entity.getCreatedAt());
        customer.setUpdatedAt(entity.getUpdatedAt());

        return customer;
    }
}