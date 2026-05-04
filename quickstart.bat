@echo off
REM HomePS Docker Quick Start Script for Windows

setlocal enabledelayedexpansion

echo.
echo ============================================
echo  HomePS Gaming Cafe Management System
echo  Docker Quick Start Setup
echo ============================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not installed or not in PATH
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker Compose is not installed
    echo Please install Docker Desktop which includes Docker Compose
    pause
    exit /b 1
)

echo [1/4] Checking Docker...
echo Docker version:
docker --version
echo.

echo [2/4] Building the application...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)
echo Maven build completed successfully!
echo.

echo [3/4] Stopping any existing containers...
docker-compose down -v 2>nul
echo.

echo [4/4] Starting services with Docker Compose...
docker-compose up -d

echo.
echo Waiting for services to be ready...
timeout /t 5 /nobreak

docker-compose ps
echo.

REM Check if services are running
docker-compose ps | findstr "Exit" >nul
if errorlevel 1 (
    echo.
    echo ============================================
    echo  SUCCESS! Services are running
    echo ============================================
    echo.
    echo Access the application at:
    echo   http://localhost:8080/HomePS
    echo.
    echo Login with any username/password (demo mode)
    echo.
    echo Useful commands:
    echo   - View logs:       docker-compose logs -f backend
    echo   - Stop services:   docker-compose down
    echo   - Restart:         docker-compose restart
    echo.
) else (
    echo.
    echo ERROR: Some services failed to start
    echo Run 'docker-compose logs' to see error details
    echo.
)

pause

