package com.yugioh.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class BatallaTest {

    private Jugador yugi;
    private Jugador kaiba;
    private Batalla batalla;
    private Monstruo mago;
    private Monstruo dragon;

    @Before
    public void setUp() {
        yugi = new Jugador("Yugi");
        kaiba = new Jugador("Kaiba");
        mago = new MonstruoNormal("Mago Oscuro", 2500, 2100, Posicion.ATAQUE);
        dragon = new MonstruoNormal("Dragón", 3000, 2500, Posicion.ATAQUE);
        yugi.invocar(mago);
        kaiba.invocar(dragon);
        batalla = new Batalla(yugi, kaiba);
    }

    @Test
    public void ataquePerdido_restaLpAlAtacanteYLoDestruye() {
        batalla.atacar(mago, dragon);
        assertEquals(7500, yugi.getPuntosDeVida());
        assertEquals(8000, kaiba.getPuntosDeVida());
        assertFalse(yugi.controla(mago));
        assertTrue(kaiba.controla(dragon));
    }

    @Test
    public void ataqueGanado_restaLpAlRivalYDestruyeDefensor() {
        batalla.terminarTurno();
        batalla.atacar(dragon, mago);
        assertEquals(7500, yugi.getPuntosDeVida());
        assertFalse(yugi.controla(mago));
        assertEquals(1, yugi.getCementerio().size());
    }

    @Test
    public void ataqueDirecto_soloSiRivalNoTieneMonstruos() {
        kaiba.destruir(dragon);
        batalla.atacarDirectamente(mago);
        assertEquals(5500, kaiba.getPuntosDeVida());
    }

    @Test(expected = AccionInvalidaException.class)
    public void ataqueDirecto_conDefensores_noSePermite() {
        batalla.atacarDirectamente(mago);
    }

    @Test(expected = AccionInvalidaException.class)
    public void unMonstruoNoAtacaDosVecesPorTurno() {
        kaiba.invocar(new MonstruoNormal("Kuriboh", 300, 200, Posicion.DEFENSA));
        Monstruo kuriboh = kaiba.getCampo().get(1);
        batalla.atacar(mago, kuriboh);
        batalla.atacar(mago, dragon);
    }

    @Test
    public void terminarTurno_permiteVolverAAtacar() {
        kaiba.destruir(dragon);
        batalla.atacarDirectamente(mago);
        batalla.terminarTurno();
        batalla.terminarTurno();
        batalla.atacarDirectamente(mago);
        assertEquals(3000, kaiba.getPuntosDeVida());
        assertEquals(3, batalla.getNumeroTurno());
    }

    @Test(expected = AccionInvalidaException.class)
    public void noSePuedeAtacarConMonstruoDelRival() {
        batalla.atacar(dragon, mago);
    }

    @Test
    public void lpEnCero_terminaElDueloYHayGanador() {
        kaiba.destruir(dragon);
        assertNull(batalla.getGanador());
        kaiba.recibirDanio(6000);
        batalla.atacarDirectamente(mago);
        assertTrue(batalla.haTerminado());
        assertEquals(0, kaiba.getPuntosDeVida());
        assertSame(yugi, batalla.getGanador());
    }

    @Test(expected = AccionInvalidaException.class)
    public void dueloTerminado_noAdmiteMasAcciones() {
        kaiba.recibirDanio(8000);
        batalla.terminarTurno();
    }

    @Test
    public void cambiarPosicion_unaVezPorTurno() {
        batalla.cambiarPosicion(mago);
        assertEquals(Posicion.DEFENSA, mago.getPosicion());
        try {
            batalla.cambiarPosicion(mago);
            throw new AssertionError("Debía impedir un segundo cambio");
        } catch (AccionInvalidaException esperado) {
            assertEquals(Posicion.DEFENSA, mago.getPosicion());
        }
    }

    @Test
    public void registroGuardaCadaAtaque() {
        batalla.atacar(mago, dragon);
        assertEquals(1, batalla.getRegistro().size());
        assertEquals(500, batalla.getRegistro().get(0).getDanio());
    }
}