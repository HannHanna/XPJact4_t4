package com.example.xpjact4_t4.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/** Envoltura de una página de resultados (en lugar de regresar el Page de Spring tal cual). */
public record PaginaDTO<T>(
        List<T> datos,
        int paginaActual,
        int elementosPorPagina,
        long totalElementos,
        int totalPaginas,
        boolean hayMas) {

    public static <T> PaginaDTO<T> desde(Page<T> page) {
        return new PaginaDTO<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }
}
