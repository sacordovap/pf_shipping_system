package com.ms3.shippingservice.infrastructure.mapper;

import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.infrastructure.dto.response.ShippingResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingResponseMapper {

    public ShippingResponseDTO toResponse(Shipping shipping) {
        return ShippingResponseDTO.fromDomain(shipping);
    }

    public List<ShippingResponseDTO> toResponseList(List<Shipping> shippings) {
        return shippings.stream()
                .map(this::toResponse)
                .toList();
    }
}
