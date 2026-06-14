package com.ms3.shippingservice.domain.ports.in;

import com.ms3.shippingservice.domain.model.Category;

import java.util.List;

public interface CategoriesPortIn {
    List<Category> getAllCategories();
}
