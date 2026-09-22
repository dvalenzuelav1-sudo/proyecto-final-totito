@echo off
REM ============================================================
REM  Totito - Proyecto Final Programacion II
REM  Compila el proyecto y ejecuta el juego.
REM ============================================================
setlocal
cd /d "%~dp0"

echo Compilando el proyecto...
if not exist out mkdir out
javac -encoding UTF-8 -d out src\com\umg\totito\*.java
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar el proyecto.
    pause
    exit /b 1
)

echo Iniciando el juego de Totito...
java -cp out com.umg.totito.JuegoMain

endlocal
