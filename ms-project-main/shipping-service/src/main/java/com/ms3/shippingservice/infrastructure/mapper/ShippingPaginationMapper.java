package com.ms3.shippingservice.infrastructure.mapper;

import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.infrastructure.config.ShippingMapper;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.PageResponseDTO;
import com.ms3.shippingservice.infrastructure.dto.response.ShippingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingPaginationMapper {

    private final ShippingResponseMapper shippingResponseMapper;

    public ApiResponse<PageResponseDTO<ShippingResponseDTO>> toPagedResponse(Page<Shipping> page) {
        return new ApiResponse<>(
                true,
                "Envios Registrados",
                new PageResponseDTO<>(
                        shippingResponseMapper.toResponseList(page.getContent()),
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast()
                )
        );
    }
}