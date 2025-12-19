# GraphQL API Guide - Payment Echo System

## Overview

The Payment Echo System provides a GraphQL API alongside the REST API, allowing flexible querying and mutations for payments, creditors, and debtors.

## GraphQL Endpoint

- **GraphQL Endpoint**: `http://localhost:8080/graphql`
- **GraphiQL UI**: `http://localhost:8080/graphiql` (Interactive query explorer)

## Getting Started

### 1. Access GraphiQL UI

Open your browser and navigate to:
```
http://localhost:8080/graphiql
```

This provides an interactive interface to explore and test GraphQL queries and mutations.

### 2. Basic Query Examples

#### Get All Payments

```graphql
query {
  payments(page: 0, size: 20) {
    payments {
      id
      amount
      currency
      status
      createdAt
      creditorId
      debtorId
    }
    total
    page
    size
    totalPages
  }
}
```

#### Get Payment by ID

```graphql
query {
  payment(id: "e9c5b9de-45c0-407c-a376-52c92721ce79") {
    id
    amount
    currency
    status
    createdAt
    creditorId
    debtorId
  }
}
```

**Note**: Replace the ID with an actual payment ID from your database. Get IDs using:
```bash
curl http://localhost:8080/api/v1/payments | jq '.payments[0].id'
```

#### Filter Payments by Status

```graphql
query {
  paymentsByStatus(status: "RECEIVED", page: 0, size: 20) {
    payments {
      id
      amount
      currency
      status
    }
    total
  }
}
```

#### Filter Payments by Currency

```graphql
query {
  paymentsByCurrency(currency: "USD", page: 0, size: 20) {
    payments {
      id
      amount
      currency
      status
    }
    total
  }
}
```

#### Filter Payments by Amount Range

```graphql
query {
  paymentsByAmountRange(minAmount: 100.0, maxAmount: 2000.0, page: 0, size: 20) {
    payments {
      id
      amount
      currency
      status
    }
    total
  }
}
```

### 3. Mutation Examples

#### Create Payment (Standalone)

```graphql
mutation {
  createPayment(input: {
    amount: 1000.00
    currency: "USD"
    status: "RECEIVED"
  }) {
    id
    amount
    currency
    status
    createdAt
  }
}
```

#### Create Payment (With Creditor and Debtor)

```graphql
mutation {
  createPayment(input: {
    amount: 1500.00
    currency: "USD"
    status: "RECEIVED"
    creditorId: "d4c10bbc-5f9d-400a-b527-904dfa02452a"
    debtorId: "02d8b939-b2a9-445c-9fcf-e94ad8a77b15"
  }) {
    id
    amount
    currency
    status
    creditorId
    debtorId
    createdAt
  }
}
```

**Note**: Replace `creditorId` and `debtorId` with actual IDs from your database:
```bash
# Get Creditor ID
curl http://localhost:8080/api/v1/creditors | jq '.creditors[0].id'

# Get Debtor ID
curl http://localhost:8080/api/v1/debtors | jq '.debtors[0].id'
```

#### Echo Payment

```graphql
mutation {
  echoPayment(input: {
    amount: 2000.00
    currency: "EUR"
    status: "PROCESSING"
  }) {
    id
    amount
    currency
    status
    createdAt
  }
}
```

## Using cURL

### Query Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query { payments(page: 0, size: 10) { payments { id amount currency status } total } }"
  }'
```

### Mutation Example

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "mutation { createPayment(input: { amount: 1000.00 currency: \"USD\" status: \"RECEIVED\" }) { id amount currency status } }"
  }'
```

## Using Postman

1. **Create a new POST request**
   - URL: `http://localhost:8080/graphql`
   - Method: `POST`
   - Headers:
     - `Content-Type: application/json`

2. **Body (raw JSON)**:
```json
{
  "query": "query { payments(page: 0, size: 10) { payments { id amount currency status } total } }"
}
```

3. **For Mutations**:
```json
{
  "query": "mutation { createPayment(input: { amount: 1000.00 currency: \"USD\" status: \"RECEIVED\" }) { id amount currency status createdAt } }"
}
```

## GraphQL Schema

### Types

#### Payment
```graphql
type Payment {
  id: String!
  amount: Float!
  currency: String!
  status: String!
  createdAt: String!
  creditorId: String
  debtorId: String
}
```

#### PaymentPage
```graphql
type PaymentPage {
  payments: [Payment!]!
  total: Int!
  page: Int!
  size: Int!
  totalPages: Int!
}
```

#### PaymentInput
```graphql
input PaymentInput {
  amount: Float!
  currency: String!
  status: String!
  creditorId: String
  debtorId: String
}
```

#### PaymentEchoInput
```graphql
input PaymentEchoInput {
  amount: Float!
  currency: String!
  status: String!
  creditorId: String
  debtorId: String
}
```

### Queries

- `payments(page: Int, size: Int, sort: String): PaymentPage` - Get all payments with pagination
- `payment(id: String!): PaymentResponse` - Get payment by ID
- `paymentsByStatus(status: String!, page: Int, size: Int): PaymentPage` - Filter by status
- `paymentsByCurrency(currency: String!, page: Int, size: Int): PaymentPage` - Filter by currency
- `paymentsByAmountRange(minAmount: Float!, maxAmount: Float!, page: Int, size: Int): PaymentPage` - Filter by amount range

### Mutations

- `createPayment(input: PaymentInput!): PaymentResponse` - Create a new payment
- `echoPayment(input: PaymentEchoInput!): PaymentResponse` - Echo a payment (returns and saves)

## Status Values

- `RECEIVED` - Payment has been received
- `PROCESSING` - Payment is being processed
- `COMPLETED` - Payment has been completed
- `FAILED` - Payment has failed

## Currency Codes

Use valid 3-letter ISO currency codes:
- `USD` - US Dollar
- `EUR` - Euro
- `GBP` - British Pound
- `JPY` - Japanese Yen
- `CAD` - Canadian Dollar
- `AUD` - Australian Dollar

## Pagination

All list queries support pagination:
- `page`: Page number (0-indexed, default: 0)
- `size`: Page size (default: 20)

Example:
```graphql
query {
  payments(page: 0, size: 10) {
    payments { id amount }
    total
    page
    size
    totalPages
  }
}
```

## Error Handling

GraphQL errors are returned in the standard GraphQL error format:

```json
{
  "errors": [
    {
      "message": "Payment not found with id: ...",
      "extensions": {
        "status": 404
      }
    }
  ],
  "data": null
}
```

## Best Practices

1. **Always specify fields you need** - GraphQL allows you to request only the fields you need, reducing payload size
2. **Use pagination** - For large datasets, always use pagination parameters
3. **Handle errors** - Check for `errors` array in responses
4. **Use variables** - For complex queries, use GraphQL variables instead of string interpolation

### Using Variables

```graphql
query GetPayments($page: Int, $size: Int) {
  payments(page: $page, size: $size) {
    payments { id amount currency }
    total
  }
}
```

Variables:
```json
{
  "page": 0,
  "size": 20
}
```

## Testing with Sample Data

The application includes sample data that you can query:

1. **Get sample payment IDs**:
```bash
curl http://localhost:8080/api/v1/payments | jq '.payments[0:3] | .[] | .id'
```

2. **Use in GraphQL query**:
```graphql
query {
  payment(id: "<PASTE-ID-HERE>") {
    id
    amount
    currency
    status
  }
}
```

## Comparison: REST vs GraphQL

| Feature | REST API | GraphQL API |
|---------|----------|-------------|
| Endpoint | `/api/v1/payments` | `/graphql` |
| Query Flexibility | Fixed endpoints | Flexible queries |
| Over-fetching | Possible | Avoided (request only needed fields) |
| Multiple Resources | Multiple requests | Single request |
| Caching | HTTP caching | Custom caching needed |
| Learning Curve | Lower | Higher |

## Troubleshooting

### Common Issues

1. **"Payment not found"**
   - Ensure you're using a valid UUID from the database
   - Check that the payment exists using REST API first

2. **"Invalid currency"**
   - Use valid 3-letter ISO currency codes (USD, EUR, GBP, etc.)

3. **"Invalid status"**
   - Use one of: RECEIVED, PROCESSING, COMPLETED, FAILED

4. **GraphiQL not loading**
   - Ensure `spring.graphql.graphiql.enabled=true` in `application.properties`
   - Check that the application is running on port 8080

## Additional Resources

- [GraphQL Official Documentation](https://graphql.org/learn/)
- [Spring GraphQL Documentation](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
- REST API Documentation: `http://localhost:8080/swagger-ui.html`

