package com.ms2.customerservice.domain.ports.out;

import com.ms2.customerservice.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPortOut {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByDni(String dni);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    void deleteById(UUID id);
    List<Customer> findAllActive();
}