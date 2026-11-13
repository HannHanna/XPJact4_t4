package com.example.xpjact4_t4.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Cuerpo de todas las respuestas de error de la API. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        LocalDateTime fecha,
        int codigo,
        String error,
        String detalle,
        String path,
        List<CampoInvalido> campos) {

    public record CampoInvalido(String campo, String problema) {
    }

    public static ApiError simple(int codigo, String error, String detalle, String path) {
        return new ApiError(LocalDateTime.now(), codigo, error, detalle, path, List.of());
    }
}
