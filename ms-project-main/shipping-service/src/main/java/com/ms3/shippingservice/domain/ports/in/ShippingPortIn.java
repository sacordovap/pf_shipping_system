package com.ms3.shippingservice.domain.ports.in;

import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingState;
import com.ms3.shippingservice.domain.model.ShippingStateHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShippingPortIn {
    Shipping createShipping(Shipping shipping, List<UUID> categoryIds, String operatorUsername);
    Shipping updateShippingState(UUID id, ShippingState newState, String operatorUsername);
    Shipping getById(UUID id);
    Shipping deleteShipping(UUID id, String operatorUsername);
    Shipping getByTrackingNumber(String trackingNumber);
    List<Shipping> getByBranchAndState(String branch, ShippingState state);
    List<Shipping> searchByTrackingPartial(String term);
    Shipping revertShippingState(UUID id, String operatorUsername);
    List<ShippingStateHistory> getHistoryByTracking(String trackingNumber);
    List<Shipping> getByCategoryAndState(String category, ShippingState state);
    List<Shipping> getByName(String name);
    List<Shipping> getAllShipping();
}