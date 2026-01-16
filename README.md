# 🚀 Java Workshop

A production-ready **Spring Boot 4.1** workshop project showcasing modern Java 21 development practices with Docker, PostgreSQL, and Prometheus monitoring.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🏗️ **Spring Boot 4.1** | Latest snapshot with WebMVC, JPA, and Actuator |
| ☕ **Java 21** | Modern Java with records, virtual threads support |
| 🐘 **PostgreSQL** | Robust relational database with Docker integration |
| 📊 **Prometheus** | Metrics collection and monitoring out-of-the-box |
| 🐳 **Docker Compose** | One-command infrastructure setup |
| 🔍 **Actuator** | Health checks, metrics, and production readiness |
| 📝 **Structured Logging** | JSON logging with Logstash encoder |

---

## 📋 Prerequisites

- **Java 21** or later
- **Docker** & **Docker Compose** (for containerized setup)
- **Maven 3.9+** (or use the included Maven Wrapper)

---

## 🚀 Quick Start

### Option 1: Run with Docker Compose (Recommended)

```bash
# Start all services (app + PostgreSQL + Prometheus)
docker compose up -d

# View logs
docker compose logs -f workshop
```

The application will be available at: **http://localhost:8080**

### Option 2: Run Locally with Maven

```bash
# Start PostgreSQL first
docker compose up -d postgres

# Run the application
./mvnw spring-boot:run
```

---

## 🔗 Endpoints

| Endpoint | Description |
|----------|-------------|
| `http://localhost:8080` | Application root |
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/actuator/prometheus` | Prometheus metrics |
| `http://localhost:8080/actuator/info` | Application info |
| `http://localhost:9090` | Prometheus UI |

---

## ⚙️ Configuration

The application uses type-safe configuration via `AppConfig` record. Customize in `application.properties`:

```properties
# Application Settings
app.name=workshop
app.description=Workshop API Server
app.version=1.0.0

# Server Settings
app.server.port=8080
app.server.context-path=/
app.server.connection-timeout=60000
app.server.max-connections=10000
```

### Environment Variables

Override settings via environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/workshop
export SPRING_DATASOURCE_USERNAME=workshop
export SPRING_DATASOURCE_PASSWORD=workshop
```

---

## 🐳 Docker

### Build Image

```bash
# Build with multi-stage Dockerfile
docker build -t workshop:latest .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/workshop \
  workshop:latest
```

---

## 📊 Monitoring

### Prometheus

Prometheus is pre-configured to scrape metrics from the application:

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'workshop'
    static_configs:
      - targets: ['workshop:8080']
```

Access Prometheus UI at: **http://localhost:9090**

### Available Metrics

- JVM metrics (memory, GC, threads)
- HTTP request metrics
- Database connection pool metrics
- Custom application metrics

---

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Run tests with coverage
./mvnw test jacoco:report
```

---

## 📁 Project Structure

```
java-workshop/
├── src/
│   ├── main/
│   │   ├── java/com/bootstrap/workshop/
│   │   │   ├── WorkshopApplication.java    # Main entry point
│   │   │   └── config/
│   │   │       └── AppConfig.java          # Type-safe configuration
│   │   └── resources/
│   │       └── application.properties      # App configuration
│   └── test/                               # Test sources
├── compose.yaml                            # Docker Compose setup
├── Dockerfile                              # Multi-stage Docker build
├── prometheus.yml                          # Prometheus config
├── pom.xml                                 # Maven dependencies
└── mvnw                                    # Maven Wrapper
```

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.1.0-SNAPSHOT | Application framework |
| Java | 21 | Language runtime |
| PostgreSQL | 16 | Database |
| Prometheus | Latest | Metrics & monitoring |
| Lombok | Latest | Boilerplate reduction |
| Logstash Encoder | 8.0 | Structured JSON logging |

---

## 📜 License

This project is for educational and experimentation purposes.

---

<p align="center">
  Made with ☕ and Spring Boot
</p>
