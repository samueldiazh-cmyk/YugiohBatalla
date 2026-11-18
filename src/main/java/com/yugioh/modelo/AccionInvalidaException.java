package com.yugioh.modelo;

/**
 * Se lanza cuando una acción no respeta las reglas del duelo
 * (atacar en defensa, atacar dos veces en el mismo turno, etc.).
 */
public class AccionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}