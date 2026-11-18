package com.yugioh.modelo;


public abstract class Monstruo extends Carta {

    public static final int PUNTOS_MAXIMOS = 9999;

    private int ataque;
    private int defensa;
    private Posicion posicion;

    protected Monstruo(String nombre, int ataque, int defensa, Posicion posicion) {
        super(nombre);
        setAtaque(ataque);
        setDefensa(defensa);
        setPosicion(posicion);
    }

    public int getAtaque() {
        return ataque;
    }

    public final void setAtaque(int ataque) {
        this.ataque = validarPuntos(ataque, "ATQ");
    }

    public int getDefensa() {
        return defensa;
    }

    public final void setDefensa(int defensa) {
        this.defensa = validarPuntos(defensa, "DEF");
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public final void setPosicion(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posición es obligatoria.");
        }
        this.posicion = posicion;
    }

    public boolean estaEnAtaque() {
        return posicion == Posicion.ATAQUE;
    }

    public void cambiarPosicion() {
        posicion = posicion.invertir();
    }

  
    public int getPuntosEnCombate() {
        return estaEnAtaque() ? ataque : defensa;
    }

    private static int validarPuntos(int valor, String campo) {
        if (valor < 0 || valor > PUNTOS_MAXIMOS) {
            throw new IllegalArgumentException(
                    campo + " debe estar entre 0 y " + PUNTOS_MAXIMOS + ".");
        }
        return valor;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) ATQ %d / DEF %d - %s",
                getNombre(), getTipo(), ataque, defensa, posicion);
    }
}