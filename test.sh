#!/bin/bash
# Test script to verify HomePS application is running

echo "🧪 Testing HomePS Application..."
echo "================================"

# Test 1: Check if Tomcat process is running
echo ""
echo "1️⃣  Checking Tomcat Process..."
if pgrep -f "tomcat" > /dev/null; then
    echo "✅ Tomcat is running"
else
    echo "❌ Tomcat is NOT running"
    exit 1
fi

# Test 2: Check if application is accessible
echo ""
echo "2️⃣  Testing Application Endpoint..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/HomePS/home)
if [ "$response" = "200" ]; then
    echo "✅ HomePS application is accessible (HTTP $response)"
else
    echo "⚠️  HomePS returned HTTP $response (may need more time to load)"
fi

# Test 3: Check database connectivity
echo ""
echo "3️⃣  Checking Database..."
if command -v psql &> /dev/null; then
    if PGPASSWORD=postgres psql -h localhost -U postgres -d HomePS -c "SELECT COUNT(*) FROM mayps;" &>/dev/null; then
        echo "✅ Database connection successful"
    else
        echo "❌ Cannot connect to database"
        echo "   Make sure PostgreSQL is running and database 'HomePS' exists"
    fi
else
    echo "⚠️  psql not found, skipping database check"
fi

# Test 4: Check API endpoints
echo ""
echo "4️⃣  Testing API Endpoints..."

echo "   - Testing /home endpoint..."
curl -s -o /dev/null -w "     HTTP Status: %{http_code}\n" http://localhost:8080/HomePS/home

echo "   - Testing /sukien endpoint..."
curl -s -o /dev/null -w "     HTTP Status: %{http_code}\n" http://localhost:8080/HomePS/sukien

echo "   - Testing /thongke endpoint..."
curl -s -o /dev/null -w "     HTTP Status: %{http_code}\n" http://localhost:8080/HomePS/thongke

# Test 5: Check Tomcat logs
echo ""
echo "5️⃣  Checking Tomcat Logs..."
log_file="tomcat/logs/catalina.log"
if [ -f "$log_file" ]; then
    echo "✅ Tomcat log file found"
    echo "   Recent errors:"
    grep -i "error\|exception" "$log_file" | tail -5
else
    echo "⚠️  Log file not found yet (may still be initializing)"
fi

echo ""
echo "================================"
echo "✨ Test Complete!"
echo ""
echo "📍 Access the application at:"
echo "   http://localhost:8080/HomePS/home"
echo ""

