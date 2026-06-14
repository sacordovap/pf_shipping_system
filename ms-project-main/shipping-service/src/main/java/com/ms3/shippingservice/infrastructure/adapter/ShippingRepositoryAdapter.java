package com.ms3.shippingservice.infrastructure.adapter;

import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingState;
import com.ms3.shippingservice.domain.model.ShippingStateHistory;
import com.ms3.shippingservice.domain.ports.out.ShippingPortOut;
import com.ms3.shippingservice.infrastructure.config.ShippingMapper;
import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingEntity;
import com.ms3.shippingservice.infrastructure.persistency.entity.ShippingStateHistoryEntity;
import com.ms3.shippingservice.infrastructure.persistency.repository.JpaCategoryRepository;
import com.ms3.shippingservice.infrastructure.persistency.repository.JpaShippingRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ShippingRepositoryAdapter implements ShippingPortOut {

    private final JpaShippingRepository jpaRepository;
    private final JpaCategoryRepository jpaCategoryRepository;

    public ShippingRepositoryAdapter(JpaShippingRepository jpaRepository, JpaCategoryRepository jpaCategoryRepository) {
        this.jpaRepository = jpaRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    @Override
    public Shipping save(Shipping shipping) {
        ShippingEntity entity;

        if (shipping.getId() == null) {
            entity = ShippingMapper.toEntity(shipping);

            if (shipping.getStateHistory() != null && !shipping.getStateHistory().isEmpty()) {
                List<ShippingStateHistoryEntity> historyEntities = new ArrayList<>();
                for (ShippingStateHistory histDomain : shipping.getStateHistory()) {
                    ShippingStateHistoryEntity histEntity = new ShippingStateHistoryEntity();
                    histEntity.setState(histDomain.getState());
                    histEntity.setChangedBy(histDomain.getChangedBy());
                    histEntity.setTimestamp(histDomain.getTimestamp());
                    histEntity.setShipping(entity); // Vínculo obligatorio
                    historyEntities.add(histEntity);
                }
                entity.setStateHistory(historyEntities);
            }
        } else {
            entity = jpaRepository.findById(shipping.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado"));

            entity.setCurrentState(shipping.getCurrentState());
            entity.setUpdatedAt(shipping.getUpdatedAt());

// Solo necesitamos añadir lo que no existe en la base de datos (los nuevos registros)
            if (shipping.getStateHistory() != null) {
                for (ShippingStateHistory histDomain : shipping.getStateHistory()) {
                    if (histDomain.getId() == null) {
                        ShippingStateHistoryEntity newHistory = new ShippingStateHistoryEntity();
                        newHistory.setState(histDomain.getState());
                        newHistory.setChangedBy(histDomain.getChangedBy());
                        newHistory.setTimestamp(histDomain.getTimestamp());
                        newHistory.setShipping(entity);

                        entity.getStateHistory().add(newHistory);
                    }
                }
            }

        }
        ShippingEntity savedEntity = jpaRepository.save(entity);
        return ShippingMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Shipping> findById(UUID id) {
        return jpaRepository.findById(id).map(ShippingMapper::toDomain);
    }

    @Override
    public Optional<Shipping> findByTrackingNumber(String trackingNumber) {
        return jpaRepository.findByTrackingNumber(trackingNumber).map(ShippingMapper::toDomain);
    }

    @Override
    public List<Shipping> findByBranchAndState(String branch, ShippingState state) {
        return jpaRepository.findByBranchAndState(branch, state.name()).stream()
                .map(ShippingMapper::toDomain)
                .toList();
    }

    @Override
    public List<Shipping> searchByTrackingPartial(String term) {
        return jpaRepository.searchByTrackingPartial(term).stream()
                .map(ShippingMapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findCategoriesByIds(List<UUID> categoryIds) {
        return jpaCategoryRepository.findAllById(categoryIds).stream()
                .map(c -> new Category(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }

    @Override
    public List<Shipping> findByCategoryAndState(String category, ShippingState state) {
        List<ShippingEntity> entities = jpaRepository.findByStateAndCategory(state, category);
        return entities.stream()
                .map(ShippingMapper::toDomain)
                .toList();
    }

    @Override
    public List<Shipping> findByName(String name) {
        List<ShippingEntity> entities = jpaRepository.findByName(name);
        return entities.stream()
                .map(ShippingMapper::toDomain)
                .toList();
    }
}