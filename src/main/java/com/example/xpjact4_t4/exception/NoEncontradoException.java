package com.example.xpjact4_t4.exception;

/** 404: el registro buscado no existe. */
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String recurso, Object id) {
        super(recurso + " con id " + id + " no existe");
    }
}
