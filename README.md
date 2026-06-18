# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.estudios.kafka-reservation-consumer.ApplicationTests"

# Run the application
./gradlew bootRun

# Build without tests
./gradlew build -x test
```

## Architecture

This is a Spring Boot 4.0.6 / Java 17 project demonstrating Apache Kafka integration with a REST layer.

**Key dependencies:**
- `spring-boot-starter-kafka` — Kafka producer/consumer support
- `spring-boot-starter-webmvc` — REST endpoints
- `spring-boot-docker-compose` — auto-starts Docker Compose services during development (`developmentOnly` scope)
- `spring-boot-starter-mongodb` — nonrelational DB 
- Lombok — used for reducing boilerplate

**Package root:** `com.estudios.demosafkaspring`

**Infrastructure:** The app relies on Docker Compose for Kafka and mongo.

**Testing:** Uses `spring-boot-starter-kafka-test` (embedded Kafka) and `spring-boot-starter-webmvc-test` (MockMvc). The context-load test requires Kafka to be reachable; use `@EmbeddedKafka` for isolated unit/integration tests.