package com.ms2.customerservice.infraestructure.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReniecResponse {

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "first_last_name")
    private String firstLastName;

    @JsonProperty(value = "second_last_name")
    private String secondLastName;

    @JsonProperty(value = "full_name")
    private String fullName;

    @JsonProperty(value = "document_number")
    private String documentNumber;
}
