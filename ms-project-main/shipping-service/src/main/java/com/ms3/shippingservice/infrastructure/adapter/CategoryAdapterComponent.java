package com.ms3.shippingservice.infrastructure.adapter;


import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.ports.out.CategoryPortOut;
import com.ms3.shippingservice.infrastructure.persistency.entity.CategoryEntity;
import com.ms3.shippingservice.infrastructure.persistency.repository.JpaCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryAdapterComponent implements CategoryPortOut {
    private final JpaCategoryRepository jpaCategoryRepository;

    CategoryAdapterComponent(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    @Override
    public List<Category> findAll() {

        List<CategoryEntity> entities = jpaCategoryRepository.findAll();
        List<Category> domainList = new ArrayList<>();
        for (CategoryEntity entity : entities) {
            domainList.add(mapToDomain(entity));
        }
        return domainList;
    }

    private Category mapToDomain(CategoryEntity entity) {
        Category category = new Category();
        category.setId(entity.getId());
        category.setDescription(entity.getDescription());
        category.setName(entity.getName());

        return category;
    }
}
