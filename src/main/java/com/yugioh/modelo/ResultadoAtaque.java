package com.yugioh.modelo;


 
public final class ResultadoAtaque {

    
    public enum Afectado { NINGUNO, ATACANTE, DEFENSOR }

    private final String nombreAtacante;
    private final String nombreDefensor;
    private final int danio;
    private final Afectado jugadorAfectado;
    private final boolean atacanteDestruido;
    private final boolean defensorDestruido;
    private final String resumen;

    ResultadoAtaque(String nombreAtacante, String nombreDefensor, int danio, Afectado jugadorAfectado,
            boolean atacanteDestruido, boolean defensorDestruido, String resumen) {
        this.nombreAtacante = nombreAtacante;
        this.nombreDefensor = nombreDefensor;
        this.danio = danio;
        this.jugadorAfectado = jugadorAfectado;
        this.atacanteDestruido = atacanteDestruido;
        this.defensorDestruido = defensorDestruido;
        this.resumen = resumen;
    }

    public String getNombreAtacante() {
        return nombreAtacante;
    }

    /** Nombre del monstruo defensor, o "Ataque directo" si no había. */
    public String getNombreDefensor() {
        return nombreDefensor == null ? "Ataque directo" : nombreDefensor;
    }

    public boolean esAtaqueDirecto() {
        return nombreDefensor == null;
    }

    public int getDanio() {
        return danio;
    }

    public Afectado getJugadorAfectado() {
        return jugadorAfectado;
    }

    public boolean isAtacanteDestruido() {
        return atacanteDestruido;
    }

    public boolean isDefensorDestruido() {
        return defensorDestruido;
    }

    public String getResumen() {
        return resumen;
    }
}