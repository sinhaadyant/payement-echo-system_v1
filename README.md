# Payment Echo System

A comprehensive Spring Boot application for managing payments, creditors, and debtors with full REST API support.

## Features

- ✅ **RESTful API** - Complete CRUD operations for Payments, Creditors, and Debtors
- ✅ **API Versioning** - All endpoints use `/api/v1/` prefix
- ✅ **Pagination & Filtering** - Advanced filtering and pagination on all list endpoints
- ✅ **Input Validation** - Jakarta Bean Validation with custom validators
- ✅ **DTOs** - Separate Request/Response DTOs for clean API contracts
- ✅ **Exception Handling** - Global exception handler with standardized error responses
- ✅ **Internationalization (i18n)** - Multi-language support via Accept-Language header
- ✅ **Soft Delete** - Records are marked as deleted, not physically removed
- ✅ **Logging** - AOP-based logging, request/response logging with sensitive data masking
- ✅ **API Documentation** - OpenAPI/Swagger UI integration with comprehensive examples
- ✅ **GraphQL API** - Complete GraphQL API alongside REST API with GraphiQL playground
- ✅ **Health Monitoring** - Spring Boot Actuator endpoints with detailed health checks
- ✅ **Hot Reload** - Spring Boot DevTools for automatic restart and live reload
- ✅ **Comprehensive Tests** - 92 tests covering unit, integration, edge cases, and end-to-end scenarios
- ✅ **Consistent API Responses** - Standardized response format with request ID tracking

## Tech Stack

- **Language**: Kotlin 2.0.21
- **Framework**: Spring Boot 3.3.5
- **Database**: MySQL 8.0 (H2 for testing)
- **Build Tool**: Gradle
- **API Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, MockMvc

## Quick Start

### Prerequisites

- Java 17 or higher
- Gradle (or use Gradle Wrapper)

### Running the Application

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Hot Reload

The application includes Spring Boot DevTools for automatic restart and live reload:

1. Run the application with `./gradlew bootRun`
2. Make changes to Kotlin files in `src/main/kotlin/`
3. The application will automatically restart
4. For browser live reload, install a LiveReload extension

See [HOT_RELOAD_GUIDE.md](HOT_RELOAD_GUIDE.md) for detailed instructions.

## API Endpoints

### REST API

All REST endpoints are versioned under `/api/v1/`

### Payments

- `GET /api/v1/payments` - Get all payments (with pagination and filtering)
  - Query params: `page`, `size`, `sort`, `status`, `currency`, `minAmount`, `maxAmount`, `startDate`, `endDate`
- `GET /api/v1/payments/{id}` - Get payment by ID
- `POST /api/v1/payments` - Create a new payment
- `POST /api/v1/payments/echo` - Echo a payment (returns and saves)

### Creditors

- `GET /api/v1/creditors` - Get all creditors (with pagination and filtering)
  - Query params: `page`, `size`, `sort`, `name`, `bankCode`
- `GET /api/v1/creditors/{id}` - Get creditor by ID
- `POST /api/v1/creditors` - Create a new creditor
- `DELETE /api/v1/creditors/{id}` - Soft delete a creditor

### Debtors

- `GET /api/v1/debtors` - Get all debtors (with pagination and filtering)
  - Query params: `page`, `size`, `sort`, `name`, `bankCode`
- `GET /api/v1/debtors/{id}` - Get debtor by ID
- `POST /api/v1/debtors` - Create a new debtor
- `DELETE /api/v1/debtors/{id}` - Soft delete a debtor

### Monitoring

- `GET /actuator/health` - Health check
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Documentation

- `GET /swagger-ui.html` - Swagger UI (Interactive REST API documentation)
- `GET /v3/api-docs` - OpenAPI JSON specification
- `GET /graphiql` - GraphiQL (Interactive GraphQL playground)
- `POST /graphql` - GraphQL endpoint

## API Response Format

All list endpoints return a consistent format:

```json
{
  "total": 10,
  "payments": [...],  // or "creditors" or "debtors"
}
```

Single resource endpoints return the resource directly:

```json
{
  "id": "uuid",
  "amount": 100.50,
  "currency": "USD",
  ...
}
```

Error responses follow a standardized format:

```json
{
  "timestamp": "2025-12-10T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/payments",
  "fieldErrors": {
    "amount": "Amount must be greater than 0"
  }
}
```

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test Class

```bash
./gradlew test --tests PaymentServiceTest
```

### Test Coverage

The project includes **92 comprehensive tests**:

- ✅ Repository tests (CRUD operations)
- ✅ Service tests (business logic, filtering)
- ✅ Controller tests (MockMvc, request/response validation)
- ✅ Integration tests (end-to-end flows)
- ✅ Edge case tests (boundary conditions, error handling)
- ✅ API response format tests
- ✅ Filter functionality tests

**Current Status**: ✅ All 92 tests passing

## API Testing

### Using Postman

Import the Postman collection from `postman/PaymentEchoSystem.postman_collection.json`

The collection includes:

- All CRUD operations
- Filtering examples
- Pagination examples
- Validation error examples
- Error handling examples

### Using Swagger UI

1. Start the application: `./gradlew bootRun`
2. Navigate to: `http://localhost:8080/swagger-ui.html`
3. Explore and test all endpoints interactively

### Using curl

See [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md) for detailed curl examples.

### Filtering Examples

```bash
# Filter payments by status
GET /api/v1/payments?status=RECEIVED

# Filter payments by currency and amount range
GET /api/v1/payments?currency=USD&minAmount=100&maxAmount=1000

# Filter creditors by name (partial match, case-insensitive)
GET /api/v1/creditors?name=John

# Pagination with sorting
GET /api/v1/payments?page=0&size=20&sort=amount,desc
```

## Project Structure

```
src/
├── main/
│   ├── kotlin/com/example/paymentecho/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── request/    # Request DTOs
│   │   │   └── response/    # Response DTOs
│   │   ├── mapper/         # DTO mappers
│   │   ├── exception/      # Custom exceptions
│   │   ├── aspect/         # AOP aspects
│   │   └── config/         # Configuration classes
│   └── resources/
│       └── application.properties
└── test/
    └── kotlin/com/example/paymentecho/
        ├── repository/      # Repository tests
        ├── service/         # Service tests
        ├── controller/      # Controller tests
        └── integration/     # Integration tests
```

## Configuration

Key configuration files:

- `build.gradle.kts` - Build configuration and dependencies
- `src/main/resources/application.properties` - Application configuration
- `src/test/resources/application-test.properties` - Test configuration

## Development

### Hot Reload Setup

1. **IntelliJ IDEA**:

   - Enable "Build project automatically" in Settings
   - Enable `compiler.automake.allow.when.app.running` in Registry

2. **VS Code**:

   - Install Spring Boot Extension Pack
   - Enable auto-save

3. **Eclipse**:
   - Enable "Build Automatically" in Project menu

See [HOT_RELOAD_GUIDE.md](HOT_RELOAD_GUIDE.md) for detailed setup instructions.

## Database

The application uses **MySQL** database. 

### Setup MySQL

#### Option 1: Using Docker Compose (Recommended)

```bash
# Start MySQL and application
docker-compose up -d

# Or start only MySQL
docker-compose up -d mysql
```

#### Option 2: Local MySQL Installation

1. Install MySQL 8.0 or higher
2. Create database:
   ```sql
   CREATE DATABASE payment_echo_db;
   ```
3. Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/payment_echo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Default Configuration

- **Host**: `localhost:3306`
- **Database**: `payment_echo_db`
- **Username**: `root` (default, change in production)
- **Password**: `root` (default, change in production)

### Database Management

- The database schema will be automatically created/updated on startup (`spring.jpa.hibernate.ddl-auto=update`)
- Use MySQL Workbench, DBeaver, or any MySQL client to manage the database
- Connection string: `jdbc:mysql://localhost:3306/payment_echo_db`

**Note**: For testing, the application still uses H2 in-memory database (configured in `application-test.properties`).

## Validation Rules

### Payment

- `amount`: Must be between 0.01 and 999,999,999.99
- `currency`: Must be a valid 3-letter ISO code (e.g., USD, EUR)
- `status`: Must be one of: RECEIVED, PROCESSING, COMPLETED, FAILED

### Creditor/Debtor

- `name`: Required, not blank
- `accountNumber`: Required, not blank
- `bankCode`: Required, not blank
- `email`: Optional, must be valid email format if provided

## Error Handling

The application includes comprehensive error handling:

- **400 Bad Request**: Validation errors with field-level details
- **404 Not Found**: Resource not found with clear messages (supports i18n)
- **500 Internal Server Error**: Generic error handler for unexpected errors

All errors follow a consistent format with timestamp, status, error type, message, and path.

### Internationalization (i18n)

Error messages and validation errors support multiple languages via the `Accept-Language` header:

```bash
# Hindi (default)
curl http://localhost:8080/api/v1/payments/123

# English
curl -H "Accept-Language: en" http://localhost:8080/api/v1/payments/123

# Tamil
curl -H "Accept-Language: ta" http://localhost:8080/api/v1/payments/123

# Russian
curl -H "Accept-Language: ru" http://localhost:8080/api/v1/payments/123

# Bengali
curl -H "Accept-Language: bn" http://localhost:8080/api/v1/payments/123
```

**Supported Languages**: Hindi (hi - default), English (en), Spanish (es), French (fr), German (de), Bengali (bn), Tamil (ta), Telugu (te), Kannada (kn), Russian (ru), Chinese (zh)

**Default Language**: Hindi (hi) - All API endpoints default to Hindi if `Accept-Language` header is not provided.

**Features**:

- ✅ Localized error messages (404, 400, 500)
- ✅ Localized validation field errors
- ✅ Works for all REST and GraphQL APIs
- ✅ Postman collection includes language dropdown variable

## Logging

The application uses SLF4J with Logback:

- **Request/Response Logging**: All HTTP requests and responses are logged
- **Sensitive Data Masking**: Passwords, emails, account numbers are automatically masked
- **Request Correlation**: Each request gets a unique `X-Request-ID` header
- **Performance Tracking**: Response times tracked via `X-Response-Time` header
- **AOP Logging**: Method entry/exit logging via AOP
- **Performance Monitoring**: Slow method detection and logging
- **Service-level Debug Logging**: Detailed service method logging
- **Error Logging**: Comprehensive error logging with stack traces

## Contributing

1. Follow Kotlin coding conventions
2. Write tests for new features
3. Update API documentation
4. Ensure all tests pass before submitting

## License

This project is licensed under the Apache 2.0 License.

## Support

For issues and questions, please refer to:

- [API Testing Guide](API_TESTING_GUIDE.md)
- [Hot Reload Guide](HOT_RELOAD_GUIDE.md)
- [Implementation Plan](IMPLEMENTATION_PLAN.md)
