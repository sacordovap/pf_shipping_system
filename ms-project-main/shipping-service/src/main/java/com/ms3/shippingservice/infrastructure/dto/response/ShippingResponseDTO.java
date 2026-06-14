package com.ms3.shippingservice.infrastructure.dto.response;

import com.ms3.shippingservice.domain.model.Shipping;
import com.ms3.shippingservice.domain.model.ShippingState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ShippingResponseDTO {
//    private UUID id;
    private String trackingNumber;

    private String dniRemitente;
    private String remitente;
    private String dniDestinatario;
    private String destinatario;
    private String description;

    private String originBranch;
    private String destinationBranch;
    private double weight;
    private BigDecimal declaredValue;
    private BigDecimal shippingCost;
    private ShippingState currentState;
    private List<String> categories;
    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

    public static ShippingResponseDTO fromDomain(Shipping domain) {
        if (domain == null) return null;

        ShippingResponseDTO dto = new ShippingResponseDTO();
//        dto.setId(domain.getId());
        dto.setTrackingNumber(domain.getTrackingNumber());

        dto.setDniRemitente(domain.getDniRemitente());
        dto.setRemitente(domain.getRemitente());

        dto.setDniDestinatario(domain.getDniDestinatario());
        dto.setDestinatario(domain.getDestinatario());

        dto.setDescription(domain.getDescription());

        dto.setOriginBranch(domain.getOriginBranch());
        dto.setDestinationBranch(domain.getDestinationBranch());

        dto.setWeight(domain.getWeight());
        dto.setDeclaredValue(domain.getDeclaredValue());
        dto.setShippingCost(domain.getShippingCost());
        dto.setCurrentState(domain.getCurrentState());
        dto.setCreatedAt(domain.getCreatedAt());
//        dto.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getCategories() != null) {
            dto.setCategories(domain.getCategories().stream()
                    .map(c -> c.getName())
                    .toList());
        }

        return dto;
    }
}