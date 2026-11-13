package com.example.xpjact4_t4.dto;

import java.time.Instant;

/** Respuesta del login: el JWT y a quién pertenece. */
public record TokenResponse(
        String accessToken,
        String tokenType,
        Instant expiraEn,
        UsuarioDTO usuario) {
}
