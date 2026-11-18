package com.yugioh.modelo;


public abstract class Carta {

    public static final int LONGITUD_MAXIMA_NOMBRE = 30;

    private static int totalCartasCreadas = 0;

    private final int id;
    private final String nombre;

    protected Carta(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la carta es obligatorio.");
        }
        String limpio = nombre.trim();
        if (limpio.length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar " + LONGITUD_MAXIMA_NOMBRE + " caracteres.");
        }
        this.nombre = limpio;
        this.id = ++totalCartasCreadas;
    }

    public final int getId() {
        return id;
    }

    public final String getNombre() {
        return nombre;
    }

    public static int getTotalCartasCreadas() {
        return totalCartasCreadas;
    }

  
    public abstract String getTipo();

    
    public String describir() {
        return "[" + getTipo() + "] " + nombre;
    }
}