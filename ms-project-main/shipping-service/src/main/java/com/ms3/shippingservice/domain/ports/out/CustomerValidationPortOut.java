package com.ms3.shippingservice.domain.ports.out;

import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.CustomerFeignDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.UUID;

public interface CustomerValidationPortOut {
    boolean isCustomerValid(String docDNI);
    ApiResponse<CustomerFeignDto> getCustomerByDni(String docDNI);
}