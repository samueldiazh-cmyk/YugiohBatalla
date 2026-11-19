package com.yugioh.persistencia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.yugioh.modelo.CalculadoraDanio;
import com.yugioh.modelo.MonstruoNormal;
import com.yugioh.modelo.Posicion;
import com.yugioh.modelo.ResultadoAtaque;
import java.io.File;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Prueba de la persistencia usando un archivo temporal. */
public class HistorialCombateTest {

    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

    @Test
    public void registrar_agregaUnaLineaPorAtaque() throws Exception {
        File archivo = new File(carpeta.getRoot(), "historial_combate.txt");
        HistorialCombate historial = new HistorialCombate(archivo.toPath());
        ResultadoAtaque r = new CalculadoraDanio().calcular(
                new MonstruoNormal("Mago", 2500, 2100, Posicion.ATAQUE),
                new MonstruoNormal("Guerrero", 1400, 1200, Posicion.ATAQUE));

        historial.registrar(1, "Yugi", "Kaiba", r);
        historial.registrar(1, "Yugi", "Kaiba", r);

        List<String> lineas = historial.leer();
        assertEquals(2, lineas.size());
        assertTrue(lineas.get(0).contains("Atacante: Mago (Yugi)"));
        assertTrue(lineas.get(0).contains("Daño: 1100 a Kaiba"));
    }

    @Test
    public void leer_archivoInexistente_devuelveListaVacia() throws Exception {
        HistorialCombate historial = new HistorialCombate(new File(carpeta.getRoot(), "no.txt").toPath());
        assertTrue(historial.leer().isEmpty());
    }
}