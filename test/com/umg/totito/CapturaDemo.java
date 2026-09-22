package com.umg.totito;

import javax.imageio.ImageIO;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Genera las capturas de pantalla del informe tecnico.
 *
 * <p>No forma parte del juego: es una utilidad de apoyo que construye
 * posiciones reales (jugando con el mismo {@link IA_Minimax}) y las pinta
 * sobre la misma interfaz de {@link JuegoMain} para guardarlas como PNG.</p>
 *
 * @author Proyecto Final - Programacion II
 */
public final class CapturaDemo {

    private static final String CARPETA = "informe/capturas";

    private CapturaDemo() {
        // Clase de utilidades: no se instancia.
    }

    /**
     * Genera las tres capturas requeridas.
     *
     * @param args no se usan
     * @throws Exception si ocurre un error al escribir las imagenes
     */
    public static void main(String[] args) throws Exception {
        File directorio = new File(CARPETA);
        if (!directorio.exists() && !directorio.mkdirs()) {
            throw new IllegalStateException("No se pudo crear " + CARPETA);
        }

        // 1) Partida en curso (4 jugadas, turno del humano).
        char[][] enJuego = jugarOptimo(4);
        capturar(JuegoMain.Modo.VS_COMPUTADORA, enJuego, "Tu turno (X)",
                new File(directorio, "01_en_juego.png"));

        // 2) Victoria de la IA: el humano juega deliberadamente mal.
        char[][] victoriaIA = jugarHumanoMal();
        System.out.println("Resultado victoria IA = '" + ganador(victoriaIA) + "'");
        capturar(JuegoMain.Modo.VS_COMPUTADORA, victoriaIA,
                "¡Gano la computadora (O)!",
                new File(directorio, "02_victoria_ia.png"));

        // 3) Empate: ambos jugadores juegan de forma optima.
        char[][] empate = jugarOptimo(9);
        System.out.println("Resultado empate       = '" + ganador(empate) + "'");
        capturar(JuegoMain.Modo.VS_COMPUTADORA, empate,
                "¡Empate! Tablero completo",
                new File(directorio, "03_empate.png"));

        // 4) Modo de dos jugadores (turno del Jugador 2).
        char[][] dosJugadores = {
            {'X', 'O', 'X'},
            {'O', 'X', ' '},
            {' ', ' ', ' '}
        };
        capturar(JuegoMain.Modo.VS_JUGADOR, dosJugadores,
                "Turno de Jugador 2 (O)",
                new File(directorio, "04_dos_jugadores.png"));

        System.out.println("Capturas generadas en: " + directorio.getAbsolutePath());
    }

    /**
     * Pinta el contenido de una ventana {@link JuegoMain} en una imagen.
     */
    private static void capturar(JuegoMain.Modo modo, char[][] celdas,
            String mensaje, File salida) throws Exception {
        JuegoMain juego = new JuegoMain();
        juego.setModo(modo);
        juego.setEstadoCaptura(celdas, mensaje);
        juego.pack();
        juego.setSize(460, 600);
        juego.validate();

        Container contenido = juego.getContentPane();
        int ancho = Math.max(contenido.getWidth(), 460);
        int alto = Math.max(contenido.getHeight(), 600);
        BufferedImage imagen = new BufferedImage(ancho, alto,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        contenido.printAll(g);
        g.dispose();
        ImageIO.write(imagen, "png", salida);
        juego.dispose();
    }

    /**
     * Juega una partida donde ambos bandos usan Minimax (empate garantizado).
     *
     * @param jugadas cantidad maxima de movimientos a realizar
     * @return estado final del tablero
     */
    private static char[][] jugarOptimo(int jugadas) {
        Tablero tablero = new Tablero();
        IA_Minimax iaO = new IA_Minimax('O', 'X');
        IA_Minimax iaX = new IA_Minimax('X', 'O');
        char turno = 'X';
        int realizadas = 0;

        while (tablero.ganador() == Tablero.VACIO && !tablero.estaLleno()
                && realizadas < jugadas) {
            IA_Minimax ia = (turno == 'X') ? iaX : iaO;
            int[] mov = ia.mejorMovimiento(tablero);
            tablero.colocar(mov[0], mov[1], turno);
            turno = (turno == 'X') ? 'O' : 'X';
            realizadas++;
        }
        return aMatriz(tablero);
    }

    /**
     * El humano elige siempre la peor jugada posible, provocando la victoria
     * de la IA.
     *
     * @return estado final del tablero con victoria de la IA
     */
    private static char[][] jugarHumanoMal() {
        Tablero tablero = new Tablero();
        IA_Minimax iaO = new IA_Minimax('O', 'X');
        char turno = 'X';
        int guarda = 0;

        while (tablero.ganador() == Tablero.VACIO && !tablero.estaLleno()
                && guarda < 9) {
            int[] mov = (turno == 'X')
                    ? peorMovimientoHumano(tablero, iaO)
                    : iaO.mejorMovimiento(tablero);
            tablero.colocar(mov[0], mov[1], turno);
            turno = (turno == 'X') ? 'O' : 'X';
            guarda++;
        }
        return aMatriz(tablero);
    }

    /**
     * Devuelve la jugada que mas favorece a la IA (peor para el humano).
     */
    private static int[] peorMovimientoHumano(Tablero tablero, IA_Minimax ia) {
        int mejorParaIA = Integer.MIN_VALUE;
        int[] elegido = null;
        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                if (!tablero.estaLibre(fila, col)) {
                    continue;
                }
                tablero.colocar(fila, col, 'X');
                int valor = ia.minimax(tablero, true, 0);
                tablero.vaciar(fila, col);
                if (valor > mejorParaIA) {
                    mejorParaIA = valor;
                    elegido = new int[]{fila, col};
                }
            }
        }
        return elegido;
    }

    /**
     * Convierte el tablero a una matriz simple de caracteres.
     */
    private static char[][] aMatriz(Tablero tablero) {
        char[][] copia = new char[Tablero.TAM][Tablero.TAM];
        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                copia[fila][col] = tablero.getCelda(fila, col);
            }
        }
        return copia;
    }

    /**
     * @return el ganador de una matriz de tablero ('X', 'O' o ' ')
     */
    private static char ganador(char[][] celdas) {
        Tablero tablero = new Tablero();
        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                if (celdas[fila][col] != Tablero.VACIO) {
                    tablero.colocar(fila, col, celdas[fila][col]);
                }
            }
        }
        return tablero.ganador();
    }
}
