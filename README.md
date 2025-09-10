# Product Service

> **🟢 This service is highlighted in the architecture diagram below**

Product catalog and inventory management service for the e-commerce microservices ecosystem.

## Service Role: Producer Only
This service provides product catalog and inventory data to other services but does not consume external APIs.

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │🟢Product Service│
│   (Port 8081)   │    │   (Port 8082)   │
│                 │    │                 │
│ • Authentication│    │ • Product Catalog│
│ • User Profiles │    │ • Inventory Mgmt│
│ • JWT Tokens    │    │ • Pricing       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          │ validates users      │ fetches products
          │                      │
          ▼                      ▼
    ┌─────────────────────────────────┐
    │        Order Service            │
    │        (Port 8083)              │
    │                                 │
    │ • Order Management              │
    │ • Order Processing              │
    │ • Consumes User & Product APIs  │
    └─────────────┬───────────────────┘
                  │
                  │ triggers payment
                  │
                  ▼
    ┌─────────────────────────────────┐
    │       Payment Service           │
    │       (Port 8084)               │
    │                                 │
    │ • Payment Processing            │
    │ • Gateway Integration           │
    │ • Refund Management             │
    └─────────────┬───────────────────┘
                  │
                  │ sends notifications
                  │
                  ▼
    ┌─────────────────────────────────┐
    │    Notification Service         │
    │       (Port 8085)               │
    │                                 │
    │ • Email Notifications           │
    │ • SMS Notifications             │
    │ • Order & Payment Updates       │
    └─────────────────────────────────┘
                  │ All services send telemetry data
                  │
                  ▼
    ┌─────────────────────────────────┐
    │📊  Telemetry Service            │
    │       (Port 8086)               │
    │                                 │
    │ • Distributed Tracing           │
    │ • Service Metrics               │
    │ • Request Tracking              │
    │ • Performance Monitoring        │
    └─────────────────────────────────┘
```

## Features

- **Product Catalog**: Complete product information management
- **Inventory Management**: Real-time stock quantity tracking
- **Category Management**: Product categorization system
- **Search & Filtering**: Find products by category and name
- **Stock Updates**: Inventory level management
- **Pricing Management**: Product pricing with validation

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Validation**: Spring Validation
- **Java Version**: 17

## API Endpoints

### Product Management
- `POST /api/products` - Create new product
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Inventory Management
- `PUT /api/products/{id}/stock` - Update product stock quantity

### Search & Filter
- `GET /api/products/category/{category}` - Get products by category

## Telemetry Integration

The Product Service sends comprehensive telemetry data to the Telemetry Service for monitoring and observability:

### Telemetry Features
- **Request Tracing**: All API requests are traced with unique trace IDs
- **Service Metrics**: Performance metrics including response times and throughput
- **Error Tracking**: Automatic error detection and reporting
- **Database Monitoring**: H2 database query performance tracking
- **Custom Business Metrics**: Product catalog and inventory operation metrics

### Traced Operations
- Product CRUD operations (create, read, update, delete)
- Inventory management operations (stock updates)
- Product search and filtering operations
- Database query operations
- Service health checks

### Telemetry Configuration
The service is configured to send telemetry data to the Telemetry Service:
```yaml
telemetry:
  service:
    url: http://localhost:8086
    enabled: true
  tracing:
    sample-rate: 1.0
  metrics:
    enabled: true
    export-interval: 30s
```

## Running the Service

### Prerequisites
- Java 17+
- Gradle (or use included Gradle wrapper)

### Start the Service
```bash
./gradlew bootRun
```

The service will start on **port 8082**.

### Database Access
- **H2 Console**: http://localhost:8082/h2-console
- **JDBC URL**: `jdbc:h2:mem:productdb`
- **Username**: `sa`
- **Password**: (empty)

## Service Dependencies

### Consumers of This Service
- **Order Service**: Fetches product details and pricing during order creation

### External Dependencies
- None (this is a producer-only service)

## Data Model

### Product Entity
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "imageUrl": "https://example.com/laptop.jpg",
  "sku": "LAP-001"
}
```

## Example Usage

### Create a Product
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop with RTX graphics",
    "price": 1299.99,
    "stockQuantity": 25,
    "category": "Electronics",
    "sku": "GAM-LAP-001"
  }'
```

### Get All Products
```bash
curl -X GET http://localhost:8082/api/products
```

### Update Stock
```bash
curl -X PUT http://localhost:8082/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 45}'
```

### Get Products by Category
```bash
curl -X GET http://localhost:8082/api/products/category/Electronics
```

## Pact Contract Testing

This service uses [Pact](https://pact.io/) for consumer contract testing to ensure reliable communication with external services.

### Consumer Role

This service acts as a consumer for the following external services:
- **telemetry-service**: Receives telemetry events and metrics data via POST `/api/telemetry/events`

### Running Pact Tests

#### Consumer Tests
```bash
# Run consumer tests and generate contracts
./gradlew pactTest

# Generated contracts will be in build/pacts/
```

#### Publishing Contracts
```bash
# Publish contracts to Pactflow
./gradlew pactPublish
```

### Contract Testing Approach

This implementation follows Pact's **"Be conservative in what you send"** principle:

- Consumer tests define minimal request structures with only required fields
- Request bodies cannot contain fields not defined in the contract
- Tests validate that actual API calls match contract expectations exactly
- Mock servers reject requests with unexpected extra fields

### Contract Files

Consumer contracts are generated in:
- `build/pacts/` - Local contract files  
- Pactflow - Centralized contract storage and management

### Troubleshooting

#### Common Issues

1. **Consumer Test Failures**
   - **Extra fields in request**: Remove any fields from request body that aren't actually needed
   - **Mock server expectation mismatch**: Verify HTTP method, path, headers, and body structure
   - **Content-Type headers**: Ensure request headers match exactly what the service sends
   - **URL path parameters**: Check that path parameters are correctly formatted in the contract

2. **Contract Generation Issues**
   - **Missing @Pact annotation**: Ensure each contract method has proper annotations
   - **Invalid JSON structure**: Verify LambdaDsl body definitions match actual data structures
   - **Provider state setup**: Ensure provider state descriptions are descriptive and specific

3. **Pactflow Integration Issues**
   - **Authentication**: Verify `PACT_BROKER_TOKEN` environment variable is set
   - **Base URL**: Confirm `PACT_BROKER_BASE_URL` points to `https://pairgood.pactflow.io`
   - **Network connectivity**: Check firewall/proxy settings if publishing fails

#### Debug Commands

```bash
# Run with debug output
./gradlew pactTest --info --debug

# Run specific test class
./gradlew pactTest --tests="*TelemetryServicePactTest*"

# Generate contracts without publishing
./gradlew pactTest -x pactPublish

# Clean and regenerate contracts
./gradlew clean pactTest
```

#### Debug Logging

Add to `application-test.properties` for detailed Pact logging:
```properties
logging.level.au.com.dius.pact=DEBUG
logging.level.org.apache.http=DEBUG
```

### Contract Evolution

When external services change their APIs:

1. **New Fields in Responses**: No action needed - consumers ignore extra fields
2. **Removed Response Fields**: Update consumer tests if those fields were being used
3. **New Required Request Fields**: Update consumer tests and service code
4. **Changed Endpoints**: Update consumer contract paths and service client code

### Integration with CI/CD

Consumer contract tests run automatically on:
- **Pull Requests**: Generate and validate contracts
- **Main Branch**: Publish contracts to Pactflow for provider verification
- **Feature Branches**: Generate contracts for validation (not published)

### Manual Testing

For local development against real services:
```bash
# Test against local services (disable Pact)
./gradlew test -Dpact.verifier.disabled=true

# Test against staging services
export TELEMETRY_SERVICE_URL=https://staging.telemetry.example.com
./gradlew test -Dpact.verifier.disabled=true
```

### Contract Documentation

Generated contracts document:
- **API interactions**: What endpoints this service calls
- **Request formats**: Exact structure of requests sent
- **Response expectations**: What fields this service relies on
- **Error handling**: How this service handles different response scenarios

### Implementation Notes

**Current Status**: Consumer test infrastructure is in place with Pact dependencies and configuration. The telemetry service contract test (`TelemetryServicePactTest.java`) is implemented but requires API compatibility adjustments for Pact 4.6.4. 

The test follows the correct pattern:
- Uses reflection to inject mock server URLs into TelemetryClient
- Defines minimal request body structures with only fields actually sent
- Tests real client behavior against mock Pact server

**Next Steps**: 
- Resolve Pact 4.6.4 method signature compatibility issues
- Verify contract generation in `build/pacts/` directory
- Enable automatic contract publishing on main branch builds

## Related Services

- **[Order Service](../order-service/README.md)**: Consumes this service for product details
- **[User Service](../user-service/README.md)**: Independent service
- **[Payment Service](../payment-service/README.md)**: Independent service  
- **[Notification Service](../notification-service/README.md)**: Independent service
- **[Telemetry Service](../telemetry-service/README.md)**: Collects telemetry data from this service