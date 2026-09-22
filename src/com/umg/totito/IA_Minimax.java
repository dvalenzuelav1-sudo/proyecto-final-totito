package com.umg.totito;

/**
 * Implementa la Inteligencia Artificial del juego mediante el
 * <b>algoritmo Minimax</b> (busqueda en arbol de decisiones).
 *
 * <p>El Totito es un juego de suma cero: lo que gana un jugador lo pierde el
 * otro. Por ello el algoritmo minimax explora de forma recursiva <i>todos</i>
 * los estados posibles del tablero y asigna un puntaje a cada uno:</p>
 * <ul>
 *   <li>Victoria de la IA ({@link #simboloIA}): puntaje positivo.</li>
 *   <li>Victoria del humano ({@link #simboloHumano}): puntaje negativo.</li>
 *   <li>Empate: cero.</li>
 * </ul>
 * <p>Al restar la profundidad al puntaje, las victorias mas rapidas se
 * prefieren sobre las lentas (y las derrotas se retrasan lo mas posible).</p>
 *
 * @author Proyecto Final - Programacion II
 */
public class IA_Minimax {

    /** Puntaje base otorgado a una victoria de la IA. */
    public static final int PUNTAJE_VICTORIA = 10;

    /** Simbolo que utiliza la computadora. */
    private final char simboloIA;

    /** Simbolo que utiliza el jugador humano. */
    private final char simboloHumano;

    /**
     * Crea la IA indicando que simbolo controla cada bando.
     *
     * @param simboloIA     simbolo de la computadora
     * @param simboloHumano simbolo del jugador
     */
    public IA_Minimax(char simboloIA, char simboloHumano) {
        this.simboloIA = simboloIA;
        this.simboloHumano = simboloHumano;
    }

    /**
     * Calcula el mejor movimiento posible para la IA en el estado actual.
     *
     * <p>Para cada casilla libre prueba la jugada, evalua el resultado con
     * {@link #minimax(Tablero, boolean, int)} y se queda con aquella que
     * maximiza el puntaje. Como minimax asume que el rival jugara de forma
     * optima, la IA nunca pierde: gana o empata.</p>
     *
     * @param tablero estado actual del juego
     * @return arreglo {@code {fila, col}} con el mejor movimiento, o
     *         {@code null} si no hay jugadas disponibles
     */
    public int[] mejorMovimiento(Tablero tablero) {
        int mejorValor = Integer.MIN_VALUE;
        int[] mejor = null;

        for (int fila = 0; fila < Tablero.TAM; fila++) {
            for (int col = 0; col < Tablero.TAM; col++) {
                if (!tablero.estaLibre(fila, col)) {
                    continue;
                }
                // Se prueba la jugada de la IA.
                tablero.colocar(fila, col, simboloIA);
                // Se evalua la posicion suponiendo que ahora mueve el humano.
                int valor = minimax(tablero, false, 1);
                // Se deshace la jugada (backtracking) para probar la siguiente.
                tablero.vaciar(fila, col);

                if (valor > mejorValor) {
                    mejorValor = valor;
                    mejor = new int[]{fila, col};
                }
            }
        }
        return mejor;
    }

    /**
     * Funcion recursiva del algoritmo Minimax.
     *
     * <p>Recorre el arbol de juego hasta los estados terminales (victoria o
     * empate) y va devolviendo hacia arriba el puntaje del mejor resultado.
     * El bando de la IA es el <b>maximizador</b> (busca el puntaje mas alto) y
     * el bando del humano es el <b>minimizador</b> (busca el mas bajo).</p>
     *
     * @param tablero     estado del tablero que se esta evaluando
     * @param esTurnoIA   {@code true} si en este nivel le toca a la IA (max)
     * @param profundidad cantidad de niveles recorridos (ajusta el puntaje)
     * @return el puntaje minimax del estado recibido
     */
    int minimax(Tablero tablero, boolean esTurnoIA, int profundidad) {

        // --- Casos base: estados terminales del arbol de juego ---
        char ganador = tablero.ganador();
        if (ganador == simboloIA) {
            // La IA gano: conviene que ocurra lo antes posible.
            return PUNTAJE_VICTORIA - profundidad;
        }
        if (ganador == simboloHumano) {
            // El humano gano: conviene retrasar la derrota.
            return profundidad - PUNTAJE_VICTORIA;
        }
        if (tablero.estaLleno()) {
            // Tablero lleno sin ganador: empate.
            return 0;
        }

        if (esTurnoIA) {
            // Nivel MAX: la IA elige la jugada con el mayor puntaje.
            int mejor = Integer.MIN_VALUE;
            for (int fila = 0; fila < Tablero.TAM; fila++) {
                for (int col = 0; col < Tablero.TAM; col++) {
                    if (!tablero.estaLibre(fila, col)) {
                        continue;
                    }
                    tablero.colocar(fila, col, simboloIA);         // intenta
                    int valor = minimax(tablero, false, profundidad + 1);
                    tablero.vaciar(fila, col);     // deshace
                    mejor = Math.max(mejor, valor);
                }
            }
            return mejor;
        } else {
            // Nivel MIN: el humano elige la jugada con el menor puntaje
            // (desde el punto de vista de la IA es el peor caso).
            int mejor = Integer.MAX_VALUE;
            for (int fila = 0; fila < Tablero.TAM; fila++) {
                for (int col = 0; col < Tablero.TAM; col++) {
                    if (!tablero.estaLibre(fila, col)) {
                        continue;
                    }
                    tablero.colocar(fila, col, simboloHumano);     // intenta
                    int valor = minimax(tablero, true, profundidad + 1);
                    tablero.vaciar(fila, col);     // deshace
                    mejor = Math.min(mejor, valor);
                }
            }
            return mejor;
        }
    }

    /**
     * @return el simbolo que controla la IA
     */
    public char getSimboloIA() {
        return simboloIA;
    }
}
