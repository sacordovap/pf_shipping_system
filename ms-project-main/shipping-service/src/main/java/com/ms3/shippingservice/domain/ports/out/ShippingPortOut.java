package com.ms3.shippingservice.domain.ports.out;

import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingPortOut {

    Shipping save(Shipping shipping);
    Optional<Shipping> findById(UUID id);
    Optional<Shipping> findByTrackingNumber(String trackingNumber);
    List<Shipping> findByBranchAndState(String branch, ShippingState state);
    List<Shipping> searchByTrackingPartial(String term);
    List<Category> findCategoriesByIds(List<UUID> categoryIds);
    List<Shipping> findByCategoryAndState(String category, ShippingState state);
    List<Shipping> findByName(String name);
}
