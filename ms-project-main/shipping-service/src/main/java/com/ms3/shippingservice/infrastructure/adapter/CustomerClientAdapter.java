package com.ms3.shippingservice.infrastructure.adapter;

import com.ms3.shippingservice.domain.ports.out.CustomerValidationPortOut;
import com.ms3.shippingservice.infrastructure.adapter.client.CustomerFeignClient;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.CustomerFeignDto;
import com.ms3.shippingservice.infrastructure.exception.ServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomerClientAdapter implements CustomerValidationPortOut {

    private static final Logger log = LoggerFactory.getLogger(CustomerClientAdapter.class);
    private final CustomerFeignClient customerFeignClient;

    public CustomerClientAdapter(CustomerFeignClient customerFeignClient) {
        this.customerFeignClient = customerFeignClient;
    }
    @Override
    @CircuitBreaker(name = "customer-service",fallbackMethod = "isCustomerValidFallback")
    public boolean isCustomerValid(String docDNI) {

        log.info("event=customer_lookup_started dni={}", docDNI);
        try {
            ApiResponse<CustomerFeignDto> response = customerFeignClient.getCustomerByDni(docDNI);
            log.info("event=customer_lookup_succeeded dni={}", docDNI);

            return response != null
                    && response.isSuccess()
                    && response.getData() != null
                    && response.getData().isActive();

        } catch (FeignException.NotFound e) {
            log.warn("event=customer_not_found dni={}", docDNI);
            return false;
        }

    }
    private boolean isCustomerValidFallback(String docDNI,Throwable t) {
        log.error("event=customer_lookup_fallback dni={} exception={} message={}",
                docDNI,
                t.getClass().getName(),
                t.getMessage(),
                t
        );
        throw new ServiceUnavailableException(
                "El servicio de verificación de clientes no está disponible temporalmente."
        );
    }

    @Override
    @CircuitBreaker(name = "customer-service", fallbackMethod = "customerDataValidFallback")
    public ApiResponse<CustomerFeignDto> getCustomerByDni(String docDNI) {
        ApiResponse<CustomerFeignDto> response = customerFeignClient.getCustomerByDni(docDNI);
        log.info("event=customer_lookup_succeeded dni={}", docDNI);
        return response;
    }

    private boolean customerDataValidFallback(String docDNI,Throwable t) {
        log.error("event=customer_lookup_fallback dni={} exception={} message={}",
                docDNI,
                t.getClass().getName(),
                t.getMessage(),
                t
        );
        throw new ServiceUnavailableException(
                "El servicio de verificación de clientes no está disponible temporalmente."
        );
    }
}