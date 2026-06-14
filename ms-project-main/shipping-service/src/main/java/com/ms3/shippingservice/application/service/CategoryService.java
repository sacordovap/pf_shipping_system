package com.ms3.shippingservice.application.service;

import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.ports.in.CategoriesPortIn;
import com.ms3.shippingservice.domain.ports.out.CategoryPortOut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoriesPortIn {

    private final CategoryPortOut categoryPortOut;

    public CategoryService(CategoryPortOut categoryPortOut) {

        this.categoryPortOut = categoryPortOut;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryPortOut.findAll();
    }
}
