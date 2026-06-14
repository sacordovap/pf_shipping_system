package com.ms2.customerservice.infraestructure.adapters;

import com.ms2.customerservice.domain.ports.out.ReniecPortOut;
import com.ms2.customerservice.infraestructure.adapters.client.ReniecFeignClient;
import com.ms2.customerservice.infraestructure.dto.response.ReniecResponse;
import com.ms2.customerservice.infraestructure.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReniecAdapter implements ReniecPortOut {

    private final ReniecFeignClient reniecFeignClient;
    private final String apiToken;

    public ReniecAdapter(ReniecFeignClient reniecFeignClient,
                         @Value("${api.reniec.token}") String apiToken) {
        this.reniecFeignClient = reniecFeignClient;
        this.apiToken = apiToken;
    }

    @Override
    public Optional<String> fetchByDni(String dni) {
        try {
            ReniecResponse response = reniecFeignClient.validarDni(dni, apiToken);
            if (response == null) {
                return Optional.empty();
            }
            String officialFullName;

            if (response.getFullName() != null && !response.getFullName().isBlank()) {
                officialFullName = response.getFullName().trim();
            } else if (response.getFirstName() != null) {
                String firstLast = response.getFirstLastName() != null ? response.getFirstLastName() : "";
                String secondLast = response.getSecondLastName() != null ? response.getSecondLastName() : "";

                officialFullName = String.format("%s %s %s",
                        response.getFirstName(),
                        firstLast,
                        secondLast
                );
            } else {
                return Optional.empty();
            }

            officialFullName = officialFullName.trim().replaceAll("\\s+", " ");

            return Optional.of(officialFullName);

        } catch (Exception ex) {
            throw new ExternalServiceException("The RENIEC external service is currently unavailable or returned an error: ",ex);
        }
    }
}