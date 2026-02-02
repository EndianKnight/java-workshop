# 🚀 Java Workshop

A production-ready **Spring Boot 4.1** workshop project showcasing modern Java 21 development practices with Docker, PostgreSQL, and full observability stack.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🏗️ **Spring Boot 4.1** | Latest snapshot with WebMVC, JPA, and Actuator |
| ☕ **Java 21** | Modern Java with records, virtual threads support |
| 🐘 **PostgreSQL** | Robust relational database with Docker integration |
| 📊 **Grafana Dashboards** | Pre-built JVM and HTTP metrics dashboards |
| � **OpenTelemetry** | Distributed tracing with Tempo |
| � **Loki** | Centralized log aggregation |
| 📈 **Prometheus** | Metrics collection and monitoring |
| � **Docker Compose** | One-command infrastructure setup |

---

## 📋 Prerequisites

- **Java 21** or later
- **Docker** & **Docker Compose** (for containerized setup)
- **Maven 3.9+** (or use the included Maven Wrapper)

---

## 🚀 Quick Start

### Option 1: Run Everything with Docker Compose (Recommended)

```bash
# Start all services (app + database + observability stack)
docker compose up -d

# View application logs
docker compose logs -f workshop
```

### Option 2: Run App Locally with Maven

```bash
# Start infrastructure services
docker compose up -d postgres grafana prometheus loki tempo otel-collector

# Run the application
./mvnw spring-boot:run
```

---

## 🔗 Endpoints

### Application

| Endpoint | Description |
|----------|-------------|
| http://localhost:8080 | Application root |
| http://localhost:8080/actuator/health | Health check |
| http://localhost:8080/actuator/prometheus | Prometheus metrics |
| http://localhost:8080/actuator/info | Application info |

### Observability Stack

| Service | URL | Credentials |
|---------|-----|-------------|
| **Grafana** | http://localhost:3000 | admin / admin |
| **Prometheus** | http://localhost:9090 | - |
| **Loki** | http://localhost:3100 | - |
| **Tempo** | http://localhost:3200 | - |

---

## 📊 Grafana Dashboards

Pre-built dashboards are automatically provisioned:

### JVM Metrics Dashboard
- Heap memory usage
- Thread counts (live, daemon, peak)
- Garbage collection pause times
- CPU usage (process and system)

### HTTP Metrics Dashboard
- Request rate by endpoint
- Response time percentiles (p50, p95, p99)
- Error counts by status code

**Access**: Grafana → Dashboards → Workshop folder

---

## 🔭 Observability Architecture

```
┌─────────────────┐     OTLP      ┌──────────────────┐
│  Spring Boot    │──────────────►│  OTEL Collector  │
│  Application    │               └────────┬─────────┘
└─────────────────┘                        │
                                           ▼
                    ┌──────────────────────┼──────────────────────┐
                    │                      │                      │
                    ▼                      ▼                      ▼
             ┌──────────┐          ┌──────────┐          ┌──────────┐
             │ Prometheus│          │   Loki   │          │  Tempo   │
             │ (Metrics) │          │  (Logs)  │          │ (Traces) │
             └─────┬─────┘          └────┬─────┘          └────┬─────┘
                   │                     │                     │
                   └─────────────────────┼─────────────────────┘
                                         ▼
                                  ┌──────────────┐
                                  │   Grafana    │
                                  │ (Dashboards) │
                                  └──────────────┘
```

---

## ⚙️ Configuration

### Application Settings

Customize in `application.properties`:

```properties
# Application Settings
app.name=workshop
app.description=Workshop API Server
app.version=1.0.0

# Server Settings
app.server.port=8080
app.server.context-path=/
```

### OpenTelemetry Settings

```properties
# Tracing
management.tracing.sampling.probability=1.0
management.otlp.tracing.endpoint=http://localhost:4318/v1/traces
```

---

## 🐳 Docker Services

| Container | Image | Ports |
|-----------|-------|-------|
| workshop-app | Built from Dockerfile | 8080 |
| workshop-postgres | postgres:16-alpine | 5432 |
| workshop-grafana | grafana/grafana | 3000 |
| workshop-prometheus | prom/prometheus | 9090 |
| workshop-loki | grafana/loki | 3100 |
| workshop-tempo | grafana/tempo | 3200 |
| workshop-otel-collector | otel/opentelemetry-collector-contrib | 4317, 4318, 8889 |

---

## 📁 Project Structure

```
java-workshop/
├── src/main/java/com/bootstrap/workshop/
│   ├── WorkshopApplication.java      # Main entry point
│   └── config/AppConfig.java         # Type-safe configuration
├── observability/
│   ├── otel-collector-config.yaml    # OTEL Collector pipeline
│   ├── tempo-config.yaml             # Tempo tracing config
│   └── grafana/
│       ├── provisioning/             # Auto-provisioned datasources
│       └── dashboards/               # Pre-built dashboards
├── compose.yaml                       # Docker Compose setup
├── Dockerfile                         # Multi-stage Docker build
├── prometheus.yml                     # Prometheus scrape config
└── pom.xml                           # Maven dependencies
```

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.1.0-SNAPSHOT | Application framework |
| Java | 21 | Language runtime |
| PostgreSQL | 16 | Database |
| Grafana | Latest | Dashboards & visualization |
| Prometheus | Latest | Metrics collection |
| Loki | Latest | Log aggregation |
| Tempo | Latest | Distributed tracing |
| OpenTelemetry | 1.45.0 | Observability standard |

---

## 📜 License

This project is for educational and experimentation purposes.

---

<p align="center">
  Made with ☕ and Spring Boot
</p>
