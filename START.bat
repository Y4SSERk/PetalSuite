@echo off
title PetalSuite Explorer
setlocal

:: Get the directory of the script
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

echo ==========================================
echo       🌸 PETALSUITE EXPLORER 🌸
echo ==========================================
echo.

:: Check if target/classes exists
if not exist "target\classes\com\florist\MainApp.class" (
    echo [INFO] First time setup detected. Building project...
    powershell -ExecutionPolicy Bypass -File "build.ps1"
    if %ERRORLEVEL% neq 0 (
        echo.
        echo [ERROR] Build failed. Please check your Java installation.
        pause
        exit /b %ERRORLEVEL%
    )
    echo.
)

echo [INFO] Launching NexaVerse UI...
powershell -ExecutionPolicy Bypass -File "run.ps1"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Application closed with errors.
    pause
)
