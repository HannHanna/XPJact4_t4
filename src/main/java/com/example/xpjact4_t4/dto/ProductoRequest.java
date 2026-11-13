package com.example.xpjact4_t4.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Datos para crear (POST) o reemplazar (PUT) un producto. */
public record ProductoRequest(

        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "^[A-Z]{3}-\\d{3,5}$", message = "El código debe tener el formato ABC-001")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio admite máximo 2 decimales")
        BigDecimal precio,

        @NotNull(message = "Las existencias son obligatorias")
        @PositiveOrZero(message = "Las existencias no pueden ser negativas")
        Integer existencias,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId) {
}
