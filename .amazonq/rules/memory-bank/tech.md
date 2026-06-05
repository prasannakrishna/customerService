# Technology Stack — customerService

## Core
| Technology | Version | Role |
|---|---|---|
| Java | 17 | Primary language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Cloud | 2023.0.1 | Cloud-native features |
| Maven | (wrapper included) | Build tool |

## Frameworks & Libraries
| Library | Version | Purpose |
|---|---|---|
| Spring Web | Boot-managed | REST API |
| Spring Data JPA | Boot-managed | PostgreSQL ORM (command side) |
| Spring Data MongoDB | Boot-managed | MongoDB ODM (query side) |
| Spring Security | Boot-managed | Authentication, BCrypt |
| Spring Kafka | Boot-managed | Kafka producer/consumer |
| Spring Cloud Netflix Eureka Client | Cloud-managed | Service discovery |
| Spring Cloud Config Client | Cloud-managed | Centralized config |
| springdoc-openapi-starter-webmvc-ui | 2.5.0 | Swagger/OpenAPI UI |
| Lombok | 1.18.30 | Boilerplate reduction |
| Jackson Databind | Boot-managed | JSON serialization |
| Jakarta Validation API | Boot-managed | Bean validation |
| Jakarta Persistence API | 3.1.0 | JPA annotations |

## Databases
| Database | Usage |
|---|---|
| PostgreSQL 42.7.3 | Command side — JPA entities, port 5432, db: `customerdb` |
| MongoDB | Query side — documents, port 27017, db: `customerdb` |

## Messaging
- **Kafka** (localhost:9092) — event bus between command and query sides
  - Consumer group: `customer-service-group`
  - Trusted packages: `com.bhagwat.scm.customerService.command.events`
  - Value serializer/deserializer: `JsonSerializer` / `JsonDeserializer`

## Internal Shared Libraries (Maven)
| Artifact | Version |
|---|---|
| `com.bhagwat.scm:api-rest` | 1.0.0-SNAPSHOT |
| `com.bhagwat.scm:api-kafka` | 1.0.0-SNAPSHOT |
| `com.bhagwat.scm:api-db` | 1.0.0-SNAPSHOT |
| `com.bhagwat.scm:api-observability` | 1.0.0-SNAPSHOT |
| `com.bhagwat.scm:inventory-dto` | 1.0.0 |

## Observability
- Distributed tracing → Grafana Tempo
- Metrics → Prometheus
- Logs → Loki
- Provided via `api-observability` shared library

## Containerization & CI/CD
- **Docker**: `eclipse-temurin:17-jre` base image; copies `target/*.jar` as `app.jar`
- **GitHub Actions**: CI on push/PR to `master` — `mvn clean install` + `mvn test`

## Service Configuration
- Port: **8082**
- App name: `customerservice` (Eureka registration)
- Multitenancy: disabled by default
- External service URLs (configurable via properties):
  - Catalog: `http://localhost:8089`
  - Community Manager: `http://localhost:8081`
  - Cart: `http://localhost:8091`

## Development Commands
```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run

# Test
./mvnw test

# Package (skip tests)
./mvnw clean package -DskipTests

# Docker build
docker build -t customer-service .
```
