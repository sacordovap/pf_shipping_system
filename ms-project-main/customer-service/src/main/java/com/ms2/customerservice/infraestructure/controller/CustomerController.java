package com.ms2.customerservice.infraestructure.controller;

import com.ms2.customerservice.domain.ports.in.CustomerPortIn;
import com.ms2.customerservice.infraestructure.dto.request.CustomerRegisterRequest;
import com.ms2.customerservice.infraestructure.dto.response.ApiResponse;
import com.ms2.customerservice.infraestructure.dto.response.CustomerResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerPortIn customerPortIn;

    public CustomerController(CustomerPortIn customerPortIn) {
        this.customerPortIn = customerPortIn;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> register(@Valid @RequestBody CustomerRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true,
                "Cliente registrado de manera satisfactoria",
                CustomerResponseDTO.fromDomain(customerPortIn.registerCustomer(request.toDomain()))
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getById(@PathVariable UUID id) {
        return customerPortIn.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(new ApiResponse<>(true, "Cliente encontrado", CustomerResponseDTO.fromDomain(customer))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Customer not found with ID: " + id, null)));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getByDni(@PathVariable String dni) {
        return customerPortIn.getCustomerByDni(dni)
                .map(customer -> ResponseEntity.ok(new ApiResponse<>(true, "Cliente encontrado con el DNI", CustomerResponseDTO.fromDomain(customer))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "No se encontro el cliente con DNI: " + dni, null)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Lista de todos los clientes",
                customerPortIn.getAllCustomers().stream().map(CustomerResponseDTO::fromDomain).toList()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerRegisterRequest request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Información del cliente actualizada",
                CustomerResponseDTO.fromDomain(customerPortIn.updateCustomer(id, request.toDomain()))
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        customerPortIn.deleteCustomer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cliente deshabilitado", null));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> reactivate(@PathVariable UUID id) {
        customerPortIn.reactivateCustomer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cliente reactivado", null));
    }
}