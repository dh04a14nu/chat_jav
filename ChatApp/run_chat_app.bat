@echo off
set PATH_TO_FX=C:\Users\TEMP.KGISLEDU.011\Documents\javafx-sdk-23.0.1\lib

echo Compiling Java files...
javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d bin src/*.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b
)

echo Starting Chat Server...
start java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp bin ChatServer
if %ERRORLEVEL% NEQ 0 (
    echo Failed to start Chat Server.
    pause
    exit /b
)

echo Starting Chat Application...
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp bin ChatApp
if %ERRORLEVEL% NEQ 0 (
    echo Failed to start Chat Application.
    pause
)
