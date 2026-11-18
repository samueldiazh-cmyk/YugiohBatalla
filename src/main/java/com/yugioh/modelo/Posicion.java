package com.yugioh.modelo;


public enum Posicion {

    ATAQUE("Ataque"),
    DEFENSA("Defensa");

    private final String etiqueta;

    Posicion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

   
    public Posicion invertir() {
        return this == ATAQUE ? DEFENSA : ATAQUE;
    }

 
    public static Posicion desdeEtiqueta(String texto) {
        for (Posicion p : values()) {
            if (p.etiqueta.equalsIgnoreCase(texto) || p.name().equalsIgnoreCase(texto)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Posición desconocida: " + texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}