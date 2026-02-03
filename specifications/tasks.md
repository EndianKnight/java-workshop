# Tasks

## Status Legend
| Symbol | Meaning |
|--------|---------|
| `[ ]` | Not Started |
| `[/]` | In Progress |
| `[x]` | Completed |
| `[!]` | Blocked |

## Restart Point
**Last Updated:** 2026-02-04  
**Current Focus:** Phase 6 - Security  
**Next Task:** Implement Spring Security, JWT authentication, role-based access

---

## Completed ✅

### Phase 1: Project Setup
- [x] Spring Boot 4.1 project
- [x] PostgreSQL + Docker Compose
- [x] Prometheus metrics

### Phase 2: Observability
- [x] Grafana, Loki, Tempo, OTEL Collector
- [x] All dashboards (JVM, HTTP, Logs, Traces)

### Phase 3: Entity & Repository Layer
- [x] Create `User` entity (email, name, password, bank, accountId, address, role)
- [x] Create `Wallet` entity (address hex-16, balance, userId, **@Version**)
- [x] Create `Transaction` entity (from, to, amount, status, timestamp, **idempotencyKey**)
- [x] Create repositories with **pessimistic locking** queries
- [x] Add Flyway migrations
- [x] **Unit Tests:**
  - [x] `UserTest` - constructor, role, wallet relationship
  - [x] `WalletTest` - deposit, withdraw, validation
  - [x] `TransactionTest` - status transitions, markSuccess/markFailed
  - [x] `UserRepositoryTest` - CRUD, findByEmail, existsByEmail
  - [x] `WalletRepositoryTest` - CRUD, findByAddress, findByUserId
  - [x] `TransactionRepositoryTest` - idempotency, pagination, status queries

---

### Phase 4: Service Layer
- [x] `UserService` - CRUD operations
- [x] `WalletService` - balance, deposit, withdraw (**with locking**)
- [x] `TransactionService` - transfer with **SERIALIZABLE isolation**
- [x] Wallet address generation (16-char hex)
- [x] Idempotency handling for transactions
- [x] **Unit Tests:**
  - [x] `UserServiceTest` - register, update, delete, findById
  - [x] `WalletServiceTest` - deposit, withdraw, balance, concurrency
  - [x] `TransactionServiceTest` - transfer, insufficient balance, idempotency

---

## Current Sprint

### Phase 5: Controllers
- [x] `AuthController` - login, register
- [x] `UserController` - /users/me endpoints
- [x] `AdminController` - /admin/users CRUD
- [x] `WalletController` - balance, deposit, withdraw
- [x] `TransactionController` - transfer, history
- [x] `GlobalExceptionHandler` - error handling
- [x] **Unit Tests:**
  - [x] `AuthControllerTest` - login, register, token validation
  - [x] `WalletControllerTest` - wallet operations
  - [x] `TransactionControllerTest` - transfer, history

---

## Current Sprint

### Phase 6: Security
- [ ] Spring Security config
- [ ] JWT authentication
- [ ] Role-based access (USER, ADMIN)
- [ ] Password hashing (BCrypt)
- [ ] **Unit Tests:**
  - [ ] `SecurityConfigTest` - endpoint protection
  - [ ] `JwtServiceTest` - token generation, validation, expiry

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

### Phase 8: Integration & Load Testing
- [ ] Full integration tests (PostgreSQL)
- [ ] Concurrency tests (balance consistency)
- [ ] Load testing (1000+ TPS)
- [ ] API contract tests

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
| 2026-02-03 | Phase 3: Entities, repositories, Flyway migrations, unit tests (44 tests) |
| 2026-02-03 | Phase 4: Services, DTOs, exceptions, unit tests (71 total tests) |
| 2026-02-04 | Phase 5: Controllers, GlobalExceptionHandler, controller tests (86 total tests) |
