package com.example.xpjact4_t4.exception;

/** 409: se intenta guardar un dato que debe ser único (correo o código de producto). */
public class DuplicadoException extends RuntimeException {

    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
