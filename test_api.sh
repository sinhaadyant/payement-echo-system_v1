#!/bin/bash

# Payment Echo System - API Verification Script
# This script tests all API endpoints using curl

BASE_URL="${1:-http://localhost:8080}"
TEST_ID="00000000-0000-0000-0000-000000000000"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "=========================================="
echo "Payment Echo System - API Verification"
echo "=========================================="
echo ""
echo "Base URL: $BASE_URL"
echo ""

# Test counter
PASSED=0
FAILED=0

test_endpoint() {
    local name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local expected_status=$5
    local lang=${6:-hi}
    
    echo -e "${BLUE}Testing: $name${NC}"
    echo "  $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -H "Accept-Language: $lang" "$BASE_URL$endpoint" 2>&1)
    elif [ "$method" = "POST" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X POST -H "Accept-Language: $lang" \
            -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint" 2>&1)
    elif [ "$method" = "DELETE" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE -H "Accept-Language: $lang" "$BASE_URL$endpoint" 2>&1)
    fi
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" = "$expected_status" ]; then
        echo -e "  ${GREEN}✓ PASS${NC} - Status: $HTTP_CODE (Expected: $expected_status)"
        PASSED=$((PASSED + 1))
        if [ -n "$BODY" ] && [ ${#BODY} -lt 200 ]; then
            echo "  Response: ${BODY:0:100}..."
        fi
    else
        echo -e "  ${RED}✗ FAIL${NC} - Status: $HTTP_CODE (Expected: $expected_status)"
        FAILED=$((FAILED + 1))
        if [ -n "$BODY" ]; then
            echo "  Error: ${BODY:0:150}..."
        fi
    fi
    echo ""
}

# Check if server is running
echo "Checking if server is running..."
if curl -s -f "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Server is running${NC}"
    echo ""
else
    echo -e "${RED}✗ Server is not running at $BASE_URL${NC}"
    echo "Please start the application first:"
    echo "  ./gradlew bootRun"
    echo ""
    exit 1
fi

# Health Check
echo "=========================================="
echo "1. Health & Monitoring Endpoints"
echo "=========================================="
test_endpoint "Health Check" "GET" "/actuator/health" "" "200"
test_endpoint "Application Info" "GET" "/actuator/info" "" "200"
test_endpoint "Metrics" "GET" "/actuator/metrics" "" "200"

# Creditor Endpoints
echo "=========================================="
echo "2. Creditor Endpoints"
echo "=========================================="
test_endpoint "Get All Creditors" "GET" "/api/v1/creditors" "" "200"
test_endpoint "Get All Creditors (English)" "GET" "/api/v1/creditors" "" "200" "en"
test_endpoint "Get Creditor by Invalid ID (404)" "GET" "/api/v1/creditors/$TEST_ID" "" "404"
test_endpoint "Get Creditors with Pagination" "GET" "/api/v1/creditors?page=0&size=10" "" "200"
test_endpoint "Get Creditors with Filter" "GET" "/api/v1/creditors?name=Acme" "" "200"

# Create a creditor for further tests
echo "Creating test creditor..."
CREDITOR_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -H "Accept-Language: hi" \
    -d '{"name":"Test Creditor API","accountNumber":"TEST001","bankCode":"TESTBANK"}' \
    "$BASE_URL/api/v1/creditors")
CREDITOR_ID=$(echo "$CREDITOR_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
if [ -n "$CREDITOR_ID" ]; then
    echo -e "${GREEN}✓ Created creditor with ID: $CREDITOR_ID${NC}"
    test_endpoint "Get Creditor by Valid ID" "GET" "/api/v1/creditors/$CREDITOR_ID" "" "200"
else
    echo -e "${YELLOW}⚠ Could not create creditor (may already exist)${NC}"
fi
echo ""

# Debtor Endpoints
echo "=========================================="
echo "3. Debtor Endpoints"
echo "=========================================="
test_endpoint "Get All Debtors" "GET" "/api/v1/debtors" "" "200"
test_endpoint "Get Debtor by Invalid ID (404)" "GET" "/api/v1/debtors/$TEST_ID" "" "404"
test_endpoint "Get Debtors with Pagination" "GET" "/api/v1/debtors?page=0&size=10" "" "200"
test_endpoint "Get Debtors with Filter" "GET" "/api/v1/debtors?name=John" "" "200"

# Create a debtor for further tests
echo "Creating test debtor..."
DEBTOR_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -H "Accept-Language: hi" \
    -d '{"name":"Test Debtor API","accountNumber":"TEST002","bankCode":"TESTBANK"}' \
    "$BASE_URL/api/v1/debtors")
DEBTOR_ID=$(echo "$DEBTOR_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
if [ -n "$DEBTOR_ID" ]; then
    echo -e "${GREEN}✓ Created debtor with ID: $DEBTOR_ID${NC}"
    test_endpoint "Get Debtor by Valid ID" "GET" "/api/v1/debtors/$DEBTOR_ID" "" "200"
else
    echo -e "${YELLOW}⚠ Could not create debtor (may already exist)${NC}"
fi
echo ""

# Payment Endpoints
echo "=========================================="
echo "4. Payment Endpoints"
echo "=========================================="
test_endpoint "Get All Payments" "GET" "/api/v1/payments" "" "200"
test_endpoint "Get Payment by Invalid ID (404)" "GET" "/api/v1/payments/$TEST_ID" "" "404"
test_endpoint "Get Payments with Pagination" "GET" "/api/v1/payments?page=0&size=10" "" "200"
test_endpoint "Get Payments with Status Filter" "GET" "/api/v1/payments?status=RECEIVED" "" "200"
test_endpoint "Get Payments with Currency Filter" "GET" "/api/v1/payments?currency=USD" "" "200"
test_endpoint "Get Payments with Amount Range" "GET" "/api/v1/payments?minAmount=100&maxAmount=1000" "" "200"

# Create a standalone payment
echo "Creating standalone payment..."
PAYMENT_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -H "Accept-Language: hi" \
    -d '{"amount":999.99,"currency":"USD","status":"RECEIVED"}' \
    "$BASE_URL/api/v1/payments")
PAYMENT_ID=$(echo "$PAYMENT_RESPONSE" | grep -o '"id":"[^"]*' | cut -d'"' -f4)
if [ -n "$PAYMENT_ID" ]; then
    echo -e "${GREEN}✓ Created payment with ID: $PAYMENT_ID${NC}"
    test_endpoint "Get Payment by Valid ID" "GET" "/api/v1/payments/$PAYMENT_ID" "" "200"
else
    echo -e "${YELLOW}⚠ Could not create payment${NC}"
fi
echo ""

# Payment Echo Endpoint
echo "=========================================="
echo "5. Payment Echo Endpoint"
echo "=========================================="
test_endpoint "Echo Payment" "POST" "/api/v1/payments/echo" \
    '{"amount":1234.56,"currency":"EUR","status":"PROCESSING"}' "200"

# Validation Tests
echo "=========================================="
echo "6. Validation Tests"
echo "=========================================="
test_endpoint "Invalid Payment (Negative Amount)" "POST" "/api/v1/payments" \
    '{"amount":-100,"currency":"USD","status":"RECEIVED"}' "400"
test_endpoint "Invalid Payment (Invalid Currency)" "POST" "/api/v1/payments" \
    '{"amount":100,"currency":"INVALID","status":"RECEIVED"}' "400"
test_endpoint "Invalid Creditor (Empty Name)" "POST" "/api/v1/creditors" \
    '{"name":"","accountNumber":"TEST","bankCode":"TEST"}' "400"

# Internationalization Tests
echo "=========================================="
echo "7. Internationalization (i18n) Tests"
echo "=========================================="
test_endpoint "Error in Hindi (default)" "GET" "/api/v1/payments/$TEST_ID" "" "404" "hi"
test_endpoint "Error in English" "GET" "/api/v1/payments/$TEST_ID" "" "404" "en"
test_endpoint "Error in Spanish" "GET" "/api/v1/payments/$TEST_ID" "" "404" "es"
test_endpoint "Error in Tamil" "GET" "/api/v1/payments/$TEST_ID" "" "404" "ta"

# GraphQL Endpoint (if available)
echo "=========================================="
echo "8. GraphQL Endpoint"
echo "=========================================="
GRAPHQL_RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" \
    -d '{"query":"{ payments { id amount currency status } }"}' \
    "$BASE_URL/graphql")
if echo "$GRAPHQL_RESPONSE" | grep -q "data"; then
    echo -e "${GREEN}✓ GraphQL endpoint working${NC}"
    PASSED=$((PASSED + 1))
else
    echo -e "${YELLOW}⚠ GraphQL endpoint may not be configured${NC}"
fi
echo ""

# Summary
echo "=========================================="
echo "Test Summary"
echo "=========================================="
TOTAL=$((PASSED + FAILED))
echo -e "Total Tests: $TOTAL"
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}✗ Some tests failed${NC}"
    exit 1
fi

