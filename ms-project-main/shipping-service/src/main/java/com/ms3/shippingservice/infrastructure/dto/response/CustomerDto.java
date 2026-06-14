package com.ms3.shippingservice.infrastructure.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDto {
    private UUID id;
    private boolean active;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
