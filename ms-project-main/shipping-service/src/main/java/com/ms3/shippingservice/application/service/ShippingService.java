package com.ms3.shippingservice.application.service;

import com.ms3.shippingservice.application.util.TrackingGenerator;
import com.ms3.shippingservice.domain.model.Category;
import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingState;
import com.ms3.shippingservice.domain.model.ShippingStateHistory;
import com.ms3.shippingservice.domain.ports.in.ShippingPortIn;
import com.ms3.shippingservice.domain.ports.out.CustomerValidationPortOut;
import com.ms3.shippingservice.domain.ports.out.ShippingPortOut;
import com.ms3.shippingservice.infrastructure.dto.request.ShippingFilterRequest;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.CustomerFeignDto;
import com.ms3.shippingservice.infrastructure.exception.InvalidTrackingFormatException;
import com.ms3.shippingservice.infrastructure.exception.ResourceNotFoundException;
import com.ms3.shippingservice.infrastructure.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RefreshScope
public class ShippingService implements ShippingPortIn {

    private final ShippingPortOut shippingPortOut;
    private final CustomerValidationPortOut customerValidationPortOut;
    private final SecurityUtils securityUtils;

    @Value("${tarifa.base}")
    private double base;

    @Value("${tarifa.factor-peso}")
    private double factorPeso;

    @Value("${tarifa.insureance}")
    private double insureance;


    public ShippingService(ShippingPortOut shippingPortOut, CustomerValidationPortOut customerValidationPortOut, SecurityUtils securityUtils) {
        this.shippingPortOut = shippingPortOut;
        this.customerValidationPortOut = customerValidationPortOut;
        this.securityUtils = securityUtils;
    }

    @Override
    public Shipping createShipping(Shipping shipping, List<UUID> categoryIds, String operatorUsername) {

        UUID userId = securityUtils.getCurrentUserId();
        String tracking = TrackingGenerator.generateUniqueTracking();
        boolean isRemitValid = customerValidationPortOut.isCustomerValid(shipping.getDniRemitente());
        boolean isDestValid = customerValidationPortOut.isCustomerValid(shipping.getDniDestinatario());
        if (!tracking.matches("TRK-[A-Z0-9]+-[A-Z0-9]+")) {
            throw new InvalidTrackingFormatException("Formato de tracking inválido");
        }
        if (!isRemitValid) {
            throw new ResourceNotFoundException("El remitente no está registrado o está inactivo.");
        }
        if (!isDestValid) {
            throw new ResourceNotFoundException("El destinatario no está registrado o está inactivo.");
        }

        ApiResponse<CustomerFeignDto> remitente = customerValidationPortOut.getCustomerByDni(shipping.getDniRemitente());
        ApiResponse<CustomerFeignDto> destinatario = customerValidationPortOut.getCustomerByDni(shipping.getDniDestinatario());

        if (remitente.getData() == null || remitente.getData().getFullName().isEmpty())
            throw new ResourceNotFoundException("El remitente no cuenta con nombre.");
        if (destinatario.getData() == null || destinatario.getData().getFullName().isEmpty())
            throw new ResourceNotFoundException("El destinatario no cuenta con nombre.");

        shipping.setRemitente(remitente.getData().getFullName());
        shipping.setDestinatario(destinatario.getData().getFullName());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = shippingPortOut.findCategoriesByIds(categoryIds);
            shipping.setCategories(categories);
        }

        shipping.setCreatedBy(userId);
        shipping.calculateShippingCost(this.base, this.factorPeso,this.insureance);
        shipping.initialize(ShippingState.REGISTRADO, operatorUsername);
//        shipping.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipping.setTrackingNumber(tracking);
        return shippingPortOut.save(shipping);
    }

    @Override
    public Shipping updateShippingState(UUID id, ShippingState newState, String operatorUsername) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del envío no puede ser nulo.");
        }
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado del envío es obligatorio.");
        }
        if (operatorUsername == null || operatorUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar el estado sin un operador autenticado.");
        }

        if (newState == ShippingState.ELIMINADO) {
            throw new IllegalArgumentException("El envío no esta disponible.");
        }

        Shipping shipping = shippingPortOut.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún envío con el ID proporcionado: " + id));

        shipping.updateState(newState, operatorUsername);

        return shippingPortOut.save(shipping);
    }

    @Override
    public Shipping deleteShipping(UUID id, String operatorUsername) {
        ShippingState newState = ShippingState.ELIMINADO;

        if (id == null) {
            throw new IllegalArgumentException("El ID del envío no puede ser nulo.");
        }
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado del envío es obligatorio.");
        }
        if (operatorUsername == null || operatorUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar el estado sin un operador autenticado.");
        }

        Shipping shipping = shippingPortOut.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún envío con el ID proporcionado: " + id));

        shipping.updateState(newState, operatorUsername);

        return shippingPortOut.save(shipping);
    }

    @Override
    public Shipping getById(UUID id) {
        return shippingPortOut.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío con el id solicitado no encontrado"));
    }

    @Override
    public Shipping getByTrackingNumber(String trackingNumber) {
        return shippingPortOut.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Envío con tracking " + trackingNumber + " no encontrado"));
    }

    @Override
    public List<Shipping> getByBranchAndState(String branch, ShippingState state) { return shippingPortOut.findByBranchAndState(branch, state); }

    @Override
    public List<Shipping> searchByTrackingPartial(String term) {
        if (term == null || term.trim().isEmpty()) return List.of();
        return shippingPortOut.searchByTrackingPartial(term.trim());
    }

    @Override
    public Shipping revertShippingState(UUID id, String operatorUsername) {
        Shipping shipping = shippingPortOut.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado"));

        shipping.revertState(operatorUsername);
        return shippingPortOut.save(shipping);
    }

    @Override
    public List<ShippingStateHistory> getHistoryByTracking(String trackingNumber) {
        return shippingPortOut.findByTrackingNumber(trackingNumber)
                .map(Shipping::getStateHistory)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado con tracking: " + trackingNumber));
    }

    @Override
    public List<Shipping> getByCategoryAndState(String category, ShippingState state) {
        return shippingPortOut.findByCategoryAndState(category, state);
    }

    @Override
    public List<Shipping> getByName(String name) {
        return shippingPortOut.findByName(name);
    }

    @Override
    public List<Shipping> getAllShipping() {
            return shippingPortOut.findAll();
    }

    @Override
    public List<Shipping> getCreatedBy() {
        UUID userId = securityUtils.getCurrentUserId();
        return shippingPortOut.findByCreatedBy(userId);
    }

    @Override
    public Page<Shipping> getByPage(int page, int size) {
        // Usuario envía 1 ->  busca 0
        int pageToFetch = (page > 0) ? page - 1 : 0;
        int pageSize = (size <= 0) ? 12 : size;

        return shippingPortOut.findByPage(pageToFetch, pageSize);
    }

    @Override
    public Page<Shipping> getShippingsByUserPaged(int page, int size) {
        UUID userId = securityUtils.getCurrentUserId();
        int pageToFetch = (page > 0) ? page - 1 : 0;
        int pageSize = (size <= 0) ? 12 : size;
        return shippingPortOut.findShippingsByUser(userId, pageToFetch, pageSize);
    }

    @Override
    public Page<Shipping> searchShippingsFilter(String branch, ShippingState state, String category,
                                          String term, String name, int page, int size, boolean manual) {
        if (manual) {
            return shippingPortOut.searchManual(branch, state, category, term, name, page, size);
        }
        return shippingPortOut.searchDynamic(branch, state, category, term, name, page, size);
    }

    @Override
    public Page<Shipping> searchShippingsFilter(ShippingFilterRequest request) {
        if (request.isManual()) {
            return shippingPortOut.searchManual(
                    request.getBranch(), request.getState(), request.getCategory(),
                    request.getTerm(), request.getName(), request.getPage(), request.getSize()
            );
        }
        return shippingPortOut.searchDynamic(
                request.getBranch(), request.getState(), request.getCategory(),
                request.getTerm(), request.getName(), request.getPage(), request.getSize()
        );
    }
}