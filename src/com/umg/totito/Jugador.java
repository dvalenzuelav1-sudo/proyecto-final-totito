package com.umg.totito;

/**
 * Modela a un participante del juego de Totito.
 *
 * <p>Encapsula el nombre y el simbolo con el que juega (por ejemplo, el
 * jugador humano usa 'X'). Es una clase de apoyo que permite mantener la
 * logica del juego sin depender de la interfaz grafica.</p>
 *
 * @author Proyecto Final - Programacion II
 */
public class Jugador {

    /** Nombre visible del jugador (editable). */
    private String nombre;

    /** Simbolo con el que juega ('X' u 'O'). */
    private final char simbolo;

    /**
     * Crea un jugador.
     *
     * @param nombre  nombre del jugador
     * @param simbolo simbolo asignado ('X' u 'O')
     */
    public Jugador(String nombre, char simbolo) {
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    /**
     * @return el nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre visible del jugador.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return el simbolo del jugador
     */
    public char getSimbolo() {
        return simbolo;
    }

    @Override
    public String toString() {
        return nombre + " (" + simbolo + ")";
    }
}
