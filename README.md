# Proyecto Final: Juego de Totito

Juego de tres en linea (**Totito**) desarrollado en **Java** con interfaz grafica
**Swing** (`javax.swing` / `java.awt`) y una Inteligencia Artificial basada en el
algoritmo de busqueda en arbol **Minimax**.

Proyecto Final del curso **Programacion II** - Universidad Mariano Galvez de Guatemala.

## Modos de juego

- **Jugador vs Jugador:** dos personas juegan en el mismo equipo alternando turnos.
- **Jugador vs Computadora:** el jugador se enfrenta a la IA, que juega de forma
  optima con Minimax y es **imbatible** (siempre gana o empata).

Los nombres de los jugadores son **editables** desde la ventana y se reflejan en los
mensajes de turno y de resultado.

## Requisitos

- Java JDK 11 o superior.

## Como ejecutar

Con doble clic en el archivo `ejecutar.bat` (compila y abre el juego), o desde la
terminal:

```bash
javac -encoding UTF-8 -d out src/com/umg/totito/*.java
java -cp out com.umg.totito.JuegoMain
```

Tambien se puede abrir el proyecto en cualquier IDE (IntelliJ IDEA, Eclipse o
NetBeans) y ejecutar la clase `JuegoMain`.

## Estructura del proyecto

```
src/com/umg/totito/
    Tablero.java        Logica del tablero 3x3 (victoria, empate, reinicio)
    Jugador.java        Modelo de un jugador (nombre y simbolo)
    IA_Minimax.java     Inteligencia artificial (Minimax recursivo)
    JuegoMain.java      Interfaz grafica Swing y punto de entrada
test/com/umg/totito/
    PruebaIA.java       Verificacion exhaustiva de que la IA nunca pierde
    CapturaDemo.java    Genera las capturas del informe tecnico
informe/
    InformeTecnico.pdf  Informe tecnico (UML, Minimax y capturas)
ejecutar.bat            Script para compilar y jugar
```

## Como funciona la IA

La clase `IA_Minimax` explora recursivamente todos los estados posibles del tablero.
Los turnos de la IA son nodos **maximizadores** y los del rival **minimizadores**. El
valor de un estado terminal es `+10 - profundidad` si gana la IA, `profundidad - 10`
si gana el rival, y `0` si es empate. Asi la IA siempre elige la jugada optima y
nunca pierde.

## Autores

- **Denis Valenzuela** - dvalenzuelav1@miumg.edu.gt
- **Miguel Ayala** - mayalad@miumg.edu.gt
- **Mishell Reyes** - mreyesb12@miumg.edu.gt
