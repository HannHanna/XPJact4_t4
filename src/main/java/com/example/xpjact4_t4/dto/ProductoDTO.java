package com.example.xpjact4_t4.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Producto tal como lo regresa la API, con un resumen de su categoría. */
public record ProductoDTO(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer existencias,
        CategoriaDTO categoria,
        LocalDateTime creado,
        LocalDateTime modificado) {
}
