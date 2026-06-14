package com.ms3.shippingservice.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Category {
    private UUID id;
    private String name;
    private String description;

    public Category() {}

    public Category(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}