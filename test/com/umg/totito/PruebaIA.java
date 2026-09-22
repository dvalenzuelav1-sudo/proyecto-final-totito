package com.umg.totito;

/**
 * Prueba de verificacion: recorre <b>todas</b> las jugadas posibles del
 * jugador humano (todas las ramas del arbol) y comprueba que la IA nunca
 * pierde. Es una utilidad de apoyo, no forma parte del juego.
 *
 * @author Proyecto Final - Programacion II
 */
public final class PruebaIA {

    private static long victoriasIA;
    private static long empates;
    private static long victoriasHumano;

    private PruebaIA() {
    }

    /**
     * Ejecuta la verificacion exhaustiva.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        IA_Minimax ia = new IA_Minimax('O', 'X');
        explorar(new Tablero(), 'X', ia);

        long total = victoriasIA + empates + victoriasHumano;
        System.out.println("Partidas evaluadas : " + total);
        System.out.println("Victorias de la IA : " + victoriasIA);
        System.out.println("Empates            : " + empates);
        System.out.println("Victorias humano   : " + victoriasHumano);

        if (victoriasHumano == 0) {
            System.out.println("RESULTADO: OK - la IA es imbatible.");
        } else {
            System.out.println("RESULTADO: FALLO - el humano pudo ganar.");
            System.exit(1);
        }
    }

    /**
     * Explora recursivamente todas las respuestas del humano. La IA siempre
     * responde con su mejor movimiento de Minimax.
     *
     * @param tablero estado actual
     * @param turno   'X' (humano) u 'O' (IA)
     * @param ia      instancia de la IA
     */
    private static void explorar(Tablero tablero, char turno, IA_Minimax ia) {
        if (tablero.ganador() != Tablero.VACIO || tablero.estaLleno()) {
            registrarResultado(tablero);
            return;
        }
        if (turno == 'X') {
            for (int fila = 0; fila < Tablero.TAM; fila++) {
                for (int col = 0; col < Tablero.TAM; col++) {
                    if (tablero.estaLibre(fila, col)) {
                        tablero.colocar(fila, col, 'X');
                        explorar(tablero, 'O', ia);
                        tablero.vaciar(fila, col);
                    }
                }
            }
        } else {
            int[] mov = ia.mejorMovimiento(tablero);
            tablero.colocar(mov[0], mov[1], 'O');
            explorar(tablero, 'X', ia);
            tablero.vaciar(mov[0], mov[1]);
        }
    }

    private static void registrarResultado(Tablero tablero) {
        char ganador = tablero.ganador();
        if (ganador == 'O') {
            victoriasIA++;
        } else if (ganador == 'X') {
            victoriasHumano++;
        } else {
            empates++;
        }
    }
}
