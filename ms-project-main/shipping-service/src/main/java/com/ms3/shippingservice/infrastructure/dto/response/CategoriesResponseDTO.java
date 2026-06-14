package com.ms3.shippingservice.infrastructure.dto.response;

import com.ms3.shippingservice.domain.model.Category;import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesResponseDTO {
    private UUID id;
    private String name;
    private String description;

    public static CategoriesResponseDTO fromDomain(Category category) {
        CategoriesResponseDTO dto = new CategoriesResponseDTO();
        dto.setId(category.getId());
        dto.setDescription(category.getDescription());
        dto.setName(category.getName());

        return dto;
    }
}
