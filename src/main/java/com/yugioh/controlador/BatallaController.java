package com.yugioh.controlador;



import com.yugioh.modelo.AccionInvalidaException;
import com.yugioh.modelo.Batalla;
import com.yugioh.modelo.Jugador;
import com.yugioh.modelo.Monstruo;
import com.yugioh.modelo.MonstruoEfecto;
import com.yugioh.modelo.MonstruoNormal;
import com.yugioh.modelo.Posicion;
import com.yugioh.modelo.ResultadoAtaque;
import com.yugioh.persistencia.HistorialCombate;
import com.yugioh.vista.VentanaBatalla;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BatallaController {

    private final VentanaBatalla vista;
    private final HistorialCombate historial;
    private Batalla batalla;

    public BatallaController(VentanaBatalla vista, HistorialCombate historial) {
        this.vista = Objects.requireNonNull(vista);
        this.historial = Objects.requireNonNull(historial);
        enlazarEventos();
        cargarHistorialPrevio();
        iniciarDuelo();
    }

    public void mostrarVentana() {
        vista.setVisible(true);
    }

    private void enlazarEventos() {
        vista.alIniciarDuelo(e -> iniciarDuelo());
        vista.alInvocar(e -> invocarMonstruo());
        vista.alCargarEjemplo(e -> cargarEjemplo());
        vista.alAtacar(e -> ejecutar(this::atacar));
        vista.alAtacarDirecto(e -> ejecutar(this::atacarDirecto));
        vista.alCambiarPosicion(e -> ejecutar(this::cambiarPosicion));
        vista.alTerminarTurno(e -> ejecutar(this::terminarTurno));
        vista.alCambiarTipo(e -> vista.setEtiquetaDetalle(
                "Efecto".equals(vista.getTipo()) ? "Efecto:" : "Descripción:"));
    }

    // ---- Acciones ----
    private void iniciarDuelo() {
        String nombre1 = valorODefecto(vista.getNombreJugador(0), "Jugador 1");
        String nombre2 = valorODefecto(vista.getNombreJugador(1), "Jugador 2");
        if (nombre1.equalsIgnoreCase(nombre2)) {
            vista.mostrarError("Los duelistas deben tener nombres distintos.");
            return;
        }
        batalla = new Batalla(new Jugador(nombre1), new Jugador(nombre2));
        vista.setNombresDuelistas(nombre1, nombre2);
        vista.mostrarResultado("Duelo iniciado: " + nombre1 + " vs " + nombre2
                + ".\nCada uno empieza con " + Jugador.LP_INICIALES + " LP.");
        guardarEvento("=== Nuevo duelo: " + nombre1 + " vs " + nombre2
                + " (LP iniciales " + Jugador.LP_INICIALES + ") ===");
        refrescar();
    }

    private void invocarMonstruo() {
        try {
            Jugador destino = batalla.getJugador(vista.getDuelistaSeleccionado());
            int ataque = leerPuntos(vista.getTextoAtaque(), "ATQ");
            int defensa = leerPuntos(vista.getTextoDefensa(), "DEF");
            Posicion posicion = Posicion.desdeEtiqueta(vista.getPosicion());
            Monstruo monstruo = crearMonstruo(vista.getTipo(), vista.getNombreMonstruo(),
                    ataque, defensa, posicion, vista.getDetalle());
            destino.invocar(monstruo);
            vista.limpiarFormularioMonstruo();
            vista.mostrarResultado(monstruo.describir() + "\nfue invocado en el campo de "
                    + destino.getNombre() + ".");
            refrescar();
        } catch (IllegalArgumentException | AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cargarEjemplo() {
        try {
            Jugador j1 = batalla.getJugador(0);
            Jugador j2 = batalla.getJugador(1);
            j1.invocar(new MonstruoNormal("Mago Oscuro", 2500, 2100, Posicion.ATAQUE,
                    "El mago definitivo en ataque y defensa."));
            j1.invocar(new MonstruoNormal("Guerrero Celta", 1400, 1200, Posicion.ATAQUE));
            j1.invocar(new MonstruoEfecto("Kuriboh", 300, 200, Posicion.DEFENSA,
                    "Descártalo para reducir a 0 el daño de batalla."));
            j2.invocar(new MonstruoNormal("Dragón Blanco Ojos Azules", 3000, 2500, Posicion.ATAQUE,
                    "Dragón legendario de poder destructivo."));
            j2.invocar(new MonstruoNormal("Gigante Hitotsu-Me", 1200, 1000, Posicion.DEFENSA));
            j2.invocar(new MonstruoEfecto("Sangan", 1000, 600, Posicion.ATAQUE,
                    "Al ir al cementerio, añade un monstruo a tu mano."));
            vista.mostrarResultado("Se invocaron 3 monstruos de ejemplo para cada duelista.");
        } catch (AccionInvalidaException ex) {
            vista.mostrarError(ex.getMessage());
        }
        refrescar();
    }

    private void atacar() {
        Monstruo atacante = seleccionado(batalla.getIndiceJugadorEnTurno(),
                "Selecciona en TU campo el monstruo que va a atacar.");
        Monstruo objetivo = seleccionado(1 - batalla.getIndiceJugadorEnTurno(),
                "Selecciona en el campo RIVAL el monstruo objetivo.\n"
                + "(Si el rival no tiene monstruos usa \"Ataque directo\").");
        procesarResultado(batalla.atacar(atacante, objetivo));
    }

    private void atacarDirecto() {
        Monstruo atacante = seleccionado(batalla.getIndiceJugadorEnTurno(),
                "Selecciona en TU campo el monstruo que va a atacar.");
        procesarResultado(batalla.atacarDirectamente(atacante));
    }

    private void cambiarPosicion() {
        Monstruo monstruo = seleccionado(batalla.getIndiceJugadorEnTurno(),
                "Selecciona en TU campo el monstruo que quieres cambiar de posición.");
        batalla.cambiarPosicion(monstruo);
        vista.mostrarResultado(monstruo.getNombre() + " pasa a posición de " + monstruo.getPosicion() + ".");
        refrescar();
    }

    private void terminarTurno() {
        batalla.terminarTurno();
        vista.mostrarResultado("Turno " + batalla.getNumeroTurno() + ": le toca a "
                + batalla.getJugadorEnTurno().getNombre() + ".");
        refrescar();
    }

    // ---- Utilidades ----
    private void procesarResultado(ResultadoAtaque r) {
        Jugador enTurno = batalla.getJugadorEnTurno();
        Jugador rival = batalla.getRival();
        String texto = r.getResumen();
        if (r.getDanio() > 0) {
            String afectado = r.getJugadorAfectado() == ResultadoAtaque.Afectado.ATACANTE
                    ? enTurno.getNombre() : rival.getNombre();
            texto += "\n" + afectado + " pierde " + r.getDanio() + " LP.";
        }
        vista.mostrarResultado(texto);
        try {
            vista.agregarAlHistorial(historial.registrar(batalla.getNumeroTurno(),
                    enTurno.getNombre(), rival.getNombre(), r));
        } catch (IOException ex) {
            vista.mostrarError("No se pudo guardar el historial: " + ex.getMessage());
        }
        refrescar();
        if (batalla.haTerminado()) {
            Jugador ganador = batalla.getGanador();
            guardarEvento("*** " + ganador.getNombre() + " gana el duelo ***");
            vista.mostrarMensaje("Fin del duelo", ganador.getNombre() + " gana el duelo.\n"
                    + "Pulsa \"Iniciar nuevo duelo\" para jugar otra vez.");
        }
    }

    
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (AccionInvalidaException | IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private Monstruo seleccionado(int indiceJugador, String mensajeSiNoHay) {
        int fila = vista.getFilaSeleccionada(indiceJugador);
        List<Monstruo> campo = batalla.getJugador(indiceJugador).getCampo();
        if (fila < 0 || fila >= campo.size()) {
            throw new AccionInvalidaException(mensajeSiNoHay);
        }
        return campo.get(fila);
    }

    private static Monstruo crearMonstruo(String tipo, String nombre, int atq, int def,
            Posicion posicion, String detalle) {
        if ("Efecto".equals(tipo)) {
            return new MonstruoEfecto(nombre, atq, def, posicion, detalle);
        }
        return new MonstruoNormal(nombre, atq, def, posicion, detalle);
    }

    private static int leerPuntos(String texto, String campo) {
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Escribe los puntos de " + campo + ".");
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + " debe ser un número entero.");
        }
    }

    private static String valorODefecto(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor;
    }

    private void refrescar() {
        for (int i = 0; i < 2; i++) {
            Jugador jugador = batalla.getJugador(i);
            boolean esSuTurno = i == batalla.getIndiceJugadorEnTurno();
            List<Object[]> filas = new ArrayList<>();
            int n = 1;
            for (Monstruo m : jugador.getCampo()) {
                String estado;
                if (!esSuTurno) {
                    estado = "-";
                } else if (batalla.yaAtaco(m)) {
                    estado = "Ya atacó";
                } else {
                    estado = m.estaEnAtaque() ? "Puede atacar" : "En defensa";
                }
                filas.add(new Object[]{n++, m.getNombre(), m.getTipo(), m.getAtaque(),
                    m.getDefensa(), m.getPosicion().getEtiqueta(), estado});
            }
            vista.mostrarCampo(i, filas);
            vista.actualizarPuntosDeVida(i, jugador.getPuntosDeVida(), Jugador.LP_INICIALES);
        }
        vista.mostrarTurno(batalla.getNumeroTurno(), batalla.getJugadorEnTurno().getNombre(),
                batalla.getIndiceJugadorEnTurno());
        vista.setAccionesHabilitadas(!batalla.haTerminado());
    }

    private void cargarHistorialPrevio() {
        try {
            List<String> lineas = historial.leer();
            int desde = Math.max(0, lineas.size() - 50);
            for (String linea : lineas.subList(desde, lineas.size())) {
                vista.agregarAlHistorial(linea);
            }
        } catch (IOException ex) {
            vista.mostrarError("No se pudo leer el historial: " + ex.getMessage());
        }
    }

    private void guardarEvento(String evento) {
        try {
            vista.agregarAlHistorial(historial.registrarEvento(evento));
        } catch (IOException ex) {
            vista.mostrarError("No se pudo guardar el historial: " + ex.getMessage());
        }
    }
}