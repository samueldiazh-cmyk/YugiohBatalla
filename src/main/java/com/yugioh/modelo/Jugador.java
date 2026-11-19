package com.yugioh.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Jugador {

    public static final int LP_INICIALES = 8000;
    public static final int MAX_MONSTRUOS_EN_CAMPO = 5;

    private final String nombre;
    private int puntosDeVida;
    private final List<Monstruo> campo = new ArrayList<>();
    private final List<Monstruo> cementerio = new ArrayList<>();

    public Jugador(String nombre) {
        this(nombre, LP_INICIALES);
    }

    public Jugador(String nombre, int puntosDeVidaIniciales) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
        }
        if (puntosDeVidaIniciales <= 0) {
            throw new IllegalArgumentException("Los LP iniciales deben ser mayores que 0.");
        }
        this.nombre = nombre.trim();
        this.puntosDeVida = puntosDeVidaIniciales;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntosDeVida() {
        return puntosDeVida;
    }

    /** Resta daño a los LP sin bajar de 0. Devuelve el daño realmente aplicado. */
    public int recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        int aplicado = Math.min(danio, puntosDeVida);
        puntosDeVida -= aplicado;
        return aplicado;
    }

    public boolean estaDerrotado() {
        return puntosDeVida == 0;
    }

    public void invocar(Monstruo monstruo) {
        if (monstruo == null) {
            throw new IllegalArgumentException("No hay monstruo para invocar.");
        }
        if (campo.size() >= MAX_MONSTRUOS_EN_CAMPO) {
            throw new AccionInvalidaException(
                    nombre + " ya tiene " + MAX_MONSTRUOS_EN_CAMPO + " monstruos en el campo.");
        }
        if (campo.contains(monstruo)) {
            throw new AccionInvalidaException(monstruo.getNombre() + " ya está en el campo.");
        }
        campo.add(monstruo);
    }

    /** Saca al monstruo del campo y lo envía al cementerio. */
    public void destruir(Monstruo monstruo) {
        if (!campo.remove(monstruo)) {
            throw new AccionInvalidaException(nombre + " no controla a ese monstruo.");
        }
        cementerio.add(monstruo);
    }

    public boolean controla(Monstruo monstruo) {
        return campo.contains(monstruo);
    }

    public boolean tieneMonstruos() {
        return !campo.isEmpty();
    }

    public List<Monstruo> getCampo() {
        return Collections.unmodifiableList(campo);
    }

    public List<Monstruo> getCementerio() {
        return Collections.unmodifiableList(cementerio);
    }
}