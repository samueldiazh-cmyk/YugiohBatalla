package com.yugioh.modelo;


public class MonstruoNormal extends Monstruo {

    private static final String DESCRIPCION_POR_DEFECTO = "Monstruo sin efecto.";

    private final String descripcion;

    
    public MonstruoNormal(String nombre, int ataque, int defensa, Posicion posicion) {
        this(nombre, ataque, defensa, posicion, DESCRIPCION_POR_DEFECTO);
    }

    public MonstruoNormal(String nombre, int ataque, int defensa, Posicion posicion, String descripcion) {
        super(nombre, ataque, defensa, posicion);
        this.descripcion = (descripcion == null || descripcion.isBlank())
                ? DESCRIPCION_POR_DEFECTO : descripcion.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

   
    @Override
    public String getTipo() {
        return "Normal";
    }

    
    @Override
    public String describir() {
        return super.describir() + " - " + descripcion;
    }
}