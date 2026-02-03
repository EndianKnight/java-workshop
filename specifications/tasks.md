# Tasks

## Status Legend
| Symbol | Meaning |
|--------|---------|
| `[ ]` | Not Started |
| `[/]` | In Progress |
| `[x]` | Completed |
| `[!]` | Blocked |

## Restart Point
**Last Updated:** 2026-02-03  
**Current Focus:** Phase 3 - Entity & Repository Layer  
**Next Task:** Create User, Wallet, Transaction entities with locking support

---

## Completed ✅

### Phase 1: Project Setup
- [x] Spring Boot 4.1 project
- [x] PostgreSQL + Docker Compose
- [x] Prometheus metrics

### Phase 2: Observability
- [x] Grafana, Loki, Tempo, OTEL Collector
- [x] All dashboards (JVM, HTTP, Logs, Traces)

---

## Current Sprint

### Phase 3: Entity & Repository Layer
- [ ] Create `User` entity (email, name, password, bank, accountId, address, role)
- [ ] Create `Wallet` entity (address hex-16, balance, userId, **@Version**)
- [ ] Create `Transaction` entity (from, to, amount, status, timestamp, **idempotencyKey**)
- [ ] Create repositories with **pessimistic locking** queries
- [ ] Add Flyway migrations

### Phase 4: Service Layer
- [ ] `UserService` - CRUD operations
- [ ] `WalletService` - balance, deposit, withdraw (**with locking**)
- [ ] `TransactionService` - transfer with **SERIALIZABLE isolation**
- [ ] Wallet address generation (16-char hex)
- [ ] Idempotency handling for transactions

### Phase 5: Controllers
- [ ] `AuthController` - login, register
- [ ] `UserController` - /users/me endpoints
- [ ] `AdminController` - /admin/users CRUD
- [ ] `WalletController` - balance, deposit, withdraw
- [ ] `TransactionController` - transfer, history

### Phase 6: Security
- [ ] Spring Security config
- [ ] JWT authentication
- [ ] Role-based access (USER, ADMIN)
- [ ] Password hashing (BCrypt)

### Phase 7: Performance, Consistency & Observability
- [ ] Configure HikariCP connection pool (50 connections)
- [ ] Configure SERIALIZABLE isolation for transfers
- [ ] Add @Version optimistic locking to Wallet
- [ ] Implement idempotency keys
- [ ] Add batch processing config
- [ ] Add @Timed/@Counted metrics to service methods
- [ ] Configure structured JSON logging with traceId/spanId
- [ ] Add custom metrics for wallet operations
- [ ] Create database metrics dashboard
- [ ] Create transaction metrics dashboard

---

## Backlog

### Phase 8: Testing
- [ ] Unit tests for services
- [ ] Integration tests
- [ ] Concurrency tests (balance consistency)
- [ ] Load testing (1000+ TPS)

### Phase 9: Enhancements
- [ ] Pagination for lists
- [ ] Transaction filters
- [ ] Rate limiting (100 req/min)
- [ ] Redis caching (future)

---

## Session Log
| Date | Work Done |
|------|-----------|
| 2026-02-02 | Project setup, observability stack |
| 2026-02-03 | Specs created, NFRs for consistency & throughput |
