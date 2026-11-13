package com.example.xpjact4_t4.exception;

/** 401: la petición no trae un token válido. */
public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
