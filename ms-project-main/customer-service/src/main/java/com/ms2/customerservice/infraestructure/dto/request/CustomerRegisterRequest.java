package com.ms2.customerservice.infraestructure.dto.request;

import com.ms2.customerservice.domain.model.Address;
import com.ms2.customerservice.domain.model.Customer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterRequest {

    @NotBlank(message = "DNI es requerido")
    @Pattern(regexp = "^\\d{8}$", message = "DNI debe tener 8 digitos")
    private String dni;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number es requerido")
    private String phoneNumber;

    @NotEmpty(message = "Debe ingresar almenos una dirección")
    @Valid
    private List<AddressRequest> addresses;

    public Customer toDomain() {
        List<Address> domainAddresses = (this.addresses == null) ? new ArrayList<>() :
                this.addresses.stream()
                        .map(req -> {
                            Address address = new Address();
                            address.setStreet(req.getStreet());
                            address.setCity(req.getCity());
                            address.setDepartment(req.getDepartment());
                            return address;
                        })
                        .toList();

        Customer customer = new Customer();
        customer.setId(null);
        customer.setDni(this.dni);
//        customer.setFullName(officialFullName);
        customer.setEmail(this.email);
        customer.setPhoneNumber(this.phoneNumber);
        customer.setAddresses(domainAddresses);
        customer.setActive(true);

        return customer;
    }
}