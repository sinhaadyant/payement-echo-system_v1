# API Verification Guide - Using cURL

This guide provides step-by-step instructions to verify all API endpoints using cURL commands.

## Prerequisites

1. **MySQL must be running**
   ```bash
   # Option 1: Using Docker Compose
   docker-compose up -d mysql
   
   # Option 2: Local MySQL
   # Ensure MySQL is running and accessible
   ```

2. **Application must be running**
   ```bash
   ./gradlew bootRun
   ```

3. **Wait for application to start** (usually 10-30 seconds)
   - Look for: "Started PaymentEchoApplication"
   - Health check: `curl http://localhost:8080/actuator/health`

## Quick Verification Script

Run the automated test script:

```bash
./test_api.sh
```

Or test a specific endpoint:

```bash
./test_api.sh http://localhost:8080
```

## Manual Verification with cURL

### 1. Health & Monitoring

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Application Info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

**Expected**: All should return `200 OK` with JSON data

### 2. Creditor Endpoints

```bash
# Get All Creditors
curl http://localhost:8080/api/v1/creditors

# Get Creditors with Pagination
curl "http://localhost:8080/api/v1/creditors?page=0&size=10"

# Get Creditors with Filter
curl "http://localhost:8080/api/v1/creditors?name=Acme"

# Get Creditor by ID (replace with actual ID from above)
curl http://localhost:8080/api/v1/creditors/{creditor-id}

# Create Creditor
curl -X POST http://localhost:8080/api/v1/creditors \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "name": "Test Creditor",
    "accountNumber": "ACC123456",
    "bankCode": "BANK001",
    "email": "test@example.com",
    "address": "123 Test Street"
  }'

# Get Creditor by Invalid ID (should return 404)
curl http://localhost:8080/api/v1/creditors/00000000-0000-0000-0000-000000000000
```

**Expected**:
- GET requests: `200 OK` with creditor data
- POST request: `201 Created` with new creditor
- Invalid ID: `404 Not Found` with error message

### 3. Debtor Endpoints

```bash
# Get All Debtors
curl http://localhost:8080/api/v1/debtors

# Get Debtors with Pagination
curl "http://localhost:8080/api/v1/debtors?page=0&size=10"

# Get Debtors with Filter
curl "http://localhost:8080/api/v1/debtors?name=John"

# Get Debtor by ID (replace with actual ID)
curl http://localhost:8080/api/v1/debtors/{debtor-id}

# Create Debtor
curl -X POST http://localhost:8080/api/v1/debtors \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "name": "Test Debtor",
    "accountNumber": "DEB123456",
    "bankCode": "BANK001",
    "email": "debtor@example.com",
    "address": "456 Test Avenue"
  }'

# Get Debtor by Invalid ID (should return 404)
curl http://localhost:8080/api/v1/debtors/00000000-0000-0000-0000-000000000000
```

**Expected**: Same as Creditor endpoints

### 4. Payment Endpoints

```bash
# Get All Payments
curl http://localhost:8080/api/v1/payments

# Get Payments with Pagination
curl "http://localhost:8080/api/v1/payments?page=0&size=10"

# Get Payments with Status Filter
curl "http://localhost:8080/api/v1/payments?status=RECEIVED"

# Get Payments with Currency Filter
curl "http://localhost:8080/api/v1/payments?currency=USD"

# Get Payments with Amount Range
curl "http://localhost:8080/api/v1/payments?minAmount=100&maxAmount=1000"

# Get Payments with Date Range
curl "http://localhost:8080/api/v1/payments?startDate=2025-01-01T00:00:00Z&endDate=2025-12-31T23:59:59Z"

# Get Payments with Sorting
curl "http://localhost:8080/api/v1/payments?sort=amount,desc"

# Get Payment by ID (replace with actual ID)
curl http://localhost:8080/api/v1/payments/{payment-id}

# Create Standalone Payment (without creditor/debtor)
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": 1000.00,
    "currency": "USD",
    "status": "RECEIVED"
  }'

# Create Payment with Creditor and Debtor (replace IDs)
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": 1500.00,
    "currency": "USD",
    "status": "RECEIVED",
    "creditorId": "{creditor-id}",
    "debtorId": "{debtor-id}"
  }'

# Echo Payment
curl -X POST http://localhost:8080/api/v1/payments/echo \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": 2000.00,
    "currency": "EUR",
    "status": "PROCESSING"
  }'

# Get Payment by Invalid ID (should return 404)
curl http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000
```

**Expected**:
- GET requests: `200 OK` with payment data
- POST requests: `201 Created` or `200 OK` (for echo) with payment data
- Invalid ID: `404 Not Found` with localized error message

### 5. Validation Tests

```bash
# Invalid Payment - Negative Amount
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": -100,
    "currency": "USD",
    "status": "RECEIVED"
  }'

# Invalid Payment - Invalid Currency
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": 100,
    "currency": "INVALID",
    "status": "RECEIVED"
  }'

# Invalid Payment - Invalid Status
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "amount": 100,
    "currency": "USD",
    "status": "INVALID"
  }'

# Invalid Creditor - Empty Name
curl -X POST http://localhost:8080/api/v1/creditors \
  -H "Content-Type: application/json" \
  -H "Accept-Language: hi" \
  -d '{
    "name": "",
    "accountNumber": "TEST",
    "bankCode": "TEST"
  }'
```

**Expected**: All should return `400 Bad Request` with validation error details

### 6. Internationalization (i18n) Tests

```bash
# Error in Hindi (default)
curl -H "Accept-Language: hi" \
  http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Error in English
curl -H "Accept-Language: en" \
  http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Error in Spanish
curl -H "Accept-Language: es" \
  http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Error in Tamil
curl -H "Accept-Language: ta" \
  http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Error in Russian
curl -H "Accept-Language: ru" \
  http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000
```

**Expected**: All return `404 Not Found` but with error messages in the requested language

### 7. GraphQL Endpoint

```bash
# GraphQL Query - Get All Payments
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ payments { id amount currency status createdAt } }"
  }'

# GraphQL Query - Get Payment by ID
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { payment(id: \"{payment-id}\") { id amount currency status } }"
  }'

# GraphQL Mutation - Create Payment
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createPayment(input: { amount: 500.0, currency: \"USD\", status: \"RECEIVED\" }) { id amount currency status } }"
  }'
```

**Expected**: `200 OK` with GraphQL response containing `data` field

### 8. API Documentation

```bash
# Swagger UI (open in browser)
open http://localhost:8080/swagger-ui.html

# OpenAPI JSON
curl http://localhost:8080/v3/api-docs

# GraphiQL (open in browser)
open http://localhost:8080/graphiql
```

## Expected Response Formats

### Success Response (List)
```json
{
  "total": 10,
  "payments": [
    {
      "id": "uuid",
      "amount": 1000.0,
      "currency": "USD",
      "status": "RECEIVED",
      "createdAt": "2025-01-01T00:00:00Z"
    }
  ]
}
```

### Success Response (Single)
```json
{
  "id": "uuid",
  "amount": 1000.0,
  "currency": "USD",
  "status": "RECEIVED",
  "createdAt": "2025-01-01T00:00:00Z"
}
```

### Error Response (404)
```json
{
  "timestamp": "2025-01-01T00:00:00Z",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Payment not found with id: uuid",
  "path": "/api/v1/payments/uuid"
}
```

### Validation Error Response (400)
```json
{
  "timestamp": "2025-01-01T00:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed",
  "fieldErrors": {
    "amount": "Amount must be greater than 0"
  },
  "path": "/api/v1/payments"
}
```

## Troubleshooting

### Connection Refused
```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# If not running, start it
./gradlew bootRun
```

### MySQL Connection Error
```bash
# Check MySQL is running
mysql -u root -proot -e "SELECT 1"

# Check application logs for MySQL connection errors
tail -f /tmp/payment-app.log
```

### 404 Errors
- Verify the endpoint URL is correct
- Check if the resource ID exists
- Ensure API version is `/api/v1/`

### 400 Validation Errors
- Check request body format (must be valid JSON)
- Verify required fields are present
- Check field value constraints

## Complete Test Sequence

Run this complete sequence to verify everything:

```bash
# 1. Health check
curl http://localhost:8080/actuator/health

# 2. Get initial data
curl http://localhost:8080/api/v1/creditors
curl http://localhost:8080/api/v1/debtors
curl http://localhost:8080/api/v1/payments

# 3. Create test data
CREDITOR_ID=$(curl -s -X POST http://localhost:8080/api/v1/creditors \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","accountNumber":"T001","bankCode":"TB"}' | \
  grep -o '"id":"[^"]*' | cut -d'"' -f4)

DEBTOR_ID=$(curl -s -X POST http://localhost:8080/api/v1/debtors \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","accountNumber":"T002","bankCode":"TB"}' | \
  grep -o '"id":"[^"]*' | cut -d'"' -f4)

# 4. Create payment with creditor/debtor
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d "{\"amount\":1000,\"currency\":\"USD\",\"status\":\"RECEIVED\",\"creditorId\":\"$CREDITOR_ID\",\"debtorId\":\"$DEBTOR_ID\"}"

# 5. Test filters
curl "http://localhost:8080/api/v1/payments?status=RECEIVED&currency=USD"

# 6. Test i18n
curl -H "Accept-Language: en" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000
```

## Automated Testing

Use the provided test script for comprehensive testing:

```bash
# Run all tests
./test_api.sh

# Test against different URL
./test_api.sh http://localhost:8080
```

The script will:
- ✅ Test all endpoints
- ✅ Verify response codes
- ✅ Test validation
- ✅ Test internationalization
- ✅ Provide summary report

---

**Note**: Replace `{creditor-id}`, `{debtor-id}`, and `{payment-id}` with actual UUIDs from previous responses.

