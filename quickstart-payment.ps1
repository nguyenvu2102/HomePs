#!/usr/bin/env pwsh

# HomePS Quick Start Script
# Usage: .\quickstart-payment.ps1

$ErrorActionPreference = "Continue"
$homepsRoot = "E:\HUST\HomePS"
$tomcatBin = "$homepsRoot\tomcat\bin"

Write-Host "🚀 HomePS Quick Start" -ForegroundColor Cyan
Write-Host "===================" -ForegroundColor Cyan
Write-Host ""

# Check if Tomcat is already running
Write-Host "📡 Checking Tomcat status..." -ForegroundColor Yellow
$tomcatProcess = Get-Process | Where-Object { $_.ProcessName -like "*java*" } | Where-Object { $_.CommandLine -like "*catalina*" -or $_.CommandLine -like "*tomcat*" }

if ($tomcatProcess) {
    Write-Host "⚠️  Tomcat is already running (PID: $($tomcatProcess.Id))" -ForegroundColor Yellow
    Write-Host "Starting in 30 seconds..." -ForegroundColor Gray
    Start-Sleep -Seconds 30
} else {
    Write-Host "🟢 Tomcat is not running. Starting Tomcat..." -ForegroundColor Green

    # Start Tomcat
    Push-Location $tomcatBin
    .\startup.bat | Out-Null
    Pop-Location

    Write-Host "⏳ Waiting 20 seconds for Tomcat to start..." -ForegroundColor Yellow
    Start-Sleep -Seconds 20
}

# Check PostgreSQL Connection
Write-Host ""
Write-Host "🔍 Checking PostgreSQL..." -ForegroundColor Yellow
try {
    $result = & psql -U postgres -h localhost -c "SELECT 1;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ PostgreSQL is running" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PostgreSQL might not be responding. Check your connection." -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️  Could not connect to PostgreSQL. Make sure it's running." -ForegroundColor Yellow
}

# Open Browser
Write-Host ""
Write-Host "🌐 Opening HomePS app in browser..." -ForegroundColor Cyan
Start-Sleep -Seconds 3
Start-Process "http://localhost:8080/HomePS/"

Write-Host ""
Write-Host "✅ HomePS is ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Quick tips:" -ForegroundColor Cyan
Write-Host "  - Open may chi tiet tren panel ben phai" -ForegroundColor Gray
Write-Host "  - Thoi gian choi - tien cap nhat live moi giay" -ForegroundColor Gray
Write-Host "  - Khi dong may, dialog hoa don se hien thi" -ForegroundColor Gray
Write-Host "  - Xem TEST_PAYMENT.md de biet chi tiet" -ForegroundColor Gray
Write-Host ""
Write-Host "Logs:" -ForegroundColor Cyan
Write-Host "  Tomcat: $homepsRoot\tomcat\logs\catalina.out" -ForegroundColor Gray
Write-Host ""

# Keep script alive
Write-Host "Script will stay open. Press Ctrl+C to stop." -ForegroundColor Yellow
Write-Host ""

while ($true) {
    Start-Sleep -Seconds 5
}

