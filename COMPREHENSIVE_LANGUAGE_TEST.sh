#!/bin/bash

# Comprehensive test script for all APIs with all languages
# Usage: ./COMPREHENSIVE_LANGUAGE_TEST.sh [baseUrl]

BASE_URL="${1:-http://localhost:8080}"
TEST_ID="00000000-0000-0000-0000-000000000000"

echo "🧪 Comprehensive Language Testing for All APIs"
echo "================================================"
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

test_endpoint() {
    local lang=$1
    local lang_name=$2
    local endpoint=$3
    local method=${4:-GET}
    local data=${5:-""}
    local expected_status=${6:-""}
    
    if [ "$method" = "GET" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -H "Accept-Language: $lang" "$BASE_URL$endpoint")
    elif [ "$method" = "POST" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X POST -H "Accept-Language: $lang" \
            -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
    elif [ "$method" = "DELETE" ]; then
        RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE -H "Accept-Language: $lang" "$BASE_URL$endpoint")
    fi
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -1)
    BODY=$(echo "$RESPONSE" | sed '$d')
    
    # Check if response is localized
    ERROR=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('error', ''))" 2>/dev/null || echo "")
    MESSAGE=$(echo "$BODY" | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('message', ''))" 2>/dev/null || echo "")
    
    # Check if error message contains non-ASCII (likely localized)
    if echo "$ERROR$MESSAGE" | grep -qP '[^\x00-\x7F]'; then
        LOCALIZED="✓"
    else
        LOCALIZED="?"
    fi
    
    if [ -n "$expected_status" ] && [ "$HTTP_CODE" = "$expected_status" ]; then
        STATUS_COLOR=$GREEN
        STATUS="✓"
    elif [ "$HTTP_CODE" -ge 200 ] && [ "$HTTP_CODE" -lt 300 ]; then
        STATUS_COLOR=$GREEN
        STATUS="✓"
    elif [ "$HTTP_CODE" -ge 400 ] && [ "$HTTP_CODE" -lt 500 ]; then
        STATUS_COLOR=$YELLOW
        STATUS="!"
    else
        STATUS_COLOR=$RED
        STATUS="✗"
    fi
    
    echo -e "  ${STATUS_COLOR}${STATUS}${NC} [$lang_name] Status: $HTTP_CODE $LOCALIZED"
    if [ -n "$ERROR" ] && [ ${#ERROR} -gt 0 ]; then
        echo "     Error: ${ERROR:0:60}..."
    fi
}

LANGUAGES=("hi:Hindi" "en:English" "ta:Tamil" "ru:Russian" "bn:Bengali" "te:Telugu" "kn:Kannada" "es:Spanish")

echo "1. Payment APIs - GET by Invalid ID (404 errors)"
echo "--------------------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/payments/$TEST_ID" "GET" "" "404"
done

echo ""
echo "2. Payment APIs - GET All (200 success)"
echo "----------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/payments" "GET" "" "200"
done

echo ""
echo "3. Creditor APIs - GET by Invalid ID (404 errors)"
echo "--------------------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/creditors/$TEST_ID" "GET" "" "404"
done

echo ""
echo "4. Creditor APIs - GET All (200 success)"
echo "---------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/creditors" "GET" "" "200"
done

echo ""
echo "5. Debtor APIs - GET by Invalid ID (404 errors)"
echo "-----------------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/debtors/$TEST_ID" "GET" "" "404"
done

echo ""
echo "6. Debtor APIs - GET All (200 success)"
echo "-------------------------------------"
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/debtors" "GET" "" "200"
done

echo ""
echo "7. Payment APIs - POST Validation Errors (400 errors)"
echo "------------------------------------------------------"
INVALID_PAYMENT='{"amount": -100, "currency": "INVALID", "status": "INVALID"}'
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/payments" "POST" "$INVALID_PAYMENT" "400"
done

echo ""
echo "8. Creditor APIs - POST Validation Errors (400 errors)"
echo "-----------------------------------------------------"
INVALID_CREDITOR='{"name": "", "accountNumber": "", "bankCode": ""}'
for lang_pair in "${LANGUAGES[@]}"; do
    IFS=':' read -r lang lang_name <<< "$lang_pair"
    test_endpoint "$lang" "$lang_name" "/api/v1/creditors" "POST" "$INVALID_CREDITOR" "400"
done

echo ""
echo "✅ Comprehensive testing complete!"
echo ""
echo "Legend:"
echo "  ✓ = Success (correct status code)"
echo "  ! = Expected error (4xx status)"
echo "  ✗ = Failure (unexpected status)"
echo "  ✓ = Localized (contains non-ASCII characters)"
echo "  ? = May not be localized"

