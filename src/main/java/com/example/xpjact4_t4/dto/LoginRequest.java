package com.example.xpjact4_t4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Datos que se reciben en POST /api/auth/login */
public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Escribe un correo válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena) {
}
