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

## Related Services

- **[Order Service](../order-service/README.md)**: Consumes this service for product details
- **[User Service](../user-service/README.md)**: Independent service
- **[Payment Service](../payment-service/README.md)**: Independent service  
- **[Notification Service](../notification-service/README.md)**: Independent service
- **[Telemetry Service](../telemetry-service/README.md)**: Collects telemetry data from this service