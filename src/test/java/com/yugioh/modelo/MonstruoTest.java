package com.yugioh.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class MonstruoTest {

    @Test
    public void cambiarPosicion_alternaEntreAtaqueYDefensa() {
        Monstruo m = new MonstruoNormal("Mago", 2500, 2100, Posicion.ATAQUE);
        assertEquals(2500, m.getPuntosEnCombate());
        m.cambiarPosicion();
        assertFalse(m.estaEnAtaque());
        assertEquals(2100, m.getPuntosEnCombate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void ataqueNegativo_noSePermite() {
        new MonstruoNormal("Mago", -1, 100, Posicion.ATAQUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defensaMayorAlMaximo_noSePermite() {
        new MonstruoNormal("Mago", 100, Monstruo.PUNTOS_MAXIMOS + 1, Posicion.ATAQUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nombreVacio_noSePermite() {
        new MonstruoEfecto(" ", 100, 100, Posicion.ATAQUE, "x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void posicionNula_noSePermite() {
        new MonstruoNormal("Mago", 100, 100, null);
    }

    @Test
    public void sobreescritura_cadaSubclaseDefineSuTipoYDescripcion() {
        Carta normal = new MonstruoNormal("Guerrero", 1400, 1200, Posicion.ATAQUE);
        Carta efecto = new MonstruoEfecto("Sangan", 1000, 600, Posicion.ATAQUE, "Busca un monstruo.");
        assertEquals("Normal", normal.getTipo());
        assertEquals("Efecto", efecto.getTipo());
        assertTrue(efecto.describir().contains("Busca un monstruo."));
        assertTrue(normal.describir().startsWith("[Normal] Guerrero"));
    }

    @Test
    public void contadorStatic_aumentaConCadaCarta() {
        int antes = Carta.getTotalCartasCreadas();
        new MonstruoNormal("A", 1, 1, Posicion.ATAQUE);
        new MonstruoNormal("B", 1, 1, Posicion.ATAQUE);
        assertEquals(antes + 2, Carta.getTotalCartasCreadas());
    }

    @Test
    public void posicionDesdeEtiqueta() {
        assertEquals(Posicion.DEFENSA, Posicion.desdeEtiqueta("Defensa"));
        assertEquals(Posicion.ATAQUE, Posicion.DEFENSA.invertir());
    }
}