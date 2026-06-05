# Project Structure — customerService

## Root Layout
```
customerService/
├── src/main/java/com/bhagwat/scm/customerService/
│   ├── command/          # Write side (CQRS)
│   │   ├── commanddto/   # Command objects (AllocateInventory, MakePayment, etc.)
│   │   ├── controller/   # REST controllers for mutations
│   │   ├── entity/       # JPA entities (PostgreSQL)
│   │   ├── events/       # Domain events published to Kafka
│   │   ├── handlers/     # Event/command handlers
│   │   ├── repository/   # Spring Data JPA repositories
│   │   └── service/      # Business logic services
│   ├── query/            # Read side (CQRS)
│   │   ├── controller/   # REST controllers for queries
│   │   ├── dto/          # Query-specific DTOs
│   │   ├── entity/       # MongoDB documents
│   │   ├── repository/   # Spring Data MongoDB repositories
│   │   └── service/      # Query services + Kafka consumer
│   ├── config/           # Spring Security, MongoDB config
│   ├── constant/         # Enums (OrderStatus, PaymentStatus, etc.)
│   ├── dto/              # Shared request/response DTOs
│   ├── rest/             # HTTP clients to external services
│   │   ├── impl/         # API interface implementations
│   │   ├── CatalogServiceClient.java
│   │   ├── CartServiceClient.java
│   │   ├── CommunityManagerClient.java
│   │   ├── CommunityApi.java
│   │   └── InventoryApi.java
│   ├── saga/             # Saga orchestration (order fulfillment)
│   └── CustomerServiceApplication.java
├── src/main/resources/
│   ├── application.yml   # Primary config (port 8082, PostgreSQL, MongoDB, Kafka)
│   └── application.properties
├── Dockerfile
├── pom.xml
└── .github/workflows/ci-cd.yml
```

## Core Components & Relationships

### CQRS Split
- **Command side**: JPA + PostgreSQL (`customerdb`) — handles writes, emits Kafka events
- **Query side**: MongoDB (`customerdb`) — `KafkaConsumerService` consumes events and updates `CustomerDocument`

### Saga (Order Fulfillment)
Located in `saga/` — orchestrates: CreateOrder → CreateOrderLine → AllocateInventory → MakePayment → AssignCarrier → CreateShipment. Compensating events exist for each step (e.g., `InventoryAllocationFailedEvent`, `PaymentFailedEvent`).

### REST Clients (`rest/`)
Thin HTTP clients using shared `ApiClient` + `ApiConfig` from `api-rest` library. Each client wraps a specific downstream service:
- `CatalogServiceClient` → catalog-service (`:8089`)
- `CommunityManagerClient` → community-manager (`:8081`)
- `CartServiceClient` → cart-service (`:8091`)
- `InventoryApi` / `CommunityApi` → interface + impl pattern

### Shared Libraries (internal Maven artifacts)
| Artifact | Purpose |
|---|---|
| `api-rest` | `ApiClient`, `ApiConfig` for HTTP calls |
| `api-kafka` | Kafka producer/consumer abstractions |
| `api-db` | Shared DB utilities |
| `api-observability` | Tracing (Tempo), metrics (Prometheus), logs (Loki) |
| `inventory-dto` | Shared DTOs with inventory-service |

## Architectural Patterns
- **CQRS**: Strict command/query separation with dual databases
- **Event-Driven**: Kafka as the event bus between command and query sides
- **Saga Pattern**: Distributed transaction orchestration for order placement
- **Service Client Pattern**: Dedicated client classes per downstream service
- **API Gateway + Eureka**: All traffic routed through gateway; service discovery via Eureka
