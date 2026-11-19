package com.yugioh.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class JugadorTest {

    private Jugador jugador;

    @Before
    public void setUp() {
        jugador = new Jugador("Yugi");
    }

    @Test
    public void empiezaConLpIniciales() {
        assertEquals(Jugador.LP_INICIALES, jugador.getPuntosDeVida());
        assertFalse(jugador.estaDerrotado());
    }

    @Test
    public void constructorSobrecargado_aceptaLpPersonalizados() {
        assertEquals(4000, new Jugador("Joey", 4000).getPuntosDeVida());
    }

    @Test
    public void recibirDanio_restaLp() {
        assertEquals(1500, jugador.recibirDanio(1500));
        assertEquals(6500, jugador.getPuntosDeVida());
    }

    @Test
    public void recibirDanio_noBajaDeCero() {
        int aplicado = jugador.recibirDanio(9000);
        assertEquals(8000, aplicado);
        assertEquals(0, jugador.getPuntosDeVida());
        assertTrue(jugador.estaDerrotado());
    }

    @Test
    public void recibirDanioCero_noCambiaLp() {
        jugador.recibirDanio(0);
        assertEquals(8000, jugador.getPuntosDeVida());
    }

    @Test(expected = IllegalArgumentException.class)
    public void danioNegativo_noSePermite() {
        jugador.recibirDanio(-100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nombreVacio_noSePermite() {
        new Jugador("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void lpInicialesInvalidos_noSePermiten() {
        new Jugador("Yugi", 0);
    }

    @Test
    public void invocarYDestruir_mueveAlCementerio() {
        Monstruo m = new MonstruoNormal("Mago", 2500, 2100, Posicion.ATAQUE);
        jugador.invocar(m);
        assertTrue(jugador.controla(m));
        jugador.destruir(m);
        assertFalse(jugador.controla(m));
        assertEquals(1, jugador.getCementerio().size());
    }

    @Test(expected = AccionInvalidaException.class)
    public void noMasDeCincoMonstruos() {
        for (int i = 0; i < 6; i++) {
            jugador.invocar(new MonstruoNormal("M" + i, 100, 100, Posicion.ATAQUE));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void campoNoSePuedeModificarDesdeFuera() {
        jugador.getCampo().add(new MonstruoNormal("Trampa", 1, 1, Posicion.ATAQUE));
    }
}