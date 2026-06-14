package com.ms2.customerservice.domain.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private UUID id;
    private String dni;
    private String fullName;
    private String email;
    private String phoneNumber;

    private List<Address> addresses;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}