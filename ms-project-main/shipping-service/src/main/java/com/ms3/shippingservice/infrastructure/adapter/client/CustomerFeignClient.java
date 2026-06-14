package com.ms3.shippingservice.infrastructure.adapter.client;

import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.CustomerFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerFeignClient {

    @GetMapping("/api/v1/customers/dni/{dni}")
    ApiResponse<CustomerFeignDto> getCustomerByDni(@PathVariable("dni") String dni);
}