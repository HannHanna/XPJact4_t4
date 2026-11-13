package com.example.xpjact4_t4.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Actualización parcial (PATCH): solo se cambian los campos que vengan con valor.
 * Útil para ajustar precio o existencias sin mandar el producto completo.
 */
public record ProductoPatchRequest(

        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite máximo 2 decimales")
        BigDecimal precio,

        @PositiveOrZero(message = "Las existencias no pueden ser negativas")
        Integer existencias) {
}
