package com.ms3.shippingservice.infrastructure.config;

import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingStateHistory;
import com.ms3.shippingservice.infrastructure.persistency.entity.CategoryEntity;
import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingEntity;
import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingStateHistoryEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ShippingMapper {

    public static Shipping toDomain(ShippingEntity entity) {
        if (entity == null) return null;

        Shipping domain = new Shipping();
        domain.setId(entity.getId());
        domain.setTrackingNumber(entity.getTrackingNumber());

        domain.setDniRemitente(entity.getDniRemitente());
        domain.setRemitente(entity.getRemitente());

        domain.setDniDestinatario(entity.getDniDestinatario());
        domain.setDestinatario(entity.getDestinatario());

        domain.setDescription(entity.getDescription());

        domain.setOriginBranch(entity.getOriginBranch());
        domain.setDestinationBranch(entity.getDestinationBranch());

        domain.setWeight(entity.getWeight());
        domain.setDeclaredValue(entity.getDeclaredValue());
        domain.setShippingCost(entity.getShippingCost());
        domain.setCurrentState(entity.getCurrentState());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setCreatedBy(entity.getCreatedBy());

        if (entity.getCategories() != null) {
            domain.setCategories(new ArrayList<>(entity.getCategories().stream()
                    .map(c -> new Category(c.getId(), c.getName(), c.getDescription()))
                    .toList()));
        }

        if (entity.getStateHistory() != null) {
            domain.setStateHistory(new ArrayList<>(entity.getStateHistory().stream()
                    .map(statHist -> new ShippingStateHistory(
                            statHist.getId(),
                            statHist.getState(),
                            statHist.getChangedBy(),
                            statHist.getTimestamp()))
                    .toList()));
        }
        return domain;
    }

    public static ShippingEntity toEntity(Shipping domain) {
        if (domain == null) return null;

        ShippingEntity entity = new ShippingEntity();
        entity.setId(domain.getId());
        entity.setTrackingNumber(domain.getTrackingNumber());
        entity.setDniRemitente(domain.getDniRemitente());
        entity.setRemitente(domain.getRemitente());
        entity.setDniDestinatario(domain.getDniDestinatario());
        entity.setDestinatario(domain.getDestinatario());
        entity.setDescription(domain.getDescription());
        entity.setOriginBranch(domain.getOriginBranch());
        entity.setDestinationBranch(domain.getDestinationBranch());
        entity.setWeight(domain.getWeight());
        entity.setDeclaredValue(domain.getDeclaredValue());
        entity.setShippingCost(domain.getShippingCost());
        entity.setCurrentState(domain.getCurrentState());
        entity.setCreatedBy(domain.getCreatedBy());

        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : LocalDateTime.now());

        if (domain.getCategories() != null) {
            entity.setCategories(domain.getCategories().stream()
                    .map(c -> new CategoryEntity(c.getId(), c.getName(), c.getDescription()))
                    .toList());
        }

        if (domain.getStateHistory() != null) {
            entity.setStateHistory(domain.getStateHistory().stream()
                    .filter(statHist -> statHist.getId() == null)
                    .map(statHist -> {
                        ShippingStateHistoryEntity stateHistEnt = new ShippingStateHistoryEntity();
                        stateHistEnt.setState(statHist.getState());
                        stateHistEnt.setChangedBy(statHist.getChangedBy());
                        stateHistEnt.setTimestamp(statHist.getTimestamp());
                        stateHistEnt.setShipping(entity); // Establecemos la relación
                        return stateHistEnt;
                    })
                    .toList());
        }
        return entity;
    }
}