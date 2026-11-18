package com.yugioh.modelo;


public class MonstruoEfecto extends Monstruo {

    private final String efecto;

    public MonstruoEfecto(String nombre, int ataque, int defensa, Posicion posicion, String efecto) {
        super(nombre, ataque, defensa, posicion);
        this.efecto = (efecto == null || efecto.isBlank())
                ? "Efecto no especificado." : efecto.trim();
    }

    public String getEfecto() {
        return efecto;
    }

    @Override
    public String getTipo() {
        return "Efecto";
    }

    @Override
    public String describir() {
        return super.describir() + " | Efecto: " + efecto;
    }
}