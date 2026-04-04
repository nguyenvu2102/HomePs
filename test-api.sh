#!/bin/bash
# Bash script to test HomePS API using curl
# Usage: bash test-api.sh

echo "================================"
echo "🧪 HomePS API Test Suite (curl)"
echo "================================"
echo ""

BASE_URL="http://localhost:8080/HomePS"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Test 1: Check if application is running
echo -e "${YELLOW}TEST 1: Check Application Status${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/home")
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Application is running (HTTP $HTTP_CODE)${NC}"
else
    echo -e "${RED}❌ Application returned HTTP $HTTP_CODE${NC}"
    exit 1
fi

# Test 2: Test GET /home
echo ""
echo -e "${YELLOW}TEST 2: GET /home - View All Machines${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/home")
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ /home endpoint working${NC}"
    echo "   Response Preview:"
    curl -s "$BASE_URL/home" | head -c 200
    echo "   ..."
else
    echo -e "${RED}❌ /home failed with HTTP $HTTP_CODE${NC}"
fi

# Test 3: Test GET /sukien
echo ""
echo ""
echo -e "${YELLOW}TEST 3: GET /sukien - View All Promotions${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/sukien")
if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "302" ]; then
    echo -e "${GREEN}✅ /sukien endpoint responding (HTTP $HTTP_CODE)${NC}"
else
    echo -e "${YELLOW}⚠️  /sukien returned HTTP $HTTP_CODE${NC}"
fi

# Test 4: Test GET /thongke
echo ""
echo -e "${YELLOW}TEST 4: GET /thongke - View Statistics${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/thongke")
if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "302" ]; then
    echo -e "${GREEN}✅ /thongke endpoint responding (HTTP $HTTP_CODE)${NC}"
else
    echo -e "${YELLOW}⚠️  /thongke returned HTTP $HTTP_CODE${NC}"
fi

# Summary
echo ""
echo "================================"
echo -e "${CYAN}📋 API Test Summary${NC}"
echo "================================"
echo ""
echo -e "${GREEN}Available Endpoints:${NC}"
echo ""
echo "1. Machine Management:"
echo -e "   GET  ${CYAN}$BASE_URL/home${NC}          - View all machines"
echo -e "   POST ${CYAN}$BASE_URL/home${NC}          - Open/Close machine"
echo ""
echo "2. Promotion Management:"
echo -e "   GET  ${CYAN}$BASE_URL/sukien${NC}        - List all promotions"
echo -e "   POST ${CYAN}$BASE_URL/sukien${NC}        - Create/Update/Delete promotions"
echo ""
echo "3. Statistics:"
echo -e "   GET  ${CYAN}$BASE_URL/thongke${NC}       - View statistics"
echo ""

# Example commands
echo -e "${YELLOW}📌 Example CURL Commands:${NC}"
echo ""
echo "View all machines:"
echo -e "  ${CYAN}curl -i $BASE_URL/home${NC}"
echo ""
echo "View with header info:"
echo -e "  ${CYAN}curl -i -X GET $BASE_URL/home${NC}"
echo ""
echo "View Promotions:"
echo -e "  ${CYAN}curl -i $BASE_URL/sukien${NC}"
echo ""
echo "View Statistics (by day):"
echo -e "  ${CYAN}curl -i '$BASE_URL/thongke?loai=ngay'${NC}"
echo ""
echo "View Statistics (by week):"
echo -e "  ${CYAN}curl -i '$BASE_URL/thongke?loai=tuan'${NC}"
echo ""
echo "View Statistics (by month):"
echo -e "  ${CYAN}curl -i '$BASE_URL/thongke?loai=thang'${NC}"
echo ""

echo -e "${GREEN}✨ Test Complete!${NC}"

