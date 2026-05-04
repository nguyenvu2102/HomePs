#!/bin/bash
# HomePS Docker Quick Start Script for Linux/MacOS

set -e

echo ""
echo "============================================"
echo " HomePS Gaming Cafe Management System"
echo " Docker Quick Start Setup"
echo "============================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed"
    echo "Please install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose is not installed"
    echo "Please install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

echo "[1/4] Checking Docker..."
echo "Docker version: $(docker --version)"
echo ""

echo "[2/4] Building the application..."
mvn clean package -DskipTests || {
    echo "ERROR: Maven build failed"
    exit 1
}
echo "Maven build completed successfully!"
echo ""

echo "[3/4] Stopping any existing containers..."
docker-compose down -v 2>/dev/null || true
echo ""

echo "[4/4] Starting services with Docker Compose..."
docker-compose up -d

echo ""
echo "Waiting for services to be ready..."
sleep 5

docker-compose ps
echo ""

if docker-compose ps | grep -q "Exit"; then
    echo ""
    echo "ERROR: Some services failed to start"
    echo "Run 'docker-compose logs' to see error details"
    echo ""
    exit 1
else
    echo ""
    echo "============================================"
    echo " SUCCESS! Services are running"
    echo "============================================"
    echo ""
    echo "Access the application at:"
    echo "  http://localhost:8080/HomePS"
    echo ""
    echo "Login with any username/password (demo mode)"
    echo ""
    echo "Useful commands:"
    echo "  - View logs:       docker-compose logs -f backend"
    echo "  - View DB logs:    docker-compose logs -f db"
    echo "  - Stop services:   docker-compose down"
    echo "  - Restart:         docker-compose restart"
    echo "  - Rebuild image:   docker-compose up -d --build"
    echo ""
fi

