package com.yugioh.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class CalculadoraDanioTest {

    private ReglaDanio regla;

    @Before
    public void setUp() {
        regla = new CalculadoraDanio();
    }

    private static Monstruo ataque(String nombre, int atq, int def) {
        return new MonstruoNormal(nombre, atq, def, Posicion.ATAQUE);
    }

    private static Monstruo defensa(String nombre, int atq, int def) {
        return new MonstruoNormal(nombre, atq, def, Posicion.DEFENSA);
    }

   
    @Test
    public void ataqueVsAtaque_atacanteMasFuerte_destruyeYDaniaAlRival() {
        ResultadoAtaque r = regla.calcular(ataque("Mago", 2500, 2100), ataque("Guerrero", 1400, 1200));
        assertEquals(1100, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.DEFENSOR, r.getJugadorAfectado());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    public void ataqueVsAtaque_atacanteMasDebil_esDestruidoYRecibeDanio() {
        ResultadoAtaque r = regla.calcular(ataque("Guerrero", 1400, 1200), ataque("Dragón", 3000, 2500));
        assertEquals(1600, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.ATACANTE, r.getJugadorAfectado());
        assertTrue(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

    @Test
    public void ataqueVsAtaque_empate_destruyeAmbosSinDanio() {
        ResultadoAtaque r = regla.calcular(ataque("A", 1500, 1000), ataque("B", 1500, 800));
        assertEquals(0, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.NINGUNO, r.getJugadorAfectado());
        assertTrue(r.isAtacanteDestruido());
        assertTrue(r.isDefensorDestruido());
    }

    @Test
    public void ataqueVsAtaque_ambosEnCero_noPasaNada() {
        ResultadoAtaque r = regla.calcular(ataque("A", 0, 0), ataque("B", 0, 0));
        assertEquals(0, r.getDanio());
        assertFalse(r.isAtacanteDestruido());
        assertFalse(r.isDefensorDestruido());
    }

 
    @Test
    public void ataqueVsDefensa_atqMayor_destruyeSinDanio() {
        ResultadoAtaque r = regla.calcular(ataque("Mago", 2500, 2100), defensa("Gigante", 1200, 1000));
        assertEquals(0, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.NINGUNO, r.getJugadorAfectado());
        assertTrue(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    public void ataqueVsDefensa_atqMenor_reboteAlAtacanteSinDestruccion() {
        ResultadoAtaque r = regla.calcular(ataque("Kuriboh", 300, 200), defensa("Muro", 0, 2000));
        assertEquals(1700, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.ATACANTE, r.getJugadorAfectado());
        assertFalse(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    public void ataqueVsDefensa_iguales_noPasaNada() {
        ResultadoAtaque r = regla.calcular(ataque("A", 1000, 0), defensa("B", 0, 1000));
        assertEquals(0, r.getDanio());
        assertFalse(r.isDefensorDestruido());
        assertFalse(r.isAtacanteDestruido());
    }

    @Test
    public void ataqueVsDefensa_usaDefYNoAtqDelDefensor() {
        // ATQ del defensor (3000) es mayor, pero en defensa solo cuenta su DEF (500)
        ResultadoAtaque r = regla.calcular(ataque("A", 1000, 0), defensa("B", 3000, 500));
        assertTrue(r.isDefensorDestruido());
        assertEquals(0, r.getDanio());
    }


    @Test
    public void ataqueDirecto_infligeTodoElAtq() {
        ResultadoAtaque r = regla.calcular(ataque("Dragón", 3000, 2500));
        assertEquals(3000, r.getDanio());
        assertEquals(ResultadoAtaque.Afectado.DEFENSOR, r.getJugadorAfectado());
        assertTrue(r.esAtaqueDirecto());
        assertEquals("Ataque directo", r.getNombreDefensor());
    }

   
    @Test(expected = AccionInvalidaException.class)
    public void atacanteEnDefensa_noPuedeAtacar() {
        regla.calcular(defensa("A", 1000, 1000), ataque("B", 500, 500));
    }

    @Test(expected = AccionInvalidaException.class)
    public void atacanteEnDefensa_noPuedeAtacarDirecto() {
        regla.calcular(defensa("A", 1000, 1000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void monstruoNoPuedeAtacarseASiMismo() {
        Monstruo m = ataque("A", 1000, 1000);
        regla.calcular(m, m);
    }

    @Test(expected = NullPointerException.class)
    public void defensorNulo_lanzaExcepcion() {
        regla.calcular(ataque("A", 1000, 1000), null);
    }
}