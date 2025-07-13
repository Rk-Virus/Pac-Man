@echo off
echo ===================================
echo PacMan Executable Creator
echo ===================================
echo.

:: Check if jpackage is available
where jpackage >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
    echo ERROR: jpackage tool not found!
    echo.
    echo This tool requires JDK 17 or higher to be installed.
    echo Please install JDK from: https://www.oracle.com/java/technologies/downloads/
    echo.
    echo After installing JDK, make sure it's added to your PATH environment variable.
    echo.
    pause
    exit /b 1
)

echo Creating PacMan executable...
echo This may take a minute...
echo.

:: Run jpackage command with more options
jpackage --input build --main-jar PacManGame.jar --main-class App --name PacMan --type exe --win-shortcut --win-menu --icon src\assets\pacmanRight.png --app-version 1.0 --vendor "PacMan Game" --description "Classic PacMan Game"