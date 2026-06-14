package com.ms2.customerservice.application.services;

import com.ms2.customerservice.domain.model.Customer;
import com.ms2.customerservice.domain.ports.in.CustomerPortIn;
import com.ms2.customerservice.domain.ports.out.CustomerRepositoryPortOut;
import com.ms2.customerservice.domain.ports.out.ReniecPortOut;
import com.ms2.customerservice.infraestructure.exception.ResourceNotFoundException;
import jakarta.ws.rs.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService implements CustomerPortIn {

    private final CustomerRepositoryPortOut customerRepositoryPortOut;
    private final ReniecPortOut reniecPortOut;

    public CustomerService(CustomerRepositoryPortOut customerRepositoryPortOut, ReniecPortOut reniecPortOut) {
        this.customerRepositoryPortOut = customerRepositoryPortOut;
        this.reniecPortOut = reniecPortOut;
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        if (customerRepositoryPortOut.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + customer.getEmail());
        }

        if (customerRepositoryPortOut.findByDni(customer.getDni()).isPresent()) {
            throw new RuntimeException("Customer with DNI " + customer.getDni() + " already exists");
        }

        String officialFullName = reniecPortOut.fetchByDni(customer.getDni())
                .orElseThrow(() -> new RuntimeException("DNI not found in RENIEC registry"));

        customer.setFullName(officialFullName);
        return customerRepositoryPortOut.save(customer);
    }

    @Override
    public Customer updateCustomer(UUID id, Customer updatedData) {
        Customer existingCustomer = customerRepositoryPortOut.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        Optional<Customer> customerWithSameEmail = customerRepositoryPortOut.findByEmail(updatedData.getEmail());
        if (customerWithSameEmail.isPresent() && !customerWithSameEmail.get().getId().equals(id)) {
            throw new RuntimeException("Email already in use by another customer");
        }

        existingCustomer.setEmail(updatedData.getEmail());
        existingCustomer.setPhoneNumber(updatedData.getPhoneNumber());
        existingCustomer.setAddresses(updatedData.getAddresses());

        return customerRepositoryPortOut.save(existingCustomer);
    }

    @Override
    public Optional<Customer> getCustomerById(UUID id) { return customerRepositoryPortOut.findById(id); }

    @Override
    public Optional<Customer> getCustomerByDni(String dni) { return customerRepositoryPortOut.findByDni(dni); }

    @Override
    public List<Customer> getAllCustomers() { return customerRepositoryPortOut.findAll(); }

    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepositoryPortOut.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        customer.setActive(false);
        customerRepositoryPortOut.save(customer);
    }

    @Override
    public void reactivateCustomer(UUID id) {
        Customer customer = customerRepositoryPortOut.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));

        if (customer.isActive()) {
            throw new BadRequestException("Customer is already active");
        }
        customer.setActive(true);
        customerRepositoryPortOut.save(customer);
    }
}