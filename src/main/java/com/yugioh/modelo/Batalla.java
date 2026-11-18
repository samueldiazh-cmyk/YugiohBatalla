package com.yugioh.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Coordina el duelo: turnos, validación de reglas y aplicación del
 * ResultadoAtaque sobre los jugadores (LP y destrucción).
 */
public class Batalla {

    private final Jugador[] jugadores;
    private final ReglaDanio regla;
    private final Set<Monstruo> yaAtacaron = new HashSet<>();
    private final Set<Monstruo> cambiaronPosicion = new HashSet<>();
    private final List<ResultadoAtaque> registro = new ArrayList<>();
    private int indiceTurno = 0;
    private int numeroTurno = 1;

    public Batalla(Jugador jugador1, Jugador jugador2) {
        this(jugador1, jugador2, new CalculadoraDanio());
    }

    public Batalla(Jugador jugador1, Jugador jugador2, ReglaDanio regla) {
        Objects.requireNonNull(jugador1, "Falta el jugador 1.");
        Objects.requireNonNull(jugador2, "Falta el jugador 2.");
        if (jugador1 == jugador2) {
            throw new IllegalArgumentException("Un jugador no puede enfrentarse a sí mismo.");
        }
        this.jugadores = new Jugador[]{jugador1, jugador2};
        this.regla = Objects.requireNonNull(regla, "Falta la regla de daño.");
    }

    public ResultadoAtaque atacar(Monstruo atacante, Monstruo objetivo) {
        verificarPuedeAtacar(atacante);
        if (objetivo == null || !getRival().controla(objetivo)) {
            throw new AccionInvalidaException("El objetivo debe ser un monstruo del campo rival.");
        }
        ResultadoAtaque resultado = regla.calcular(atacante, objetivo);
        aplicar(resultado, atacante, objetivo);
        return resultado;
    }

    public ResultadoAtaque atacarDirectamente(Monstruo atacante) {
        verificarPuedeAtacar(atacante);
        if (getRival().tieneMonstruos()) {
            throw new AccionInvalidaException(
                    "Solo puedes atacar directamente si " + getRival().getNombre() + " no tiene monstruos.");
        }
        ResultadoAtaque resultado = regla.calcular(atacante);
        aplicar(resultado, atacante, null);
        return resultado;
    }

    public void cambiarPosicion(Monstruo monstruo) {
        verificarDueloActivo();
        if (monstruo == null || !getJugadorEnTurno().controla(monstruo)) {
            throw new AccionInvalidaException("Solo puedes cambiar la posición de tus propios monstruos.");
        }
        if (yaAtacaron.contains(monstruo)) {
            throw new AccionInvalidaException(monstruo.getNombre() + " ya atacó en este turno.");
        }
        if (!cambiaronPosicion.add(monstruo)) {
            throw new AccionInvalidaException(monstruo.getNombre() + " ya cambió de posición en este turno.");
        }
        monstruo.cambiarPosicion();
    }

    public void terminarTurno() {
        verificarDueloActivo();
        yaAtacaron.clear();
        cambiaronPosicion.clear();
        indiceTurno = 1 - indiceTurno;
        numeroTurno++;
    }

    private void aplicar(ResultadoAtaque r, Monstruo atacante, Monstruo objetivo) {
        Jugador enTurno = getJugadorEnTurno();
        Jugador rival = getRival();
        switch (r.getJugadorAfectado()) {
            case ATACANTE:
                enTurno.recibirDanio(r.getDanio());
                break;
            case DEFENSOR:
                rival.recibirDanio(r.getDanio());
                break;
            default:
                break;
        }
        if (r.isAtacanteDestruido()) {
            enTurno.destruir(atacante);
        }
        if (r.isDefensorDestruido()) {
            rival.destruir(objetivo);
        }
        yaAtacaron.add(atacante);
        registro.add(r);
    }

    private void verificarPuedeAtacar(Monstruo atacante) {
        verificarDueloActivo();
        if (atacante == null || !getJugadorEnTurno().controla(atacante)) {
            throw new AccionInvalidaException("El atacante debe ser un monstruo de tu campo.");
        }
        if (yaAtacaron.contains(atacante)) {
            throw new AccionInvalidaException(atacante.getNombre() + " ya atacó en este turno.");
        }
    }

    private void verificarDueloActivo() {
        if (haTerminado()) {
            throw new AccionInvalidaException("El duelo terminó. Ganador: " + getGanador().getNombre() + ".");
        }
    }

    public boolean haTerminado() {
        return jugadores[0].estaDerrotado() || jugadores[1].estaDerrotado();
    }

    /** Devuelve el ganador, o null si el duelo sigue. */
    public Jugador getGanador() {
        if (jugadores[0].estaDerrotado()) {
            return jugadores[1];
        }
        if (jugadores[1].estaDerrotado()) {
            return jugadores[0];
        }
        return null;
    }

    public boolean yaAtaco(Monstruo monstruo) {
        return yaAtacaron.contains(monstruo);
    }

    public Jugador getJugador(int indice) {
        return jugadores[indice];
    }

    public Jugador getJugadorEnTurno() {
        return jugadores[indiceTurno];
    }

    public Jugador getRival() {
        return jugadores[1 - indiceTurno];
    }

    public int getIndiceJugadorEnTurno() {
        return indiceTurno;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public List<ResultadoAtaque> getRegistro() {
        return Collections.unmodifiableList(registro);
    }
}