package com.ms3.shippingservice.infrastructure.dto.request;

import com.ms3.shippingservice.domain.model.Shipping;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateShippingRequest {

    @Pattern(regexp = "^\\d{8}$", message = "DNI remitente debe tener 8 digitos")
    @NotBlank(message = "El DNI del remitente es obligatorio.")
    private String dniRemitente;

    @Pattern(regexp = "^\\d{8}$", message = "DNI Destinatario debe tener 8 digitos")
    @NotBlank(message = "El DNI del destinatario es obligatorio.")
    private String dniDestinatario;

    @NotBlank(message = "Se necesita ingresar una descripción.")
    @Size(min = 10, max = 255,
            message = "La descripción debe tener entre 10 y 255 caracteres.")
    private String description;

    @NotBlank(message = "La sucursal de origen no puede estar vacía.")
    private String originBranch;

    @NotBlank(message = "La sucursal de destino no puede estar vacía.")
    private String destinationBranch;

    @Positive(message = "El peso debe ser mayor a cero.")
    private double weight;

    @NotNull(message = "El valor declarado es obligatorio.")
    private BigDecimal declaredValue;

    @NotEmpty(message = "La lista de categorías no puede estar vacía")
    private List<UUID> categoryIds;

    public Shipping toDomain() {
        Shipping domain = new Shipping();
        domain.setDniRemitente(this.dniRemitente);
        domain.setDniDestinatario(this.dniDestinatario);
        domain.setDescription(this.description);
        domain.setOriginBranch(this.originBranch);
        domain.setDestinationBranch(this.destinationBranch);
        domain.setWeight(this.weight);
        domain.setDeclaredValue(this.declaredValue);
        return domain;
    }
}
