package com.yugioh;

import com.yugioh.controlador.BatallaController;
import com.yugioh.modelo.Batalla;
import com.yugioh.modelo.Carta;
import com.yugioh.modelo.Jugador;
import com.yugioh.modelo.Monstruo;
import com.yugioh.modelo.MonstruoEfecto;
import com.yugioh.modelo.MonstruoNormal;
import com.yugioh.modelo.Posicion;
import com.yugioh.modelo.ResultadoAtaque;
import com.yugioh.persistencia.HistorialCombate;
import com.yugioh.vista.VentanaBatalla;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {
        List<Jugador> jugadores = crearDatosDeEjemplo();
        List<Monstruo> todos = new ArrayList<>();
        for (Jugador j : jugadores) {
            todos.addAll(j.getCampo());
        }

        reporteRankingPorAtaque(todos);
        reportePorJugador(jugadores);
        reportePorTipo(todos);
        reporteSimulacion(jugadores);

        if (GraphicsEnvironment.isHeadless() || (args.length > 0 && args[0].equals("--solo-reportes"))) {
            return;
        }
        EventQueue.invokeLater(() -> {
            VentanaBatalla vista = new VentanaBatalla();
            BatallaController controlador = new BatallaController(vista, new HistorialCombate());
            controlador.mostrarVentana();
        });
    }

    private static List<Jugador> crearDatosDeEjemplo() {
        Jugador yugi = new Jugador("Yugi");
        yugi.invocar(new MonstruoNormal("Mago Oscuro", 2500, 2100, Posicion.ATAQUE));
        yugi.invocar(new MonstruoNormal("Guerrero Celta", 1400, 1200, Posicion.ATAQUE));
        yugi.invocar(new MonstruoEfecto("Kuriboh", 300, 200, Posicion.DEFENSA, "Reduce el daño a 0."));

        Jugador kaiba = new Jugador("Kaiba");
        kaiba.invocar(new MonstruoNormal("Dragón Blanco Ojos Azules", 3000, 2500, Posicion.ATAQUE));
        kaiba.invocar(new MonstruoNormal("Gigante Hitotsu-Me", 1200, 1000, Posicion.DEFENSA));
        kaiba.invocar(new MonstruoEfecto("Sangan", 1000, 600, Posicion.ATAQUE, "Busca un monstruo."));

        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(yugi);
        jugadores.add(kaiba);
        return jugadores;
    }

    
    private static void reporteRankingPorAtaque(List<Monstruo> monstruos) {
        titulo("REPORTE 1 - Ranking de monstruos por ATQ");
        List<Monstruo> ordenados = new ArrayList<>(monstruos);
        ordenados.sort(Comparator.comparingInt(Monstruo::getAtaque).reversed());
        int puesto = 1;
        for (Monstruo m : ordenados) {
            
            System.out.printf("%d. %-28s ATQ %4d  DEF %4d  %-8s %s%n", puesto++, m.getNombre(),
                    m.getAtaque(), m.getDefensa(), m.getPosicion(), m.describir());
        }
        System.out.println("Total de cartas creadas (atributo static): " + Carta.getTotalCartasCreadas());
    }

   
    private static void reportePorJugador(List<Jugador> jugadores) {
        titulo("REPORTE 2 - Resumen por jugador");
        for (Jugador j : jugadores) {
            double promedio = j.getCampo().stream().mapToInt(Monstruo::getAtaque).average().orElse(0);
            String masFuerte = j.getCampo().stream().max(Comparator.comparingInt(Monstruo::getAtaque))
                    .map(Monstruo::getNombre).orElse("-");
            long enAtaque = j.getCampo().stream().filter(Monstruo::estaEnAtaque).count();
            System.out.printf("%-6s | LP %5d | Monstruos %d (en ataque %d) | ATQ promedio %.1f | Más fuerte: %s%n",
                    j.getNombre(), j.getPuntosDeVida(), j.getCampo().size(), enAtaque, promedio, masFuerte);
        }
    }

    
    private static void reportePorTipo(List<Monstruo> monstruos) {
        titulo("REPORTE 3 - Monstruos por tipo");
        Map<String, List<String>> porTipo = monstruos.stream().collect(Collectors.groupingBy(
                Carta::getTipo, TreeMap::new, Collectors.mapping(Carta::getNombre, Collectors.toList())));
        porTipo.forEach((tipo, nombres) -> System.out.printf("%-7s (%d): %s%n", tipo, nombres.size(), nombres));
    }

    
    private static void reporteSimulacion(List<Jugador> jugadores) {
        titulo("REPORTE 4 - Simulación de batalla (sin guardar en archivo)");
        Jugador yugi = jugadores.get(0);
        Jugador kaiba = jugadores.get(1);
        Batalla batalla = new Batalla(yugi, kaiba);

        
        batalla.atacar(yugi.getCampo().get(0), kaiba.getCampo().get(2));
        batalla.terminarTurno();
        
        batalla.atacar(kaiba.getCampo().get(0), yugi.getCampo().get(0));

        int danioTotal = 0;
        for (ResultadoAtaque r : batalla.getRegistro()) {
            System.out.printf("- %s -> %s | daño %d | %s%n", r.getNombreAtacante(),
                    r.getNombreDefensor(), r.getDanio(), r.getResumen());
            danioTotal += r.getDanio();
        }
        System.out.println("Daño total infligido: " + danioTotal);
        for (Jugador j : jugadores) {
            System.out.printf("%s queda con %d LP y %d monstruo(s) en el cementerio%n",
                    j.getNombre(), j.getPuntosDeVida(), j.getCementerio().size());
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==== " + texto + " ====");
    }
}