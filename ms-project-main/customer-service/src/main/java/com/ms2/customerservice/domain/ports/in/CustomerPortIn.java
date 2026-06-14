package com.ms2.customerservice.domain.ports.in;

import com.ms2.customerservice.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerPortIn {
    Customer registerCustomer(Customer customer);
    Optional<Customer> getCustomerById(UUID id);
    Optional<Customer> getCustomerByDni(String dni);
    List<Customer> getAllCustomers();
    Customer updateCustomer(UUID id, Customer updatedData);
    void deleteCustomer(UUID id);
    void reactivateCustomer(UUID id);
}