package com.yugioh.persistencia;

import com.yugioh.modelo.ResultadoAtaque;
import com.yugioh.modelo.ResultadoAtaque;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


public class HistorialCombate {

    public static final String ARCHIVO_POR_DEFECTO = "historial_combate.txt";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path ruta;

    public HistorialCombate() {
        this(Paths.get(ARCHIVO_POR_DEFECTO));
    }

    public HistorialCombate(Path ruta) {
        this.ruta = ruta;
    }

   
    public String registrar(int turno, String jugadorAtacante, String jugadorDefensor,
            ResultadoAtaque r) throws IOException {
        String afectado;
        switch (r.getJugadorAfectado()) {
            case ATACANTE:
                afectado = jugadorAtacante;
                break;
            case DEFENSOR:
                afectado = jugadorDefensor;
                break;
            default:
                afectado = "nadie";
                break;
        }
        String linea = String.format("%s | Turno %d | Atacante: %s (%s) | Defensor: %s (%s) | Daño: %d a %s | Resultado: %s",
                LocalDateTime.now().format(FORMATO_FECHA), turno,
                r.getNombreAtacante(), jugadorAtacante,
                r.getNombreDefensor(), jugadorDefensor,
                r.getDanio(), afectado, r.getResumen());
        escribir(linea);
        return linea;
    }

    
    public String registrarEvento(String evento) throws IOException {
        String linea = LocalDateTime.now().format(FORMATO_FECHA) + " | " + evento;
        escribir(linea);
        return linea;
    }

    public List<String> leer() throws IOException {
        if (!Files.exists(ruta)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(ruta, StandardCharsets.UTF_8);
    }

    public Path getRuta() {
        return ruta;
    }

    private void escribir(String linea) throws IOException {
        Files.writeString(ruta, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}