package com.ms3.shippingservice.infrastructure.persistency.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    public CategoryEntity() {}

    public CategoryEntity(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}