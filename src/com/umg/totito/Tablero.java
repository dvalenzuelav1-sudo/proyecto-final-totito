package com.umg.totito;

/**
 * Representa el tablero del juego de Totito (3 filas x 3 columnas).
 *
 * <p>Esta clase contiene <b>unicamente la logica del juego</b> y no tiene
 * ninguna dependencia de Swing ni de AWT. De esta forma se cumple con la
 * separacion entre la logica del juego y la interfaz grafica exigida en la
 * rubrica de evaluacion.</p>
 *
 * @author Proyecto Final - Programacion II
 */
public class Tablero {

    /** Constante que representa una casilla vacia. */
    public static final char VACIO = ' ';

    /** Dimension del tablero (3 x 3). */
    public static final int TAM = 3;

    /** Matriz que almacena el contenido de cada casilla. */
    private final char[][] celdas;

    /**
     * Crea un tablero nuevo completamente vacio.
     */
    public Tablero() {
        this.celdas = new char[TAM][TAM];
        reiniciar();
    }

    /**
     * Deja todas las casillas del tablero en blanco.
     */
    public void reiniciar() {
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                celdas[fila][col] = VACIO;
            }
        }
    }

    /**
     * Indica si una coordenada pertenece a los limites del tablero.
     *
     * @param fila fila a validar (0..2)
     * @param col  columna a validar (0..2)
     * @return {@code true} si la coordenada es valida
     */
    public boolean esValida(int fila, int col) {
        return fila >= 0 && fila < TAM && col >= 0 && col < TAM;
    }

    /**
     * Verifica si una casilla existe y esta vacia (validacion de movimientos).
     *
     * @param fila fila de la casilla
     * @param col  columna de la casilla
     * @return {@code true} si la casilla se puede ocupar
     */
    public boolean estaLibre(int fila, int col) {
        return esValida(fila, col) && celdas[fila][col] == VACIO;
    }

    /**
     * Coloca un simbolo en una casilla si esta disponible.
     *
     * @param fila    fila destino
     * @param col     columna destino
     * @param simbolo simbolo a colocar ('X' u 'O')
     * @return {@code true} si el movimiento se realizo
     */
    public boolean colocar(int fila, int col, char simbolo) {
        if (!estaLibre(fila, col)) {
            return false;
        }
        celdas[fila][col] = simbolo;
        return true;
    }

    /**
     * Obtiene el contenido de una casilla.
     *
     * @param fila fila consultada
     * @param col  columna consultada
     * @return el simbolo almacenado o {@link #VACIO}
     */
    public char getCelda(int fila, int col) {
        return celdas[fila][col];
    }

    /**
     * Deja una casilla vacia. Se usa internamente para deshacer jugadas
     * (backtracking) durante la busqueda del algoritmo Minimax.
     *
     * @param fila fila de la casilla
     * @param col  columna de la casilla
     */
    void vaciar(int fila, int col) {
        if (esValida(fila, col)) {
            celdas[fila][col] = VACIO;
        }
    }

    /**
     * Indica si ya no quedan casillas libres.
     *
     * @return {@code true} si el tablero esta completo
     */
    public boolean estaLleno() {
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                if (celdas[fila][col] == VACIO) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Devuelve el simbolo ganador si existe una linea completa.
     *
     * @return 'X', 'O' o {@link #VACIO} si aun no hay ganador
     */
    public char ganador() {
        int[][] linea = lineaGanadora();
        return (linea == null) ? VACIO : celdas[linea[0][0]][linea[0][1]];
    }

    /**
     * Comprueba si un simbolo especifico gano la partida.
     *
     * @param simbolo simbolo a evaluar
     * @return {@code true} si ese simbolo tiene tres en linea
     */
    public boolean hayGanador(char simbolo) {
        return ganador() == simbolo;
    }

    /**
     * Busca una linea ganadora (horizontal, vertical o diagonal).
     *
     * @return arreglo con las tres coordenadas ganadoras, o {@code null}
     *         si no existe ninguna linea
     */
    public int[][] lineaGanadora() {
        // Filas
        for (int fila = 0; fila < TAM; fila++) {
            if (celdas[fila][0] != VACIO
                    && celdas[fila][0] == celdas[fila][1]
                    && celdas[fila][1] == celdas[fila][2]) {
                return new int[][]{{fila, 0}, {fila, 1}, {fila, 2}};
            }
        }
        // Columnas
        for (int col = 0; col < TAM; col++) {
            if (celdas[0][col] != VACIO
                    && celdas[0][col] == celdas[1][col]
                    && celdas[1][col] == celdas[2][col]) {
                return new int[][]{{0, col}, {1, col}, {2, col}};
            }
        }
        // Diagonal principal
        if (celdas[0][0] != VACIO
                && celdas[0][0] == celdas[1][1]
                && celdas[1][1] == celdas[2][2]) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }
        // Diagonal secundaria
        if (celdas[0][2] != VACIO
                && celdas[0][2] == celdas[1][1]
                && celdas[1][1] == celdas[2][0]) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }
        return null;
    }
}
