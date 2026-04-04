# PowerShell test script for HomePS application
# Run: powershell -ExecutionPolicy Bypass -File test-windows.ps1

Write-Host "🧪 Testing HomePS Application..." -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# Test 1: Check if Java is running
Write-Host ""
Write-Host "1️⃣  Checking Java/Tomcat Process..." -ForegroundColor Yellow
$javaProcess = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcess) {
    Write-Host "✅ Java/Tomcat is running (PID: $($javaProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "❌ Java/Tomcat is NOT running" -ForegroundColor Red
    Write-Host "   Please start Tomcat: tomcat\bin\startup.bat" -ForegroundColor Yellow
    exit 1
}

# Test 2: Check if application is accessible
Write-Host ""
Write-Host "2️⃣  Testing Application Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/HomePS/home" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ HomePS application is accessible (HTTP $($response.StatusCode))" -ForegroundColor Green
    } else {
        Write-Host "⚠️  HomePS returned HTTP $($response.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️  Cannot access application yet: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "   Application may still be initializing. Wait 10-15 seconds and try again." -ForegroundColor Yellow
}

# Test 3: Check database connectivity
Write-Host ""
Write-Host "3️⃣  Checking PostgreSQL..." -ForegroundColor Yellow
$pgProcess = Get-Process postgres -ErrorAction SilentlyContinue
if ($pgProcess) {
    Write-Host "✅ PostgreSQL is running" -ForegroundColor Green
} else {
    Write-Host "⚠️  PostgreSQL may not be running" -ForegroundColor Yellow
    Write-Host "   Please start PostgreSQL service" -ForegroundColor Yellow
}

# Test 4: Check Tomcat logs
Write-Host ""
Write-Host "4️⃣  Checking Tomcat Logs..." -ForegroundColor Yellow
$logDir = "tomcat\logs"
if (Test-Path $logDir) {
    Write-Host "✅ Tomcat logs directory found" -ForegroundColor Green
    $files = Get-ChildItem -Path $logDir | Select-Object Name, LastWriteTime | Sort-Object LastWriteTime -Descending
    Write-Host "   Recent log files:"
    $files | Select-Object -First 3 | Format-Table -AutoSize -HideTableHeaders
} else {
    Write-Host "⚠️  Logs directory not found" -ForegroundColor Yellow
}

# Test 5: Check WAR file
Write-Host ""
Write-Host "5️⃣  Checking Deployment..." -ForegroundColor Yellow
$warFile = "target\HomePS.war"
$webappsFile = "tomcat\webapps\HomePS.war"

if (Test-Path $warFile) {
    $warSize = (Get-Item $warFile).Length / 1MB
    Write-Host "✅ WAR file found: $warFile ($([Math]::Round($warSize, 2)) MB)" -ForegroundColor Green
}

if (Test-Path $webappsFile) {
    $deploySize = (Get-Item $webappsFile).Length / 1MB
    Write-Host "✅ WAR deployed: $webappsFile ($([Math]::Round($deploySize, 2)) MB)" -ForegroundColor Green
} else {
    Write-Host "⚠️  WAR not deployed to Tomcat" -ForegroundColor Yellow
}

# Test 6: Check key endpoints
Write-Host ""
Write-Host "6️⃣  Testing API Endpoints..." -ForegroundColor Yellow

$endpoints = @(
    "http://localhost:8080/HomePS/home",
    "http://localhost:8080/HomePS/sukien",
    "http://localhost:8080/HomePS/thongke"
)

foreach ($endpoint in $endpoints) {
    try {
        $response = Invoke-WebRequest -Uri $endpoint -UseBasicParsing -TimeoutSec 3 -ErrorAction SilentlyContinue
        $status = $response.StatusCode
        Write-Host "   ✅ $endpoint - HTTP $status" -ForegroundColor Green
    } catch {
        Write-Host "   ⚠️  $endpoint - Not responding" -ForegroundColor Yellow
    }
}

# Summary
Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "✨ Test Complete!" -ForegroundColor Cyan
Write-Host ""
Write-Host "📍 Access the application at:" -ForegroundColor Green
Write-Host "   http://localhost:8080/HomePS/home" -ForegroundColor Green
Write-Host ""
Write-Host "📚 Default login credentials:" -ForegroundColor Green
Write-Host "   Database: HomePS" -ForegroundColor Green
Write-Host "   User: postgres" -ForegroundColor Green
Write-Host "   Password: postgres" -ForegroundColor Green
Write-Host ""
Write-Host "🛑 To stop Tomcat:" -ForegroundColor Yellow
Write-Host "   tomcat\bin\shutdown.bat" -ForegroundColor Yellow
Write-Host ""

