package com.ms3.shippingservice.infrastructure.controller;


import com.ms3.shippingservice.domain.ports.in.CategoriesPortIn;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.CategoriesResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoriesPortIn categoriesPortIn;

    public CategoryController(CategoriesPortIn categoriesPortIn) {
        this.categoriesPortIn = categoriesPortIn;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriesResponseDTO>>> getAll(){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Lista de categorias",
                        categoriesPortIn.getAllCategories().stream().map(CategoriesResponseDTO::fromDomain).toList()
                )
        );
    }
}
