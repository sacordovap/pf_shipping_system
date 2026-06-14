package com.ms3.shippingservice.infrastructure.dto.response;

import lombok.Data;

@Data
public class CustomerFeignDto {
    private String dni;
    private String fullName;
    private boolean active;
}