package com.ms2.customerservice.infraestructure.dto.response;
import com.ms2.customerservice.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private UUID id;
    private String dni;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<AddressResponseDTO> addresses;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // MAPEO: salida DESDE el Modelo
    public static CustomerResponseDTO fromDomain(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setDni(customer.getDni());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setActive(customer.isActive());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        dto.setAddresses((customer.getAddresses() == null) ? new ArrayList<>() :
                customer.getAddresses().stream()
                        .map(domainAddr -> {
                            AddressResponseDTO addrDto = new AddressResponseDTO();
                            addrDto.setStreet(domainAddr.getStreet());
                            addrDto.setCity(domainAddr.getCity());
                            addrDto.setDepartment(domainAddr.getDepartment());
                            return addrDto;
                        })
                        .toList());

        return dto;
    }
}