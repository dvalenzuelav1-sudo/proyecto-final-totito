package com.umg.totito;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Punto de entrada del juego y contenedor de la interfaz grafica (Swing).
 *
 * <p>La clase usa exclusivamente {@code javax.swing} y {@code java.awt}. La
 * logica del juego vive en {@link Tablero} y {@link IA_Minimax}, mientras que
 * esta clase se encarga unicamente de la presentacion y de coordinar los
 * turnos (separacion clara entre logica y GUI).</p>
 *
 * <p>El juego ofrece dos modos:</p>
 * <ul>
 *   <li><b>Jugador vs Jugador:</b> dos personas alternan sus turnos.</li>
 *   <li><b>Jugador vs Computadora:</b> la IA responde con Minimax.</li>
 * </ul>
 *
 * @author Proyecto Final - Programacion II
 */
public class JuegoMain extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Simbolo del primer jugador (siempre inicia la partida). */
    public static final char SIMBOLO_X = 'X';

    /** Simbolo del segundo jugador / computadora. */
    public static final char SIMBOLO_O = 'O';

    private static final Color COLOR_FONDO = new Color(245, 246, 248);
    private static final Color COLOR_X = new Color(33, 118, 216);
    private static final Color COLOR_O = new Color(211, 47, 47);
    private static final Color COLOR_GANADOR = new Color(198, 239, 206);
    private static final Color COLOR_ESTADO = new Color(232, 234, 237);

    /** Modos de juego disponibles. */
    enum Modo {
        /** Dos personas juegan en el mismo equipo. */
        VS_JUGADOR,
        /** El jugador se enfrenta a la IA (Minimax). */
        VS_COMPUTADORA
    }

    private final Tablero tablero = new Tablero();
    private final Jugador jugadorUno = new Jugador("Jugador 1", SIMBOLO_X);
    private final Jugador jugadorDos = new Jugador("Jugador 2", SIMBOLO_O);
    private final IA_Minimax ia = new IA_Minimax(SIMBOLO_O, SIMBOLO_X);

    private final CasillaButton[] botones = new CasillaButton[Tablero.TAM * Tablero.TAM];
    private final JLabel etiquetaEstado = new JLabel("", SwingConstants.CENTER);
    private final JButton botonReiniciar = new JButton("Nueva Partida");

    private final JRadioButton radioDosJugadores = new JRadioButton("Jugador vs Jugador", true);
    private final JRadioButton radioComputadora = new JRadioButton("Jugador vs Computadora");
    private final JTextField campoJugadorUno = new JTextField("Jugador 1", 10);
    private final JTextField campoJugadorDos = new JTextField("Jugador 2", 10);
    private final JLabel etiquetaJugadorDos = new JLabel("Jugador 2 (O):");

    /** Timer que retrasa la jugada de la IA para que se aprecie el turno. */
    private final Timer temporizadorIA = new Timer(350, e -> jugarIA());

    private Modo modo = Modo.VS_JUGADOR;
    private char turnoActual = SIMBOLO_X;
    private boolean juegoTerminado = false;

    /**
     * Construye la ventana principal del juego.
     */
    public JuegoMain() {
        super("Totito  -  Jugador vs Jugador");
        temporizadorIA.setRepeats(false);
        construirInterfaz();
        iniciarNuevaPartida();
    }

    /**
     * Crea y organiza todos los componentes de la interfaz grafica.
     */
    private void construirInterfaz() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(COLOR_FONDO);

        // --- Panel norte: configuracion de modo y nombres + estado ---
        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.Y_AXIS));
        panelConfig.setBackground(COLOR_FONDO);
        panelConfig.setBorder(BorderFactory.createEmptyBorder(10, 12, 4, 12));

        JPanel panelModo = new JPanel();
        panelModo.setLayout(new BoxLayout(panelModo, BoxLayout.X_AXIS));
        panelModo.setBackground(COLOR_FONDO);
        panelModo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel etiquetaModo = new JLabel("Modo: ");
        etiquetaModo.setFont(new Font("SansSerif", Font.BOLD, 13));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioDosJugadores);
        grupo.add(radioComputadora);
        for (JRadioButton radio : new JRadioButton[]{radioDosJugadores, radioComputadora}) {
            radio.setBackground(COLOR_FONDO);
            radio.setFont(new Font("SansSerif", Font.PLAIN, 13));
            radio.addActionListener(e -> cambiarModo());
        }
        panelModo.add(etiquetaModo);
        panelModo.add(radioDosJugadores);
        panelModo.add(Box.createHorizontalStrut(12));
        panelModo.add(radioComputadora);
        panelModo.add(Box.createHorizontalGlue());

        JPanel panelNombres = new JPanel(new GridLayout(2, 2, 8, 6));
        panelNombres.setBackground(COLOR_FONDO);
        panelNombres.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelNombres.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        JLabel etiquetaJugadorUno = new JLabel("Jugador 1 (X):");
        etiquetaJugadorUno.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiquetaJugadorDos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        configurarCampoNombre(campoJugadorUno);
        configurarCampoNombre(campoJugadorDos);
        panelNombres.add(etiquetaJugadorUno);
        panelNombres.add(campoJugadorUno);
        panelNombres.add(etiquetaJugadorDos);
        panelNombres.add(campoJugadorDos);

        panelConfig.add(panelModo);
        panelConfig.add(panelNombres);

        // Barra de estado / turno.
        etiquetaEstado.setFont(new Font("SansSerif", Font.BOLD, 17));
        etiquetaEstado.setOpaque(true);
        etiquetaEstado.setBackground(COLOR_ESTADO);
        etiquetaEstado.setPreferredSize(new Dimension(100, 48));
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelConfig, BorderLayout.NORTH);
        panelNorte.add(etiquetaEstado, BorderLayout.SOUTH);

        // --- Panel central: tablero 3 x 3 ---
        JPanel panelTablero = new JPanel(new GridLayout(Tablero.TAM, Tablero.TAM, 8, 8));
        panelTablero.setBackground(COLOR_FONDO);
        panelTablero.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        for (int i = 0; i < botones.length; i++) {
            botones[i] = crearBoton(i);
            panelTablero.add(botones[i]);
        }

        // --- Panel sur: boton de reinicio ---
        JPanel panelSur = new JPanel();
        panelSur.setBackground(COLOR_FONDO);
        panelSur.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        botonReiniciar.setFont(new Font("SansSerif", Font.BOLD, 15));
        botonReiniciar.setFocusPainted(false);
        botonReiniciar.setPreferredSize(new Dimension(180, 40));
        botonReiniciar.addActionListener(e -> iniciarNuevaPartida());
        panelSur.add(botonReiniciar);

        add(panelNorte, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);

        setSize(460, 600);
        setMinimumSize(new Dimension(400, 540));
        setLocationRelativeTo(null);
    }

    /**
     * Configura un campo de texto para editar el nombre de un jugador.
     *
     * @param campo campo de texto
     */
    private void configurarCampoNombre(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.addActionListener(e -> aplicarNombres());
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                aplicarNombres();
            }
        });
    }

    /**
     * Crea un boton-casilla del tablero.
     *
     * @param indice posicion 0..8 dentro del tablero
     * @return el boton configurado
     */
    private CasillaButton crearBoton(int indice) {
        final int posicion = indice;
        CasillaButton boton = new CasillaButton();
        boton.setFont(new Font("SansSerif", Font.BOLD, 60));
        boton.setFocusPainted(false);
        boton.setBackground(Color.WHITE);
        boton.setOpaque(true);
        boton.addActionListener(e -> clickCasilla(posicion));
        return boton;
    }

    /**
     * Cambia el modo de juego y reinicia la partida.
     */
    private void cambiarModo() {
        setModo(radioComputadora.isSelected() ? Modo.VS_COMPUTADORA : Modo.VS_JUGADOR);
        iniciarNuevaPartida();
    }

    /**
     * Aplica un modo de juego a la interfaz.
     *
     * @param nuevo modo a aplicar
     */
    void setModo(Modo nuevo) {
        this.modo = nuevo;
        boolean dosJugadores = (nuevo == Modo.VS_JUGADOR);
        radioDosJugadores.setSelected(dosJugadores);
        radioComputadora.setSelected(!dosJugadores);
        campoJugadorDos.setEnabled(dosJugadores);
        etiquetaJugadorDos.setText(dosJugadores ? "Jugador 2 (O):" : "Computadora (O):");
        setTitle(dosJugadores ? "Totito  -  Jugador vs Jugador"
                : "Totito  -  Jugador vs Computadora");
    }

    /**
     * Lee los nombres escritos por el usuario y los aplica.
     */
    private void aplicarNombres() {
        String nombreUno = campoJugadorUno.getText().trim();
        jugadorUno.setNombre(nombreUno.isEmpty() ? "Jugador 1" : nombreUno);

        if (modo == Modo.VS_JUGADOR) {
            String nombreDos = campoJugadorDos.getText().trim();
            jugadorDos.setNombre(nombreDos.isEmpty() ? "Jugador 2" : nombreDos);
        } else {
            jugadorDos.setNombre("la computadora");
        }
        refrescarMensajeTurno();
    }

    /**
     * Reinicia el estado para comenzar una partida nueva.
     */
    public void iniciarNuevaPartida() {
        temporizadorIA.stop();
        aplicarNombres();
        tablero.reiniciar();
        turnoActual = SIMBOLO_X;
        juegoTerminado = false;

        for (CasillaButton boton : botones) {
            boton.limpiar();
            boton.setBackground(Color.WHITE);
            boton.setEnabled(true);
        }
        etiquetaEstado.setBackground(COLOR_ESTADO);
        actualizarVista();
        refrescarMensajeTurno();
    }

    /**
     * Procesa el clic de un jugador humano sobre una casilla.
     *
     * @param indice posicion 0..8 seleccionada
     */
    void clickCasilla(int indice) {
        if (!esTurnoHumano()) {
            return; // Se ignora el clic durante el turno de la IA o al terminar.
        }
        int fila = indice / Tablero.TAM;
        int col = indice % Tablero.TAM;
        if (!tablero.estaLibre(fila, col)) {
            return; // Validacion: no se permiten movimientos sobre casillas ocupadas.
        }
        tablero.colocar(fila, col, turnoActual);
        actualizarVista();

        if (verificarFinDePartida()) {
            return;
        }

        if (modo == Modo.VS_COMPUTADORA) {
            turnoActual = SIMBOLO_O;
            actualizarVista(); // Deshabilita las casillas mientras juega la IA.
            etiquetaEstado.setText("Turno de la computadora (" + SIMBOLO_O + ")...");
            temporizadorIA.start();
        } else {
            turnoActual = (turnoActual == SIMBOLO_X) ? SIMBOLO_O : SIMBOLO_X;
            actualizarVista(); // Rehabilita las casillas para el otro jugador.
            refrescarMensajeTurno();
        }
    }

    /**
     * Ejecuta la jugada de la IA usando el mejor movimiento de Minimax.
     */
    void jugarIA() {
        if (juegoTerminado) {
            return;
        }
        int[] movimiento = ia.mejorMovimiento(tablero);
        if (movimiento != null) {
            tablero.colocar(movimiento[0], movimiento[1], SIMBOLO_O);
        }
        // El turno se actualiza ANTES de refrescar la vista para que las
        // casillas libres vuelvan a quedar habilitadas para el jugador.
        turnoActual = SIMBOLO_X;
        actualizarVista();

        if (verificarFinDePartida()) {
            return;
        }
        refrescarMensajeTurno();
    }

    /**
     * Indica si en este momento le toca jugar a una persona.
     *
     * @return {@code true} si se puede hacer clic en el tablero
     */
    private boolean esTurnoHumano() {
        if (juegoTerminado) {
            return false;
        }
        return modo == Modo.VS_JUGADOR || turnoActual == SIMBOLO_X;
    }

    /**
     * Obtiene el nombre del bando que juega con un simbolo.
     *
     * @param simbolo 'X' u 'O'
     * @return el nombre a mostrar
     */
    private String nombreDe(char simbolo) {
        if (simbolo == SIMBOLO_X) {
            return jugadorUno.getNombre();
        }
        return (modo == Modo.VS_COMPUTADORA) ? "la computadora" : jugadorDos.getNombre();
    }

    /**
     * Actualiza la barra de estado con el turno actual.
     */
    private void refrescarMensajeTurno() {
        if (juegoTerminado) {
            return;
        }
        etiquetaEstado.setText("Turno de " + nombreDe(turnoActual)
                + " (" + turnoActual + ")");
    }

    /**
     * Comprueba si la partida termino por victoria o empate.
     *
     * @return {@code true} si el juego finalizo
     */
    private boolean verificarFinDePartida() {
        char ganador = tablero.ganador();
        if (ganador == SIMBOLO_X) {
            finalizar("¡Gano " + jugadorUno.getNombre() + " (" + SIMBOLO_X + ")!");
            return true;
        }
        if (ganador == SIMBOLO_O) {
            finalizar("¡Gano " + nombreDe(SIMBOLO_O) + " (" + SIMBOLO_O + ")!");
            return true;
        }
        if (tablero.estaLleno()) {
            finalizar("¡Empate! Tablero completo");
            return true;
        }
        return false;
    }

    /**
     * Marca el fin de la partida y resalta la linea ganadora si existe.
     *
     * @param mensaje texto a mostrar en la barra de estado
     */
    private void finalizar(String mensaje) {
        juegoTerminado = true;
        etiquetaEstado.setText(mensaje);
        for (CasillaButton boton : botones) {
            boton.setEnabled(false);
        }
        int[][] linea = tablero.lineaGanadora();
        if (linea != null) {
            etiquetaEstado.setBackground(COLOR_GANADOR);
            for (int[] celda : linea) {
                botones[celda[0] * Tablero.TAM + celda[1]].setBackground(COLOR_GANADOR);
            }
        }
    }

    /**
     * Refresca el texto, color y estado de cada boton segun el tablero.
     */
    private void actualizarVista() {
        boolean habilitarLibres = esTurnoHumano();
        for (int i = 0; i < botones.length; i++) {
            int fila = i / Tablero.TAM;
            int col = i % Tablero.TAM;
            char valor = tablero.getCelda(fila, col);
            CasillaButton boton = botones[i];

            if (valor == Tablero.VACIO) {
                boton.limpiar();
                boton.setEnabled(habilitarLibres);
            } else {
                Color color = (valor == SIMBOLO_X) ? COLOR_X : COLOR_O;
                boton.setContenido(String.valueOf(valor), color);
                boton.setEnabled(false); // Bloqueo inmediato de casillas ocupadas.
            }
        }
    }

    /**
     * Carga un estado concreto del tablero solo para generar capturas del
     * informe tecnico. No se usa durante el juego normal.
     *
     * @param celdas  matriz 3 x 3 con los simbolos
     * @param mensaje texto a mostrar en la barra de estado
     */
    void setEstadoCaptura(char[][] celdas, String mensaje) {
        tablero.reiniciar();
        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                if (celdas[fila][col] != Tablero.VACIO) {
                    tablero.colocar(fila, col, celdas[fila][col]);
                }
            }
        }
        juegoTerminado = tablero.ganador() != Tablero.VACIO || tablero.estaLleno();
        // Le toca al bando que tiene menos fichas (X siempre inicia).
        turnoActual = (contarFichas(SIMBOLO_X) > contarFichas(SIMBOLO_O))
                ? SIMBOLO_O : SIMBOLO_X;

        actualizarVista();
        int[][] linea = tablero.lineaGanadora();
        etiquetaEstado.setBackground(linea != null ? COLOR_GANADOR : COLOR_ESTADO);
        etiquetaEstado.setText(mensaje);

        if (linea != null) {
            for (int[] celda : linea) {
                botones[celda[0] * Tablero.TAM + celda[1]].setBackground(COLOR_GANADOR);
            }
        }
    }

    /**
     * Cuenta cuantas fichas de un simbolo hay en el tablero.
     *
     * @param simbolo simbolo a contar
     * @return cantidad de fichas
     */
    private int contarFichas(char simbolo) {
        int total = 0;
        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                if (tablero.getCelda(fila, col) == simbolo) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Metodo principal: inicia la aplicacion en el hilo de eventos de Swing.
     *
     * @param args argumentos de linea de comandos (no se usan)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Si no es posible, se usa el estilo por defecto de Swing.
        }
        SwingUtilities.invokeLater(() -> new JuegoMain().setVisible(true));
    }

    /**
     * Casilla del tablero. Es un {@link JButton} que dibuja su simbolo con un
     * color propio, incluso cuando esta deshabilitado (los botones Swing
     * normales ignoran el color del texto al deshabilitarse).
     */
    private static class CasillaButton extends JButton {

        private static final long serialVersionUID = 1L;

        private String texto = "";
        private Color colorTexto = Color.DARK_GRAY;

        /**
         * Asigna el simbolo y su color.
         *
         * @param texto simbolo a mostrar
         * @param color color del simbolo
         */
        void setContenido(String texto, Color color) {
            this.texto = texto;
            this.colorTexto = color;
            super.setText("");
            repaint();
        }

        /**
         * Deja la casilla sin simbolo.
         */
        void limpiar() {
            this.texto = "";
            super.setText("");
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (texto.isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(colorTexto);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(texto)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(texto, x, y);
            g2.dispose();
        }
    }
}
