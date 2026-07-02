package com.ms3.shippingservice.infrastructure.controller;

import com.ms3.shippingservice.domain.model.ShippingState;
import com.ms3.shippingservice.domain.model.ShippingStateHistory;
import com.ms3.shippingservice.domain.ports.in.ShippingPortIn;
import com.ms3.shippingservice.infrastructure.dto.request.CreateShippingRequest;
import com.ms3.shippingservice.infrastructure.dto.request.UpdateStateRequest;
import com.ms3.shippingservice.infrastructure.dto.response.ApiResponse;
import com.ms3.shippingservice.infrastructure.dto.response.ShippingResponseDTO;
import com.ms3.shippingservice.infrastructure.exception.ResourceNotFoundException;
import com.ms3.shippingservice.infrastructure.mapper.ShippingResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shippings")
public class ShippingController {

    private final ShippingPortIn shippingPortIn;
    private final ShippingResponseMapper mapper; // Inyectamos el mapper

    public ShippingController(ShippingPortIn shippingPortIn, ShippingResponseMapper mapper) {
        this.shippingPortIn = shippingPortIn;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> createShipping(
            @Valid @RequestBody CreateShippingRequest request,
            @AuthenticationPrincipal String operatorUsername) {

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                true,
                "Registro satisfactorio",
                mapper.toResponse(shippingPortIn.createShipping(request.toDomain(), request.getCategoryIds(), operatorUsername))
        ));
    }

    @PatchMapping("/{id}/state")
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> updateState(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStateRequest request,
            @AuthenticationPrincipal String operatorUsername) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Se actualizó el estado satisfactoriamente",
                mapper.toResponse(shippingPortIn.updateShippingState(id, request.getNewState(), operatorUsername))
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Envío encontrado",
                mapper.toResponse(shippingPortIn.getById(id))));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ShippingResponseDTO>>> getByBranchAndState(
            @RequestParam String branch,
            @RequestParam ShippingState state) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Lista filtrada por sucursal - estado",
                mapper.toResponseList(shippingPortIn.getByBranchAndState(branch, state))
        ));
    }

    @GetMapping("/filter/category")
    public ResponseEntity<ApiResponse<List<ShippingResponseDTO>>> getByCategoryAndState(
            @RequestParam String category,
            @RequestParam ShippingState state) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Lista filtrada por categoria - estado",
                mapper.toResponseList(shippingPortIn.getByCategoryAndState(category, state))
        ));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ShippingResponseDTO>>> getAll() {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Envios Registrados",
                mapper.toResponseList(shippingPortIn.getAllShipping())
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShippingResponseDTO>>> searchByTrackingPartial(@RequestParam String term) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Coincidencias parciales de envío",
                mapper.toResponseList(shippingPortIn.searchByTrackingPartial(term))
        ));
    }
    @GetMapping("/searchName")
    public ResponseEntity<ApiResponse<List<ShippingResponseDTO>>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Coincidencias de nombre encontradas",
                mapper.toResponseList(shippingPortIn.getByName(name))
        ));
    }

    @PatchMapping("/{id}/revert")
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> revertState(
            @PathVariable UUID id,
            @AuthenticationPrincipal String operatorUsername) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Estado revertido exitosamente",
                mapper.toResponse(shippingPortIn.revertShippingState(id, operatorUsername))
        ));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> getByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Envío encontrado por seguimiento",
                mapper.toResponse(shippingPortIn.getByTrackingNumber(trackingNumber))
        ));
    }

    @GetMapping("/tracking/{trackingNumber}/history")
    public ResponseEntity<ApiResponse<List<ShippingStateHistory>>> getHistoryByTracking(
            @PathVariable String trackingNumber) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Historial recuperado exitosamente",
                shippingPortIn.getHistoryByTracking(trackingNumber)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ShippingResponseDTO>> deleteShipping(
            @PathVariable UUID id,
            @AuthenticationPrincipal String operatorUsername) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Se eliminó el envío satisfactoriamente",
                mapper.toResponse(shippingPortIn.deleteShipping(id, operatorUsername))
        ));
    }
}