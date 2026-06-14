package com.ms2.customerservice.domain.ports.out;

import java.util.Optional;

public interface ReniecPortOut {
    Optional<String> fetchByDni(String dni);
}