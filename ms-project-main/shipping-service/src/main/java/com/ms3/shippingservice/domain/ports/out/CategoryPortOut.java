package com.ms3.shippingservice.domain.ports.out;

import com.ms3.shippingservice.domain.model.Category;

import java.util.List;

public interface CategoryPortOut{

    List<Category> findAll();
}
