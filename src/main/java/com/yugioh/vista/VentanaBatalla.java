package com.yugioh.vista;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 * VISTA (MVC). Diseñada con el JFrame Form de NetBeans (VentanaBatalla.form).
 * No contiene lógica de negocio: solo muestra datos y expone métodos para
 * que el Controlador registre los eventos y lea lo que escribió el usuario.
 */
public class VentanaBatalla extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    /** Columnas de las tablas del campo de cada jugador. */
    public static final String[] COLUMNAS_CAMPO = {"#", "Nombre", "Tipo", "ATQ", "DEF", "Posición", "Estado"};

    private static final Color COLOR_TURNO = new Color(212, 175, 55);
    private static final Color COLOR_PANEL = new Color(36, 42, 56);

    public VentanaBatalla() {
        initComponents();
        configurarTablas();
        configurarValidaciones();
    }

    // ------------------------------------------------------------------
    // Ajustes visuales que el diseñador no ofrece (modelo de tabla, filtros)
    // ------------------------------------------------------------------
    private void configurarTablas() {
        for (JTable tabla : new JTable[]{tblCampoJ1, tblCampoJ2}) {
            tabla.setModel(new DefaultTableModel(COLUMNAS_CAMPO, 0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            });
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tabla.getTableHeader().setReorderingAllowed(false);
            tabla.getColumnModel().getColumn(0).setMaxWidth(40);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(230);
            DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
            centrado.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
            for (int c : new int[]{0, 3, 4, 5}) {
                tabla.getColumnModel().getColumn(c).setCellRenderer(centrado);
            }
        }
        scrollCampoJ1.getViewport().setBackground(new Color(24, 28, 38));
        scrollCampoJ2.getViewport().setBackground(new Color(24, 28, 38));
        txtHistorial.setCaretColor(Color.WHITE);
    }

    /** Bonificación: los campos ATQ y DEF solo aceptan dígitos (máx. 4). */
    private void configurarValidaciones() {
        ((AbstractDocument) txtAtaque.getDocument()).setDocumentFilter(new FiltroNumerico(4));
        ((AbstractDocument) txtDefensa.getDocument()).setDocumentFilter(new FiltroNumerico(4));
    }

    // ------------------------------------------------------------------
    // Registro de eventos (los usa el Controlador)
    // ------------------------------------------------------------------
    public void alIniciarDuelo(ActionListener l) { btnIniciarDuelo.addActionListener(l); }

    public void alInvocar(ActionListener l) { btnInvocar.addActionListener(l); }

    public void alCargarEjemplo(ActionListener l) { btnCargarEjemplo.addActionListener(l); }

    public void alAtacar(ActionListener l) { btnAtacar.addActionListener(l); }

    public void alAtacarDirecto(ActionListener l) { btnAtaqueDirecto.addActionListener(l); }

    public void alCambiarPosicion(ActionListener l) { btnCambiarPosicion.addActionListener(l); }

    public void alTerminarTurno(ActionListener l) { btnTerminarTurno.addActionListener(l); }

    public void alCambiarTipo(ActionListener l) { cmbTipo.addActionListener(l); }

    // ------------------------------------------------------------------
    // Lectura de datos ingresados
    // ------------------------------------------------------------------
    public String getNombreJugador(int indice) {
        return (indice == 0 ? txtJugador1 : txtJugador2).getText().trim();
    }

    public int getDuelistaSeleccionado() { return cmbDuelista.getSelectedIndex(); }

    public String getNombreMonstruo() { return txtNombre.getText().trim(); }

    public String getTextoAtaque() { return txtAtaque.getText().trim(); }

    public String getTextoDefensa() { return txtDefensa.getText().trim(); }

    public String getPosicion() { return String.valueOf(cmbPosicion.getSelectedItem()); }

    public String getTipo() { return String.valueOf(cmbTipo.getSelectedItem()); }

    public String getDetalle() { return txtDetalle.getText().trim(); }

    /** @return fila seleccionada en el campo del jugador (0 o 1), o -1. */
    public int getFilaSeleccionada(int jugador) {
        return (jugador == 0 ? tblCampoJ1 : tblCampoJ2).getSelectedRow();
    }

    // ------------------------------------------------------------------
    // Presentación
    // ------------------------------------------------------------------
    public void setNombresDuelistas(String jugador1, String jugador2) {
        lblNombreJ1.setText(jugador1);
        lblNombreJ2.setText(jugador2);
        cmbDuelista.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{jugador1, jugador2}));
    }

    public void mostrarCampo(int jugador, List<Object[]> filas) {
        JTable tabla = jugador == 0 ? tblCampoJ1 : tblCampoJ2;
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    public void actualizarPuntosDeVida(int jugador, int lp, int lpMaximos) {
        javax.swing.JProgressBar barra = jugador == 0 ? barraLpJ1 : barraLpJ2;
        barra.setMaximum(lpMaximos);
        barra.setValue(lp);
        barra.setString(lp + " / " + lpMaximos);
        double porcentaje = (double) lp / lpMaximos;
        barra.setForeground(porcentaje > 0.5 ? new Color(46, 204, 113)
                : porcentaje > 0.25 ? new Color(241, 196, 15) : new Color(231, 76, 60));
        (jugador == 0 ? lblLpJ1 : lblLpJ2).setText("LP " + lp);
    }

    public void mostrarTurno(int numeroTurno, String nombreJugador, int indiceJugador) {
        lblTurno.setText("Turno " + numeroTurno + "  -  le toca a " + nombreJugador);
        resaltar(panelJugador1, indiceJugador == 0);
        resaltar(panelJugador2, indiceJugador == 1);
    }

    private void resaltar(JPanel panel, boolean activo) {
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(activo ? COLOR_TURNO : COLOR_PANEL, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    public void mostrarResultado(String texto) {
        lblResultado.setText("<html>" + texto.replace("\n", "<br>") + "</html>");
    }

    public void agregarAlHistorial(String linea) {
        txtHistorial.append(linea + "\n");
        txtHistorial.setCaretPosition(txtHistorial.getDocument().getLength());
    }

    public void setEtiquetaDetalle(String texto) { lblDetalle.setText(texto); }

    public void limpiarFormularioMonstruo() {
        txtNombre.setText("");
        txtAtaque.setText("");
        txtDefensa.setText("");
        txtDetalle.setText("");
        txtNombre.requestFocusInWindow();
    }

    public void setAccionesHabilitadas(boolean habilitadas) {
        btnAtacar.setEnabled(habilitadas);
        btnAtaqueDirecto.setEnabled(habilitadas);
        btnCambiarPosicion.setEnabled(habilitadas);
        btnTerminarTurno.setEnabled(habilitadas);
        btnInvocar.setEnabled(habilitadas);
        btnCargarEjemplo.setEnabled(habilitadas);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Acción no válida", JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarMensaje(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelPrincipal = new javax.swing.JPanel();
        panelEncabezado = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblTurno = new javax.swing.JLabel();
        panelRegistro = new javax.swing.JPanel();
        lblSeccionDuelo = new javax.swing.JLabel();
        lblJugador1 = new javax.swing.JLabel();
        txtJugador1 = new javax.swing.JTextField();
        lblJugador2 = new javax.swing.JLabel();
        txtJugador2 = new javax.swing.JTextField();
        btnIniciarDuelo = new javax.swing.JButton();
        separadorRegistro = new javax.swing.JSeparator();
        lblSeccionInvocar = new javax.swing.JLabel();
        lblDuelista = new javax.swing.JLabel();
        cmbDuelista = new javax.swing.JComboBox<>();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblAtaque = new javax.swing.JLabel();
        txtAtaque = new javax.swing.JTextField();
        lblDefensa = new javax.swing.JLabel();
        txtDefensa = new javax.swing.JTextField();
        lblPosicion = new javax.swing.JLabel();
        cmbPosicion = new javax.swing.JComboBox<>();
        lblTipo = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        lblDetalle = new javax.swing.JLabel();
        txtDetalle = new javax.swing.JTextField();
        btnInvocar = new javax.swing.JButton();
        btnCargarEjemplo = new javax.swing.JButton();
        lblRellenoRegistro = new javax.swing.JLabel();
        panelCampos = new javax.swing.JPanel();
        panelJugador1 = new javax.swing.JPanel();
        panelInfoJ1 = new javax.swing.JPanel();
        lblNombreJ1 = new javax.swing.JLabel();
        barraLpJ1 = new javax.swing.JProgressBar();
        lblLpJ1 = new javax.swing.JLabel();
        scrollCampoJ1 = new javax.swing.JScrollPane();
        tblCampoJ1 = new javax.swing.JTable();
        panelJugador2 = new javax.swing.JPanel();
        panelInfoJ2 = new javax.swing.JPanel();
        lblNombreJ2 = new javax.swing.JLabel();
        barraLpJ2 = new javax.swing.JProgressBar();
        lblLpJ2 = new javax.swing.JLabel();
        scrollCampoJ2 = new javax.swing.JScrollPane();
        tblCampoJ2 = new javax.swing.JTable();
        panelAcciones = new javax.swing.JPanel();
        lblSeccionAcciones = new javax.swing.JLabel();
        btnAtacar = new javax.swing.JButton();
        btnAtaqueDirecto = new javax.swing.JButton();
        btnCambiarPosicion = new javax.swing.JButton();
        btnTerminarTurno = new javax.swing.JButton();
        lblAyuda = new javax.swing.JLabel();
        lblSeccionResultado = new javax.swing.JLabel();
        lblResultado = new javax.swing.JLabel();
        lblRellenoAcciones = new javax.swing.JLabel();
        panelHistorial = new javax.swing.JPanel();
        lblHistorial = new javax.swing.JLabel();
        scrollHistorial = new javax.swing.JScrollPane();
        txtHistorial = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yu-Gi-Oh! - Simulador de Fase de Batalla");
        setMinimumSize(new java.awt.Dimension(1200, 760));

        panelPrincipal.setBackground(new java.awt.Color(24, 28, 38));
        panelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panelPrincipal.setLayout(new java.awt.BorderLayout(12, 12));

        panelEncabezado.setBackground(new java.awt.Color(36, 42, 56));
        panelEncabezado.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panelEncabezado.setLayout(new java.awt.BorderLayout(10, 0));

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(212, 175, 55));
        lblTitulo.setText("YU-GI-OH!  Fase de Batalla");
        panelEncabezado.add(lblTitulo, java.awt.BorderLayout.WEST);

        lblTurno.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTurno.setForeground(new java.awt.Color(232, 234, 240));
        lblTurno.setText("Turno 1 - Jugador 1");
        panelEncabezado.add(lblTurno, java.awt.BorderLayout.EAST);

        panelPrincipal.add(panelEncabezado, java.awt.BorderLayout.NORTH);

        panelRegistro.setBackground(new java.awt.Color(36, 42, 56));
        panelRegistro.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panelRegistro.setLayout(new java.awt.GridBagLayout());

        lblSeccionDuelo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSeccionDuelo.setForeground(new java.awt.Color(212, 175, 55));
        lblSeccionDuelo.setText("DUELISTAS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        panelRegistro.add(lblSeccionDuelo, gridBagConstraints);

        lblJugador1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblJugador1.setForeground(new java.awt.Color(232, 234, 240));
        lblJugador1.setText("Jugador 1:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblJugador1, gridBagConstraints);

        txtJugador1.setColumns(12);
        txtJugador1.setText("Yugi");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtJugador1, gridBagConstraints);

        lblJugador2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblJugador2.setForeground(new java.awt.Color(232, 234, 240));
        lblJugador2.setText("Jugador 2:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblJugador2, gridBagConstraints);

        txtJugador2.setColumns(12);
        txtJugador2.setText("Kaiba");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtJugador2, gridBagConstraints);

        btnIniciarDuelo.setBackground(new java.awt.Color(212, 175, 55));
        btnIniciarDuelo.setFocusPainted(false);
        btnIniciarDuelo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnIniciarDuelo.setForeground(new java.awt.Color(24, 28, 38));
        btnIniciarDuelo.setText("Iniciar nuevo duelo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panelRegistro.add(btnIniciarDuelo, gridBagConstraints);

        separadorRegistro.setForeground(new java.awt.Color(150, 158, 176));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 4, 4);
        panelRegistro.add(separadorRegistro, gridBagConstraints);

        lblSeccionInvocar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSeccionInvocar.setForeground(new java.awt.Color(212, 175, 55));
        lblSeccionInvocar.setText("INVOCAR MONSTRUO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panelRegistro.add(lblSeccionInvocar, gridBagConstraints);

        lblDuelista.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDuelista.setForeground(new java.awt.Color(232, 234, 240));
        lblDuelista.setText("Duelista:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblDuelista, gridBagConstraints);

        cmbDuelista.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Jugador 1", "Jugador 2" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(cmbDuelista, gridBagConstraints);

        lblNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(232, 234, 240));
        lblNombre.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblNombre, gridBagConstraints);

        txtNombre.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtNombre, gridBagConstraints);

        lblAtaque.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblAtaque.setForeground(new java.awt.Color(232, 234, 240));
        lblAtaque.setText("ATQ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblAtaque, gridBagConstraints);

        txtAtaque.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtAtaque, gridBagConstraints);

        lblDefensa.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDefensa.setForeground(new java.awt.Color(232, 234, 240));
        lblDefensa.setText("DEF:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblDefensa, gridBagConstraints);

        txtDefensa.setColumns(6);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtDefensa, gridBagConstraints);

        lblPosicion.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblPosicion.setForeground(new java.awt.Color(232, 234, 240));
        lblPosicion.setText("Posición:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblPosicion, gridBagConstraints);

        cmbPosicion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ataque", "Defensa" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(cmbPosicion, gridBagConstraints);

        lblTipo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTipo.setForeground(new java.awt.Color(232, 234, 240));
        lblTipo.setText("Tipo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblTipo, gridBagConstraints);

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Efecto" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(cmbTipo, gridBagConstraints);

        lblDetalle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDetalle.setForeground(new java.awt.Color(232, 234, 240));
        lblDetalle.setText("Descripción:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblDetalle, gridBagConstraints);

        txtDetalle.setColumns(12);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(txtDetalle, gridBagConstraints);

        btnInvocar.setBackground(new java.awt.Color(39, 174, 96));
        btnInvocar.setFocusPainted(false);
        btnInvocar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInvocar.setForeground(new java.awt.Color(255, 255, 255));
        btnInvocar.setText("Invocar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 4, 4);
        panelRegistro.add(btnInvocar, gridBagConstraints);

        btnCargarEjemplo.setBackground(new java.awt.Color(90, 99, 120));
        btnCargarEjemplo.setFocusPainted(false);
        btnCargarEjemplo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCargarEjemplo.setForeground(new java.awt.Color(255, 255, 255));
        btnCargarEjemplo.setText("Cargar monstruos de ejemplo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(btnCargarEjemplo, gridBagConstraints);

        lblRellenoRegistro.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRellenoRegistro.setForeground(new java.awt.Color(232, 234, 240));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelRegistro.add(lblRellenoRegistro, gridBagConstraints);

        panelPrincipal.add(panelRegistro, java.awt.BorderLayout.WEST);

        panelCampos.setBackground(new java.awt.Color(24, 28, 38));
        panelCampos.setLayout(new java.awt.GridLayout(2, 1, 0, 12));

        panelJugador1.setBackground(new java.awt.Color(36, 42, 56));
        panelJugador1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panelJugador1.setLayout(new java.awt.BorderLayout(0, 8));

        panelInfoJ1.setBackground(new java.awt.Color(36, 42, 56));
        panelInfoJ1.setLayout(new java.awt.BorderLayout(12, 0));

        lblNombreJ1.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblNombreJ1.setForeground(new java.awt.Color(74, 144, 226));
        lblNombreJ1.setText("Jugador 1");
        panelInfoJ1.add(lblNombreJ1, java.awt.BorderLayout.WEST);

        barraLpJ1.setForeground(new java.awt.Color(46, 204, 113));
        barraLpJ1.setMaximum(8000);
        barraLpJ1.setStringPainted(true);
        barraLpJ1.setValue(8000);
        panelInfoJ1.add(barraLpJ1, java.awt.BorderLayout.CENTER);

        lblLpJ1.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblLpJ1.setForeground(new java.awt.Color(232, 234, 240));
        lblLpJ1.setText("LP 8000");
        panelInfoJ1.add(lblLpJ1, java.awt.BorderLayout.EAST);

        panelJugador1.add(panelInfoJ1, java.awt.BorderLayout.NORTH);

        tblCampoJ1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCampoJ1.setRowHeight(26);
        scrollCampoJ1.setViewportView(tblCampoJ1);

        panelJugador1.add(scrollCampoJ1, java.awt.BorderLayout.CENTER);

        panelCampos.add(panelJugador1);

        panelJugador2.setBackground(new java.awt.Color(36, 42, 56));
        panelJugador2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panelJugador2.setLayout(new java.awt.BorderLayout(0, 8));

        panelInfoJ2.setBackground(new java.awt.Color(36, 42, 56));
        panelInfoJ2.setLayout(new java.awt.BorderLayout(12, 0));

        lblNombreJ2.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblNombreJ2.setForeground(new java.awt.Color(226, 88, 74));
        lblNombreJ2.setText("Jugador 2");
        panelInfoJ2.add(lblNombreJ2, java.awt.BorderLayout.WEST);

        barraLpJ2.setForeground(new java.awt.Color(46, 204, 113));
        barraLpJ2.setMaximum(8000);
        barraLpJ2.setStringPainted(true);
        barraLpJ2.setValue(8000);
        panelInfoJ2.add(barraLpJ2, java.awt.BorderLayout.CENTER);

        lblLpJ2.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblLpJ2.setForeground(new java.awt.Color(232, 234, 240));
        lblLpJ2.setText("LP 8000");
        panelInfoJ2.add(lblLpJ2, java.awt.BorderLayout.EAST);

        panelJugador2.add(panelInfoJ2, java.awt.BorderLayout.NORTH);

        tblCampoJ2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCampoJ2.setRowHeight(26);
        scrollCampoJ2.setViewportView(tblCampoJ2);

        panelJugador2.add(scrollCampoJ2, java.awt.BorderLayout.CENTER);

        panelCampos.add(panelJugador2);

        panelPrincipal.add(panelCampos, java.awt.BorderLayout.CENTER);

        panelAcciones.setBackground(new java.awt.Color(36, 42, 56));
        panelAcciones.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panelAcciones.setLayout(new java.awt.GridBagLayout());

        lblSeccionAcciones.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSeccionAcciones.setForeground(new java.awt.Color(212, 175, 55));
        lblSeccionAcciones.setText("ACCIONES");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        panelAcciones.add(lblSeccionAcciones, gridBagConstraints);

        btnAtacar.setBackground(new java.awt.Color(192, 57, 43));
        btnAtacar.setFocusPainted(false);
        btnAtacar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAtacar.setForeground(new java.awt.Color(255, 255, 255));
        btnAtacar.setText("Atacar monstruo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panelAcciones.add(btnAtacar, gridBagConstraints);

        btnAtaqueDirecto.setBackground(new java.awt.Color(211, 84, 0));
        btnAtaqueDirecto.setFocusPainted(false);
        btnAtaqueDirecto.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAtaqueDirecto.setForeground(new java.awt.Color(255, 255, 255));
        btnAtaqueDirecto.setText("Ataque directo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelAcciones.add(btnAtaqueDirecto, gridBagConstraints);

        btnCambiarPosicion.setBackground(new java.awt.Color(41, 128, 185));
        btnCambiarPosicion.setFocusPainted(false);
        btnCambiarPosicion.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCambiarPosicion.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarPosicion.setText("Cambiar posición");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelAcciones.add(btnCambiarPosicion, gridBagConstraints);

        btnTerminarTurno.setBackground(new java.awt.Color(90, 99, 120));
        btnTerminarTurno.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnTerminarTurno.setForeground(new java.awt.Color(255, 255, 255));
        btnTerminarTurno.setText("Terminar turno");
        btnTerminarTurno.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelAcciones.add(btnTerminarTurno, gridBagConstraints);

        lblAyuda.setForeground(new java.awt.Color(150, 158, 176));
        lblAyuda.setText("<html>1. Elige tu monstruo en tu campo.<br>2. Elige el objetivo en el campo rival.<br>3. Pulsa <b>Atacar monstruo</b>.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 4, 4);
        panelAcciones.add(lblAyuda, gridBagConstraints);

        lblSeccionResultado.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSeccionResultado.setForeground(new java.awt.Color(212, 175, 55));
        lblSeccionResultado.setText("ÚLTIMO RESULTADO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(14, 4, 4, 4);
        panelAcciones.add(lblSeccionResultado, gridBagConstraints);

        lblResultado.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblResultado.setForeground(new java.awt.Color(232, 234, 240));
        lblResultado.setPreferredSize(new java.awt.Dimension(230, 120));
        lblResultado.setText("<html>-</html>");
        lblResultado.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelAcciones.add(lblResultado, gridBagConstraints);

        lblRellenoAcciones.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRellenoAcciones.setForeground(new java.awt.Color(232, 234, 240));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panelAcciones.add(lblRellenoAcciones, gridBagConstraints);

        panelPrincipal.add(panelAcciones, java.awt.BorderLayout.EAST);

        panelHistorial.setBackground(new java.awt.Color(36, 42, 56));
        panelHistorial.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panelHistorial.setLayout(new java.awt.BorderLayout(0, 6));

        lblHistorial.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblHistorial.setForeground(new java.awt.Color(212, 175, 55));
        lblHistorial.setText("HISTORIAL DE COMBATE  (historial_combate.txt)");
        panelHistorial.add(lblHistorial, java.awt.BorderLayout.NORTH);

        scrollHistorial.setPreferredSize(new java.awt.Dimension(100, 150));

        txtHistorial.setEditable(false);
        txtHistorial.setBackground(new java.awt.Color(24, 28, 38));
        txtHistorial.setColumns(20);
        txtHistorial.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtHistorial.setForeground(new java.awt.Color(232, 234, 240));
        txtHistorial.setRows(6);
        scrollHistorial.setViewportView(txtHistorial);

        panelHistorial.add(scrollHistorial, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelHistorial, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panelPrincipal, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barraLpJ1;
    private javax.swing.JProgressBar barraLpJ2;
    private javax.swing.JButton btnAtacar;
    private javax.swing.JButton btnAtaqueDirecto;
    private javax.swing.JButton btnCambiarPosicion;
    private javax.swing.JButton btnCargarEjemplo;
    private javax.swing.JButton btnIniciarDuelo;
    private javax.swing.JButton btnInvocar;
    private javax.swing.JButton btnTerminarTurno;
    private javax.swing.JComboBox<String> cmbDuelista;
    private javax.swing.JComboBox<String> cmbPosicion;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel lblAtaque;
    private javax.swing.JLabel lblAyuda;
    private javax.swing.JLabel lblDefensa;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JLabel lblDuelista;
    private javax.swing.JLabel lblHistorial;
    private javax.swing.JLabel lblJugador1;
    private javax.swing.JLabel lblJugador2;
    private javax.swing.JLabel lblLpJ1;
    private javax.swing.JLabel lblLpJ2;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreJ1;
    private javax.swing.JLabel lblNombreJ2;
    private javax.swing.JLabel lblPosicion;
    private javax.swing.JLabel lblRellenoAcciones;
    private javax.swing.JLabel lblRellenoRegistro;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JLabel lblSeccionAcciones;
    private javax.swing.JLabel lblSeccionDuelo;
    private javax.swing.JLabel lblSeccionInvocar;
    private javax.swing.JLabel lblSeccionResultado;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTurno;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JPanel panelEncabezado;
    private javax.swing.JPanel panelHistorial;
    private javax.swing.JPanel panelInfoJ1;
    private javax.swing.JPanel panelInfoJ2;
    private javax.swing.JPanel panelJugador1;
    private javax.swing.JPanel panelJugador2;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelRegistro;
    private javax.swing.JScrollPane scrollCampoJ1;
    private javax.swing.JScrollPane scrollCampoJ2;
    private javax.swing.JScrollPane scrollHistorial;
    private javax.swing.JSeparator separadorRegistro;
    private javax.swing.JTable tblCampoJ1;
    private javax.swing.JTable tblCampoJ2;
    private javax.swing.JTextField txtAtaque;
    private javax.swing.JTextField txtDefensa;
    private javax.swing.JTextField txtDetalle;
    private javax.swing.JTextArea txtHistorial;
    private javax.swing.JTextField txtJugador1;
    private javax.swing.JTextField txtJugador2;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
