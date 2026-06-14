package com.ms2.customerservice.infraestructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Calle (street) es requerida")
    private String street;

    @NotBlank(message = "Ciudad (city) es requerida")
    private String city;

    @NotBlank(message = "Departamento (department) es requerido")
    private String department;
}