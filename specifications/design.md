# Design Document

## Domain Model

```
┌─────────────┐       1:1        ┌─────────────┐
│    User     │─────────────────►│   Wallet    │
├─────────────┤                  ├─────────────┤
│ id          │                  │ id          │
│ email       │                  │ address (hex)│
│ name        │                  │ balance     │
│ password    │                  │ userId      │
│ bank        │                  │ createdAt   │
│ accountId   │                  │ version ⚡  │
│ address     │                  └──────┬──────┘
│ role        │                         │
└─────────────┘                         │ 1:N
                                        ▼
                                ┌─────────────┐
                                │ Transaction │
                                ├─────────────┤
                                │ id          │
                                │ fromWallet  │
                                │ toWallet    │
                                │ amount      │
                                │ status      │
                                │ timestamp   │
                                │ idempotencyKey│
                                └─────────────┘
```

---

## Entity Definitions

### User
```java
@Entity
public class User {
    Long id;
    String email;        // unique
    String name;
    String password;     // BCrypt hashed
    String bank;
    String accountId;
    String address;
    Role role;           // USER, ADMIN
    LocalDateTime createdAt;
}
```

### Wallet
```java
@Entity
public class Wallet {
    Long id;
    String address;      // 16-char hex, unique
    BigDecimal balance;
    Long userId;         // FK to User
    LocalDateTime createdAt;
    
    @Version             // Optimistic locking for high throughput
    Long version;
}
```

### Transaction
```java
@Entity
public class Transaction {
    Long id;
    String fromWalletAddress;
    String toWalletAddress;
    BigDecimal amount;
    TransactionStatus status;  // PENDING, SUCCESS, FAILED
    LocalDateTime timestamp;
    String idempotencyKey;     // For retry safety
}
```

---

## Consistency & Throughput Design

### Strong Consistency Strategy
| Concern | Solution |
|---------|----------|
| Balance integrity | Pessimistic locking on wallet during transfer |
| Concurrent updates | `@Version` optimistic locking |
| Transaction atomicity | `@Transactional(isolation = SERIALIZABLE)` |
| Double-spend prevention | Check balance within same DB transaction |

### High Throughput Strategy
| Concern | Solution |
|---------|----------|
| Connection pooling | HikariCP (max 50 connections) |
| Read scaling | Separate read queries (no locks) |
| Write batching | Bulk inserts for audit logs |
| Retry handling | Idempotency keys on transactions |

### Database Configuration
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
  jpa:
    properties:
      hibernate:
        jdbc.batch_size: 25
        order_inserts: true
```

---

## Observability Design (All Layers)

### Layer Instrumentation
```
┌─────────────────────────────────────────────────────────────┐
│                      API Layer                               │
│  📊 Metrics: request_count, latency_ms, error_rate          │
│  📝 Logs: request/response, validation errors               │
│  🔗 Traces: HTTP span with traceId                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│  📊 Metrics: method_duration, call_count, exception_rate    │
│  📝 Logs: business events, wallet ops, transfers            │
│  🔗 Traces: service method spans                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Database Layer                             │
│  📊 Metrics: query_time, pool_active, pool_pending          │
│  📝 Logs: slow queries, errors, deadlocks                   │
│  🔗 Traces: SQL spans, transaction boundaries               │
└─────────────────────────────────────────────────────────────┘
```

### Metrics to Expose

#### API Metrics
```java
// Auto-instrumented by Spring Boot Actuator
http_server_requests_seconds_count
http_server_requests_seconds_sum
http_server_requests_seconds_max
```

#### Service Metrics (Custom)
```java
@Timed("wallet.deposit")
@Timed("wallet.withdraw")
@Timed("transaction.transfer")
@Counted("transaction.success")
@Counted("transaction.failure")
```

#### Database Metrics
```java
// HikariCP auto-instrumented
hikaricp_connections_active
hikaricp_connections_pending
hikaricp_connections_timeout_total
// JPA
jpa_query_duration_seconds
```

### Logging Strategy

#### Structured Log Format
```json
{
  "timestamp": "2026-02-03T22:00:00.000Z",
  "level": "INFO",
  "logger": "TransactionService",
  "message": "Transfer completed",
  "traceId": "abc123",
  "spanId": "def456",
  "userId": 42,
  "fromWallet": "a1b2c3d4e5f67890",
  "toWallet": "1234567890abcdef",
  "amount": 100.00,
  "status": "SUCCESS"
}
```

### Trace Propagation
```
Request → Controller → Service → Repository → DB
   │          │           │           │        │
   └──────────┴───────────┴───────────┴────────┘
              All share same traceId
```

## Transaction Flow (Consistent & Safe)

```
1. Receive transfer request (amount, toWalletAddress, idempotencyKey)
2. Check idempotency - return existing result if duplicate
3. BEGIN TRANSACTION (SERIALIZABLE)
4. SELECT sender wallet FOR UPDATE (pessimistic lock)
5. Validate balance >= amount
6. Deduct from sender: UPDATE wallet SET balance = balance - amount
7. Add to receiver: UPDATE wallet SET balance = balance + amount
8. INSERT transaction record (SUCCESS)
9. COMMIT
10. On conflict/error → ROLLBACK, record FAILED
```

---

## API Design

### Authentication
```
POST /api/v1/auth/login    → JWT token
POST /api/v1/auth/register → User + Wallet created
```

### User Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/users/me` | USER | Own profile |
| PUT | `/api/v1/users/me` | USER | Update self |
| DELETE | `/api/v1/users/me` | USER | Delete self |

### Admin Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/admin/users` | ADMIN | List all |
| GET | `/api/v1/admin/users/{id}` | ADMIN | Get user |
| POST | `/api/v1/admin/users` | ADMIN | Create user |
| PUT | `/api/v1/admin/users/{id}` | ADMIN | Update user |
| DELETE | `/api/v1/admin/users/{id}` | ADMIN | Delete user |

### Wallet Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/v1/wallet` | USER | View balance |
| POST | `/api/v1/wallet/deposit` | USER | Add money |
| POST | `/api/v1/wallet/withdraw` | USER | Withdraw |

### Transaction Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/v1/transactions` | USER | Transfer money |
| GET | `/api/v1/transactions` | USER | List history |
| GET | `/api/v1/transactions/{id}` | USER | Get details |

---

## Package Structure

```
com.bootstrap.workshop/
├── config/
│   ├── SecurityConfig.java
│   └── HikariConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AdminController.java
│   ├── WalletController.java
│   └── TransactionController.java
├── service/
│   ├── UserService.java
│   ├── WalletService.java
│   └── TransactionService.java
├── repository/
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   └── TransactionRepository.java
├── entity/
│   ├── User.java
│   ├── Wallet.java
│   └── Transaction.java
├── dto/
│   ├── UserDTO.java
│   ├── WalletDTO.java
│   └── TransactionDTO.java
└── exception/
    ├── InsufficientBalanceException.java
    ├── DuplicateTransactionException.java
    └── WalletNotFoundException.java
```

---

## Wallet Address Generation

```java
// Generate 16-char hex address
String generateWalletAddress() {
    byte[] bytes = new byte[8];
    SecureRandom.getInstanceStrong().nextBytes(bytes);
    return HexFormat.of().formatHex(bytes); // e.g., "a1b2c3d4e5f67890"
}
```
