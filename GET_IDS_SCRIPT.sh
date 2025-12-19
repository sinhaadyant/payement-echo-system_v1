#!/bin/bash
# Quick script to get Creditor and Debtor IDs from the API

echo "=== Getting Creditor IDs ==="
curl -s http://localhost:8080/api/v1/creditors | python3 -c "
import sys, json
data = json.load(sys.stdin)
print('Creditor IDs:')
for c in data.get('creditors', []):
    print(f\"  {c['name']}: {c['id']}\")
"

echo ""
echo "=== Getting Debtor IDs ==="
curl -s http://localhost:8080/api/v1/debtors | python3 -c "
import sys, json
data = json.load(sys.stdin)
print('Debtor IDs:')
for d in data.get('debtors', []):
    print(f\"  {d['name']}: {d['id']}\")
"

echo ""
echo "=== Example: Create Payment with First Creditor and Debtor ==="
CREDITOR_ID=$(curl -s http://localhost:8080/api/v1/creditors | python3 -c "import sys, json; print(json.load(sys.stdin)['creditors'][0]['id'])")
DEBTOR_ID=$(curl -s http://localhost:8080/api/v1/debtors | python3 -c "import sys, json; print(json.load(sys.stdin)['debtors'][0]['id'])")

echo "Using Creditor ID: $CREDITOR_ID"
echo "Using Debtor ID: $DEBTOR_ID"
echo ""
echo "Command:"
echo "curl -X 'POST' 'http://localhost:8080/api/v1/payments' \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"amount\": 1500.00, \"currency\": \"USD\", \"status\": \"RECEIVED\", \"creditorId\": \"$CREDITOR_ID\", \"debtorId\": \"$DEBTOR_ID\"}'"

