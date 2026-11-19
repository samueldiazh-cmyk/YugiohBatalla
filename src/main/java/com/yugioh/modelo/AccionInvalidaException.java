package com.yugioh.modelo;


public class AccionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}