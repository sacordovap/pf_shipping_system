package com.ms3.shippingservice.infrastructure.dto.request;

import com.ms3.shippingservice.domain.model.ShippingState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFilterRequest {
    private String branch;
    private ShippingState state;
    private String category;
    private String term;
    private String name;
    private int page = 1;
    private int size = 12;
    private boolean manual = false;
}