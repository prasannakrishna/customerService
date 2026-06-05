# Development Guidelines — customerService

## Code Quality Standards

### Naming Conventions
- Classes: PascalCase — `CustomerProfileController`, `SubscriptionService`, `CartService`
- Methods: camelCase — `addOrUpdateItem`, `computeHoldUntil`, `toResponse`
- Constants (static final Strings): UPPER_SNAKE_CASE — `ALL_PRODUCTS`, `SEARCH_PRODUCTS`
- Private helpers: prefixed with intent — `toDto`, `toEntity`, `toResponse`, `toProfileMap`, `findCustomer`
- UUIDs used as all entity/aggregate identifiers (never Long/int IDs)

### Package Structure Convention
```
command/
  controller/   ← REST write endpoints
  service/      ← business logic
  entity/       ← JPA entities
  repository/   ← Spring Data JPA repos
  events/       ← Kafka domain events
  commanddto/   ← command objects
query/
  controller/   ← REST read endpoints
  service/      ← query logic + Kafka consumer
  entity/       ← MongoDB documents
  repository/   ← Spring Data MongoDB repos
```

## Annotations — Frequently Used

### Class-level
```java
@RestController
@RequestMapping("/api/<resource>")
@RequiredArgsConstructor          // Lombok — constructor injection
@Slf4j                            // Lombok — log field
@Service
@Configuration
@EnableWebSecurity
@Component
```

### Method-level
```java
@GetMapping("/{id}")
@PostMapping
@PutMapping("/{id}")
@PatchMapping("/{id}")
@DeleteMapping("/{id}")
@Transactional                    // jakarta.transaction.Transactional in services
@Transactional                    // org.springframework.transaction.annotation.Transactional in controllers
@Bean
```

### Parameter-level
```java
@PathVariable UUID customerId
@RequestBody SomeDto dto
@RequestBody Map<String, Object> updates   // used for PATCH partial updates
@Value("${property.key:default}")          // config injection in constructors
```

## Dependency Injection Pattern
- Prefer **constructor injection** (explicit or via `@RequiredArgsConstructor`)
- Controllers with `@RequiredArgsConstructor` + `private final` fields
- Services with explicit constructor (no Lombok) — both patterns coexist:
```java
// Controller style (Lombok)
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
}

// Service style (explicit)
public class CartService {
    private final ShoppingBagRepository bagRepository;
    public CartService(ShoppingBagRepository bagRepository) {
        this.bagRepository = bagRepository;
    }
}
```

## Controller Patterns

### Return Types
- Always return `ResponseEntity<T>` — never raw objects
- Use `ResponseEntity.ok(payload)` for success
- Use `Map<String, Object>` for flexible/ad-hoc responses (profile, delete confirmations)
- Use typed DTOs for structured responses (`CartResponse`, `AddressDto`, `ProductSubscriptionResponse`)

### PATCH Pattern (partial update)
```java
@PatchMapping("/{id}")
@Transactional
public ResponseEntity<Map<String, Object>> updateProfile(
        @PathVariable UUID id,
        @RequestBody Map<String, Object> updates) {
    Entity e = findEntity(id);
    if (updates.containsKey("field")) e.setField((String) updates.get("field"));
    repository.save(e);
    return ResponseEntity.ok(toMap(e));
}
```

### Section Comments in Controllers
Use ASCII-art section dividers for logical grouping:
```java
// ── Profile ─────────────────────────────────────────────────────────
// ── Addresses ───────────────────────────────────────────────────────
// ── Helpers ──────────────────────────────────────────────────────────
```

### Javadoc on Endpoints
Brief `/** ... */` Javadoc on each endpoint method describing behavior and edge cases.

## Service Patterns

### Entity Lookup Helper
Always extract entity lookup into a private method throwing `RuntimeException`:
```java
private Entity findEntity(UUID id) {
    return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entity not found: " + id));
}
```

### State Guard Pattern
Validate state before mutation, throw `IllegalStateException` for invalid transitions:
```java
if (sub.getStatus() == SubscriptionStatus.HOLD) {
    throw new IllegalStateException("Already on hold until " + sub.getHoldUntil());
}
```

### Mapper Methods (toResponse / toDto / toEntity)
- Always use private `toResponse(Entity e)` / `toDto(Entity e)` / `toEntity(Dto dto)` methods
- Manual field-by-field mapping (no MapStruct) — explicit and readable
- Mapper methods placed at the bottom of the class under `// ── Mapper ──` section comment

### Timestamp Handling
- Use `Instant.now()` for all timestamps (UTC)
- Use `ZonedDateTime` with `ZoneOffset.UTC` for date arithmetic
- Use `java.time.temporal.TemporalAdjusters` for end-of-period calculations

### getOrCreate Pattern
```java
private ShoppingBag getOrCreateActiveBag(UUID customerId) {
    return repository.findByUserIdAndStatus(customerId, Status.ACTIVE)
            .orElseGet(() -> {
                Entity e = new Entity();
                // set defaults
                return repository.save(e);
            });
}
```

## REST Client Pattern (`rest/` package)
```java
@Component
@Slf4j
public class XxxServiceClient {
    private static final String ENDPOINT = "/api/xxx/resource";
    private final ApiClient apiClient;
    private final String baseUrl;

    public XxxServiceClient(ApiClient apiClient,
                            @Value("${xxx.service.base-url:http://localhost:PORT}") String baseUrl) { ... }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String path, String errorMsg) {
        try {
            ResponseEntity<Map> response = apiClient.invoke(config(path, HttpMethod.GET), Map.class);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("{}: {}", errorMsg, e.getMessage());
            return Collections.emptyMap();  // graceful degradation — never throw
        }
    }

    private ApiConfig config(String path, HttpMethod method) {
        ApiConfig c = new ApiConfig();
        c.setHost(baseUrl);
        c.setApiPath(path);
        c.setHttpMethod(method);
        return c;
    }
}
```
Key rules:
- Base URL injected via `@Value` with localhost default
- Errors logged with `log.error("{}: {}", errorMsg, e.getMessage())` — never propagated
- Return `Collections.emptyMap()` on failure (graceful degradation)
- URL params built via string concatenation with `?param=value&param2=value2`

## Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```
- CSRF disabled (stateless microservice behind API Gateway)
- All requests permitted (auth delegated to API Gateway)
- CORS: wildcard origins with credentials, all standard HTTP methods

## Logging
- Use `@Slf4j` (Lombok) — only on controllers, not services
- Log significant state changes: `log.info("Address added for customer {}: {}", id, savedId)`
- Log errors in REST clients: `log.error("{}: {}", errorMsg, e.getMessage())`
- No debug logging in business logic

## Kafka Events
- Event classes in `command/events/` package
- Trusted package configured: `com.bhagwat.scm.customerService.command.events`
- Consumer group: `customer-service-group`
- `KafkaConsumerService` in `query/service/` consumes events to update MongoDB read model

## Enums / Constants
- Status enums in `constant/` package: `CustomerOrderStatus`, `PaymentStatus`, `ShoppingBagStatus`, `SubscriptionStatus`
- Used directly in entities and service state guards

## Testing
- Minimal: only `@SpringBootTest` context load test exists
- No unit tests for services or controllers currently
- New tests should use `@SpringBootTest` + JUnit 5 (`@Test`)
