# Postman Collection Testing Guide

## 📋 Overview

The Postman collection includes comprehensive test scripts that automatically:

- ✅ Validate response status codes
- ✅ Validate response structure
- ✅ Auto-extract IDs to collection variables
- ✅ Test filter results
- ✅ Test validation errors
- ✅ Test error responses

## 🚀 Quick Start

### 1. Import Collection

1. Open Postman
2. Click **Import** → Select `postman/PaymentEchoSystem.postman_collection.json`
3. Collection will be imported with all requests and tests

### 2. Set Base URL

The collection uses `{{baseUrl}}` variable (default: `http://localhost:8080`)

To change:

- Right-click collection → **Edit**
- Go to **Variables** tab
- Update `baseUrl` if needed

### 3. Run Collection

**Option A: Run Individual Requests**

- Click any request → **Send**
- Check **Test Results** tab for validation results

**Option B: Run Entire Collection**

- Click collection → **Run**
- Select requests to run → **Run Payment Echo System API v1**
- View test results summary

## 🔄 Auto-Populated Variables

The collection automatically extracts IDs from responses:

| Variable         | Set By                             | Used In                                             |
| ---------------- | ---------------------------------- | --------------------------------------------------- |
| `{{paymentId}}`  | Get All Payments, Create Payment   | Get Payment by ID, Delete Payment                   |
| `{{creditorId}}` | Get All Creditors, Create Creditor | Get Creditor by ID, Create Payment, Delete Creditor |
| `{{debtorId}}`   | Get All Debtors, Create Debtor     | Get Debtor by ID, Create Payment, Delete Debtor     |

### How It Works

1. Run **"Get All Payments"** → Sets `{{paymentId}}` automatically
2. Run **"Get All Creditors"** → Sets `{{creditorId}}` automatically
3. Run **"Get All Debtors"** → Sets `{{debtorId}}` automatically
4. Subsequent requests use these variables automatically

## 📝 Request Categories

### 1. Health & Info

- ✅ Health Check - Validates health endpoint
- ✅ Application Info - Gets app information
- ✅ Metrics - Gets application metrics

### 2. Payments

**GET Requests:**

- ✅ Get All Payments - Lists all payments, auto-extracts ID
- ✅ Get Payments with Filters - Filters by status, currency, amount range
- ✅ Get Payments Filtered by Currency - EUR filter example
- ✅ Get Payments Sorted - Sorted by amount descending
- ✅ Get Payment by ID - Uses `{{paymentId}}` variable

**POST Requests:**

- ✅ Create Payment (Standalone) - No IDs needed, works immediately
- ✅ Create Payment (With Creditor/Debtor) - Requires `{{creditorId}}` and `{{debtorId}}`
- ✅ Echo Payment - Echoes and saves payment

**Tests Include:**

- Status code validation (200, 201)
- Response structure validation
- Data matching validation
- Filter result validation

### 3. Creditors

**GET Requests:**

- ✅ Get All Creditors - Lists all creditors, auto-extracts ID
- ✅ Get Creditors Filtered by Name - "Acme" filter example
- ✅ Get Creditors Filtered by Bank Code - BANK001 filter example
- ✅ Get Creditor by ID - Uses `{{creditorId}}` variable

**POST Requests:**

- ✅ Create Creditor (Sample: Acme Corporation) - Sample data example

**DELETE Requests:**

- ✅ Delete Creditor (Soft Delete) - Uses `{{creditorId}}`, validates 204 response

**Tests Include:**

- Status code validation
- Filter result validation
- Response structure validation

### 4. Debtors

**GET Requests:**

- ✅ Get All Debtors - Lists all debtors, auto-extracts ID
- ✅ Get Debtors Filtered by Name - "John" filter example
- ✅ Get Debtors Filtered by Bank Code - BANK002 filter example
- ✅ Get Debtor by ID - Uses `{{debtorId}}` variable

**POST Requests:**

- ✅ Create Debtor (Sample: John Doe) - Sample data example

**DELETE Requests:**

- ✅ Delete Debtor (Soft Delete) - Uses `{{debtorId}}`, validates 204 response

### 5. Internationalization (i18n)

- ✅ Get Payment (English) - Tests English error messages
- ✅ Get Payment (Spanish) - Tests Spanish error messages

**Note:** Uses `{{paymentId}}` variable. Run "Get All Payments" first.

### 6. Validation Tests

- ✅ Invalid Payment (Negative Amount) - Tests 400 response
- ✅ Invalid Payment (Invalid Currency) - Tests validation
- ✅ Invalid Payment (Invalid Status) - Tests validation
- ✅ Invalid Creditor (Empty Fields) - Tests required field validation

**Tests Include:**

- Status code validation (400)
- Error message validation

### 7. Error Tests

- ✅ Payment Not Found (404) - Tests 404 response
- ✅ Creditor Not Found (404) - Tests 404 response
- ✅ Debtor Not Found (404) - Tests 404 response

**Tests Include:**

- Status code validation (404)
- Error message validation

## 🧪 Test Scripts

All requests include test scripts that validate:

### Success Responses (200, 201)

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Response has required fields", function () {
  var jsonData = pm.response.json();
  pm.expect(jsonData).to.have.property("id");
});
```

### Filter Validation

```javascript
pm.test("All payments have RECEIVED status", function () {
  var jsonData = pm.response.json();
  if (jsonData.payments && jsonData.payments.length > 0) {
    jsonData.payments.forEach(function (payment) {
      pm.expect(payment.status).to.eql("RECEIVED");
    });
  }
});
```

### Error Responses (400, 404)

```javascript
pm.test("Status code is 404", function () {
  pm.response.to.have.status(404);
});

pm.test("Response has error message", function () {
  var jsonData = pm.response.json();
  pm.expect(jsonData).to.have.property("message");
});
```

## 📊 Testing Workflow

### Recommended Order:

1. **Setup Variables:**

   ```
   Get All Creditors → Sets {{creditorId}}
   Get All Debtors → Sets {{debtorId}}
   Get All Payments → Sets {{paymentId}}
   ```

2. **Test CRUD Operations:**

   ```
   Create Payment (Standalone) → Creates and sets {{paymentId}}
   Get Payment by ID → Uses {{paymentId}}
   Create Payment (With Creditor/Debtor) → Uses {{creditorId}} and {{debtorId}}
   ```

3. **Test Filtering:**

   ```
   Get Payments with Filters (Sample: USD RECEIVED)
   Get Payments Filtered by Currency (EUR)
   Get Creditors Filtered by Name (Sample: Acme)
   Get Debtors Filtered by Name (Sample: John)
   ```

4. **Test Validation:**

   ```
   Invalid Payment (Negative Amount)
   Invalid Payment (Invalid Currency)
   Invalid Payment (Invalid Status)
   Invalid Creditor (Empty Fields)
   ```

5. **Test Errors:**

   ```
   Payment Not Found (404)
   Creditor Not Found (404)
   Debtor Not Found (404)
   ```

6. **Test i18n:**
   ```
   Get Payment (English)
   Get Payment (Spanish)
   ```

## ✅ Expected Test Results

When running the collection, you should see:

- ✅ **All GET requests**: 200 status, valid response structure
- ✅ **All POST requests**: 201 status (create), 200 status (echo)
- ✅ **All DELETE requests**: 204 status
- ✅ **All filter requests**: Results match filter criteria
- ✅ **All validation tests**: 400 status with error message
- ✅ **All error tests**: 404 status with error message
- ✅ **All i18n tests**: Appropriate language in error messages

## 🔍 Viewing Test Results

### In Postman UI:

1. Run a request
2. Click **Test Results** tab
3. See all test assertions and their status

### In Collection Runner:

1. Click collection → **Run**
2. View summary of all tests
3. See pass/fail counts
4. Click individual requests to see detailed results

## 🐛 Troubleshooting

### Variables Not Set

**Problem:** `{{creditorId}}`, `{{debtorId}}`, or `{{paymentId}}` is empty

**Solution:**

1. Run "Get All Creditors" first
2. Run "Get All Debtors" first
3. Run "Get All Payments" first
4. These requests automatically set the variables

### 404 Errors

**Problem:** Getting 404 when using variables

**Solution:**

- Ensure variables are set by running GET requests first
- Check that the IDs exist in the database
- Use "Create Payment (Standalone)" which doesn't need IDs

### Filter Tests Failing

**Problem:** Filter tests show no results

**Solution:**

- Ensure sample data exists in database
- Check filter values match sample data
- Run "Get All Payments" to see available data

## 📈 Collection Statistics

- **Total Requests:** 30+
- **Test Scripts:** All requests include tests
- **Auto-Extraction:** 3 variables auto-populated
- **Coverage:** All endpoints, filters, validation, errors

## 🎯 Quick Test Checklist

- [ ] Health Check returns 200
- [ ] Get All Payments returns list with total count
- [ ] Create Payment (Standalone) returns 201 with ID
- [ ] Get Payment by ID returns 200 with payment data
- [ ] Filter by status returns only matching payments
- [ ] Filter by currency returns only matching payments
- [ ] Invalid payment returns 400 with error message
- [ ] Non-existent payment returns 404
- [ ] Get All Creditors auto-sets {{creditorId}}
- [ ] Get All Debtors auto-sets {{debtorId}}
- [ ] Create Payment with IDs returns 201
- [ ] Delete Creditor returns 204
- [ ] i18n requests return appropriate language

## 📚 Additional Resources

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Documentation:** See README.md
- **Sample Data Guide:** See SAMPLE_DATA_TESTING_GUIDE.md
- **Quick Start:** See QUICK_START_GUIDE.md

