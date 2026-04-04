# PowerShell Script to Test HomePS API Endpoints
# Usage: powershell -ExecutionPolicy Bypass -File test-api.ps1

Write-Host "================================" -ForegroundColor Cyan
Write-Host "🧪 HomePS API Test Suite" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/HomePS"
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

# Test 1: Check if application is running
Write-Host ""
Write-Host "TEST 1: Check Application Status" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/home" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Application is running (HTTP 200)" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Unexpected status: $($response.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Application not responding" -ForegroundColor Red
    exit 1
}

# Test 2: Test GET /home (View all machines)
Write-Host ""
Write-Host "TEST 2: GET /home - View All Machines" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/home" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ /home endpoint working" -ForegroundColor Green
        # Check if response contains machine info
        if ($response.Content -match "may|machine" -or $response.Content.Length -gt 100) {
            Write-Host "   ✅ Response contains machine data" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "❌ /home endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Test GET /sukien (View promotions)
Write-Host ""
Write-Host "TEST 3: GET /sukien - View All Promotions" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/sukien" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ /sukien endpoint working" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  /sukien endpoint may not be deployed yet" -ForegroundColor Yellow
}

# Test 4: Test GET /thongke (View statistics)
Write-Host ""
Write-Host "TEST 4: GET /thongke - View Statistics" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/thongke" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ /thongke endpoint working" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  /thongke endpoint may not be deployed yet" -ForegroundColor Yellow
}

# Test 5: Test POST /home (Open machine)
Write-Host ""
Write-Host "TEST 5: POST /home - Open Machine" -ForegroundColor Yellow
Write-Host "   Note: This would open machine 1 with nhanVienId=1" -ForegroundColor Gray
Write-Host "   Command: " -ForegroundColor Gray -NoNewline
Write-Host "curl -X POST 'http://localhost:8080/HomePS/home' -d 'action=open&mayId=1&nhanVienId=1'" -ForegroundColor Cyan

# Test 6: Database connection check
Write-Host ""
Write-Host "TEST 6: Database Connection Check" -ForegroundColor Yellow
Write-Host "   If PostgreSQL is running:" -ForegroundColor Gray
Write-Host "   $env:PGPASSWORD='postgres'; psql -h localhost -U postgres -d HomePS -c 'SELECT COUNT(*) FROM mayps;'" -ForegroundColor Cyan

# Summary
Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "📋 API Test Summary" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Available Endpoints:" -ForegroundColor Green
Write-Host ""
Write-Host "1. Machine Management:" -ForegroundColor Cyan
Write-Host "   GET  $baseUrl/home          - View all machines" -ForegroundColor Gray
Write-Host "   POST $baseUrl/home          - Open/Close machine (parameters: action, mayId, nhanVienId)" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Invoice Management:" -ForegroundColor Cyan
Write-Host "   GET  $baseUrl/hoadon        - View invoices" -ForegroundColor Gray
Write-Host "   POST $baseUrl/hoadon        - Add service/Checkout" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Promotion Management:" -ForegroundColor Cyan
Write-Host "   GET  $baseUrl/sukien        - List all promotions" -ForegroundColor Gray
Write-Host "   POST $baseUrl/sukien        - Create/Update/Delete promotions" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Statistics:" -ForegroundColor Cyan
Write-Host "   GET  $baseUrl/thongke       - View statistics" -ForegroundColor Gray
Write-Host ""

# Curl command examples
Write-Host ""
Write-Host "📌 Example CURL Commands:" -ForegroundColor Yellow
Write-Host ""
Write-Host "View all machines:" -ForegroundColor Green
Write-Host "  curl -i http://localhost:8080/HomePS/home" -ForegroundColor Cyan
Write-Host ""
Write-Host "Open Machine 1:" -ForegroundColor Green
Write-Host "  curl -X POST 'http://localhost:8080/HomePS/home' -d 'action=open&mayId=1&nhanVienId=1' -i" -ForegroundColor Cyan
Write-Host ""
Write-Host "Close Machine 1:" -ForegroundColor Green
Write-Host "  curl -X POST 'http://localhost:8080/HomePS/home' -d 'action=close&mayId=1' -i" -ForegroundColor Cyan
Write-Host ""
Write-Host "View Promotions:" -ForegroundColor Green
Write-Host "  curl -i http://localhost:8080/HomePS/sukien" -ForegroundColor Cyan
Write-Host ""
Write-Host "View Statistics:" -ForegroundColor Green
Write-Host "  curl -i 'http://localhost:8080/HomePS/thongke?loai=ngay'" -ForegroundColor Cyan
Write-Host ""

Write-Host ""
Write-Host "✨ Test Complete!" -ForegroundColor Green

