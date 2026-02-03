# Requirements

## Overview
Digital wallet application with user management and transaction capabilities.

---

## Functional Requirements

### 1. User Management

#### User Registration
Users register with:
| Field | Type | Required |
|-------|------|----------|
| email | String | ✓ |
| name | String | ✓ |
| password | String | ✓ |
| bank | String | ✓ |
| accountId | String | ✓ |
| address | String | ✓ |

#### User APIs (Self-service)
- `POST /api/v1/users/register` - Register new user
- `GET /api/v1/users/me` - Get own profile
- `PUT /api/v1/users/me` - Update own profile
- `DELETE /api/v1/users/me` - Delete own account

#### Admin APIs (Full CRUD on all users)
- `GET /api/v1/admin/users` - List all users
- `GET /api/v1/admin/users/{id}` - Get any user
- `POST /api/v1/admin/users` - Create user
- `PUT /api/v1/admin/users/{id}` - Update any user
- `DELETE /api/v1/admin/users/{id}` - Delete any user

---

### 2. Wallet System

#### Wallet Creation
- Wallet auto-created on user registration (first onboard)
- **Unique address**: 16-character hexadecimal (e.g., `a1b2c3d4e5f67890`)
- One wallet per user

#### Wallet Operations
| Operation | Endpoint | Description |
|-----------|----------|-------------|
| View Balance | `GET /api/v1/wallet` | Get current balance |
| Add Money | `POST /api/v1/wallet/deposit` | Deposit funds |
| Withdraw | `POST /api/v1/wallet/withdraw` | Withdraw funds |

---

### 3. Transactions

#### Transfer Money
- `POST /api/v1/transactions` - Transfer to another wallet
- Requires: recipient wallet address, amount
- **Validation**: Sender must have sufficient balance
- Records: sender, receiver, amount, timestamp, status

#### Transaction History
- `GET /api/v1/transactions` - List user's transactions
- `GET /api/v1/transactions/{id}` - Get transaction details

---

## Non-Functional Requirements

### Consistency
| Requirement | Target | Implementation |
|-------------|--------|----------------|
| **Strong Consistency** | All reads see latest write | Synchronous replication |
| **ACID Transactions** | Full compliance | PostgreSQL + @Transactional |
| **Isolation Level** | SERIALIZABLE for transfers | Pessimistic locking |
| **Data Integrity** | Zero balance discrepancies | DB constraints + validation |

### Performance & Throughput
| Requirement | Target |
|-------------|--------|
| **API Response Time** | < 100ms p50, < 200ms p95, < 500ms p99 |
| **Transaction TPS** | 1,000+ transactions/second |
| **Concurrent Users** | 10,000+ simultaneous connections |
| **Database Connections** | Connection pool (HikariCP) |
| **Read Throughput** | Optimistic for reads |
| **Write Throughput** | Batched where possible |

### Scalability
| Aspect | Strategy |
|--------|----------|
| Horizontal Scaling | Stateless application tier |
| Database | Read replicas for queries |
| Connection Pooling | HikariCP with tuned settings |
| Caching | Redis for session/balance cache (future) |

### Reliability
| Requirement | Target |
|-------------|--------|
| **Availability** | 99.9% uptime |
| **Recovery Time** | < 5 minutes |
| **Data Durability** | No data loss |
| **Retry Logic** | Idempotent operations |

### Observability (All Layers)

#### API Layer
| Type | What to Track |
|------|---------------|
| **Metrics** | Request count, latency (p50/p95/p99), error rate, status codes |
| **Logs** | Request/response, errors, validation failures |
| **Traces** | Full request trace with spanId |

#### Service Layer
| Type | What to Track |
|------|---------------|
| **Metrics** | Method duration, call count, exception rate |
| **Logs** | Business logic events, wallet operations, transfers |
| **Traces** | Service method spans, nested calls |

#### Database Layer
| Type | What to Track |
|------|---------------|
| **Metrics** | Query duration, connection pool stats, slow queries |
| **Logs** | SQL queries (debug), errors, connection issues |
| **Traces** | DB operation spans, transaction boundaries |

#### Transaction Layer
| Type | What to Track |
|------|---------------|
| **Metrics** | TPS, success/failure rate, transfer amounts |
| **Logs** | Transfer events, balance changes, failures |
| **Traces** | End-to-end transfer flow |

#### Infrastructure
| Tool | Purpose |
|------|---------|
| Prometheus | Metrics collection |
| Grafana | Dashboards & visualization |
| Loki | Log aggregation (with traceId) |
| Tempo | Distributed tracing |
| Alerts | Grafana alerting (future) |

---

## Security Requirements
- [x] JWT authentication for protected endpoints
- [x] Role-based access (USER, ADMIN)
- [x] Password hashing (BCrypt)
- [x] Transaction validation (balance check)
- [ ] Rate limiting (100 req/min per user)
- [ ] Input validation & sanitization
- [ ] SQL injection prevention (JPA)
- [ ] Audit logging for sensitive operations
