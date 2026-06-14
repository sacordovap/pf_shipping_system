package com.ms2.customerservice;

import com.ms2.customerservice.application.services.CustomerService;
import com.ms2.customerservice.domain.model.Customer;
import com.ms2.customerservice.domain.ports.out.CustomerRepositoryPortOut;
import com.ms2.customerservice.domain.ports.out.ReniecPortOut;
import com.ms2.customerservice.infraestructure.dto.request.AddressRequest;
import com.ms2.customerservice.infraestructure.dto.request.CustomerRegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JUSTIFICACIÓN REQUERIDA POR LA RÚBRICA SOBRE @Spy:
 * No se incluye el uso de @Spy en esta clase de prueba debido a que CustomerService
 * no interactúa con componentes auxiliares compartidos que requieran mantener un
 * comportamiento real parcial (como algoritmos matemáticos o calculadores de tarifas mutables).
 * Todas sus dependencias externas corresponden a Puertos de Salida (I/O) puros,
 * por lo cual el aislamiento total mediante @Mock es la práctica óptima de diseño.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPortOut customerRepositoryPortOut;

    @Mock
    private ReniecPortOut reniecPortOut;

    @InjectMocks
    private CustomerService customerService;

    // =========================================================================
    // HAPPY PATH
    // =========================================================================
    @Test
    void registerCustomer_WhenValidRequest_ShouldReturnSavedCustomer() {
        // GIVEN
        AddressRequest address = new AddressRequest("Av. Las Flores 123", "Arequipa", "Arequipa");
        CustomerRegisterRequest request = new CustomerRegisterRequest(
                "74713501", "juan.perez@example.com", "+51999888777", List.of(address)
        );

        Customer customerDomain = request.toDomain();

        when(customerRepositoryPortOut.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(customerRepositoryPortOut.findByDni(request.getDni())).thenReturn(Optional.empty());
        when(reniecPortOut.fetchByDni(request.getDni())).thenReturn(Optional.of("JUAN PEREZ FLORES"));

        Customer mockSaved = new Customer();
        mockSaved.setId(UUID.randomUUID());
        mockSaved.setDni(request.getDni());
        mockSaved.setFullName("JUAN PEREZ FLORES");
        mockSaved.setEmail(request.getEmail());
        mockSaved.setActive(true);

        when(customerRepositoryPortOut.save(any(Customer.class))).thenReturn(mockSaved);

        // WHEN
        Customer result = customerService.registerCustomer(customerDomain);

        // THEN
        assertNotNull(result);
        assertEquals("JUAN PEREZ FLORES", result.getFullName());
        verify(customerRepositoryPortOut, times(1)).save(any(Customer.class));
    }

    // =========================================================================
    // RUTA DE EXCEPCIÓN
    // =========================================================================
    @Test
    void registerCustomer_WhenEmailAlreadyExists_ShouldThrowRuntimeException() {
        // GIVEN
        CustomerRegisterRequest request = new CustomerRegisterRequest(
                "74713501", "juan.perez@example.com", "+51999888777", List.of()
        );

        Customer customerDomain = request.toDomain();
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("juan.perez@example.com");

        when(customerRepositoryPortOut.findByEmail(request.getEmail())).thenReturn(Optional.of(existingCustomer));

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.registerCustomer(customerDomain);
        });

        assertEquals("Email already registered: juan.perez@example.com", exception.getMessage());
        verify(customerRepositoryPortOut, never()).save(any(Customer.class));
    }

    // =========================================================================
    // CASO DE RESULTADO VACÍO
    // =========================================================================
    @Test
    void getAllCustomers_WhenDatabaseIsEmpty_ShouldReturnEmptyList() {
        // GIVEN
        when(customerRepositoryPortOut.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<Customer> result = customerService.getAllCustomers();

        // THEN
        assertNotNull(result, "La lista no debe ser nula");
        assertTrue(result.isEmpty(), "La lista debe estar completamente vacía (Resultado Vacío)");
        assertEquals(0, result.size());

        verify(customerRepositoryPortOut, times(1)).findAll();
    }
}