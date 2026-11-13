package com.example.xpjact4_t4.dto;

import java.time.LocalDateTime;

/** Lo único que la API muestra de un usuario. NO incluye la contraseña. */
public record UsuarioDTO(
        Long id,
        String nombreCompleto,
        String correo,
        String rol,
        LocalDateTime fechaRegistro) {
}
