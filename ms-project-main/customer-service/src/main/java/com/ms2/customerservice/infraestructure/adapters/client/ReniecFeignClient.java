package com.ms2.customerservice.infraestructure.adapters.client;

import com.ms2.customerservice.infraestructure.dto.response.ReniecResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "reniec-client", url = "${api.reniec.url}")
public interface ReniecFeignClient {

    @GetMapping("/dni")
    ReniecResponse validarDni(
            @RequestParam("numero") String dni,
            @RequestHeader("Authorization") String token
    );
}
