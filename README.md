# Java Labs

This repository is my practice workspace for learning **Java core** and **Spring Boot**.

## Goals

- Practice Java fundamentals (syntax, OOP, basic algorithms)
- Build and run a Spring Boot web application
- Keep small, focused exercises in one place

## Project Structure

```text
java-labs/
├── basic/    # Core Java practice
├── spring/   # Main Spring Boot practice project
└── notification-service/   # Extracted event consumer service
```

## Getting Started

### 1. Basic Java module

From the `basic` folder, run the learning app:

```bash
cd basic
./mvnw -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

Run the unit tests:

```bash
./mvnw test
```

Or from the repository root:

```bash
./run.sh basic
```

The script tries to use `Java 17` by default. You can override it per run:

```bash
JAVA_VERSION=17 ./run.sh basic
```

### 2. Spring Boot module

From the `spring` folder, start the app with Maven Wrapper:

```bash
cd spring
./mvnw spring-boot:run
```

This module currently targets `Java 17`, which matches the local setup and Spring Boot 3 baseline.

The Spring module now includes:

- REST endpoints for user registration and profile lookup
- request validation
- H2 primary database with Spring Data JPA
- Flyway migrations for the primary database
- secondary H2 analytics database with `JdbcTemplate` and SQL-based schema init
- transaction rollback demo on the primary database
- in-memory rate limiting
- JWT authentication with role-based authorization
- refresh token rotation
- database-backed refresh token persistence and logout revocation
- hashed refresh token storage with session metadata
- refresh token reuse detection and logout-all revocation
- event-driven registration with both Kafka and RabbitMQ publishers
- Kafka and RabbitMQ consumers with a shared processing path
- Actuator health and metrics plus custom business observability
- a microservice-style service layer
- optional Postgres profile with Docker Compose
- Testcontainers integration tests against real Postgres

Or from the repository root:

```bash
./run.sh spring
```

The script will:
- load `.env` and `spring/.env` when present
- try to use the requested Java version (`17` by default)
- start at port `8089`
- automatically move to the next free port if that port is already in use
- open the browser when the app responds

Examples:

```bash
./run.sh spring
JAVA_VERSION=17 PORT=8095 ./run.sh spring
```

If you are on Windows:

```bash
mvnw.cmd spring-boot:run
```

Or from the repository root in PowerShell:

```powershell
.\run.ps1 basic
.\run.ps1 spring
$env:JAVA_VERSION=17; .\run.ps1 spring
```

Or from `cmd`:

```bat
run.cmd basic
run.cmd spring
set JAVA_VERSION=17 && run.cmd spring
```

Then open:

```text
http://localhost:8089
or the next free port chosen by the script
```

The `/` page now includes:

- a backend capability overview
- live dashboard cards for runtime and observability
- a quick login curl snippet
- a small API playground for `/hello`, `/api/auth/token`, and `/api/system/overview`

Useful Spring endpoints:

```text
GET  /hello?name=Spring
POST /api/auth/token
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/logout-all
POST /api/users/register
POST /api/users/register-demo?failAfterAudit=true
GET  /api/users/{userId}
GET  /api/system/dashboard
GET  /api/system/overview
GET  /actuator/health
GET  /actuator/info
GET  /h2-console
```

Protected `/api/**` endpoints require:

```text
Authorization: Bearer <jwt>
```

Demo credentials:

```text
student / student123 -> USER
admin / admin123 -> ADMIN, USER
```

Example:

```bash
TOKEN=$(curl -s -X POST http://localhost:8089/api/auth/token \
  -H 'X-Session-Label: laptop-browser' \
  -H 'Content-Type: application/json' \
  -d '{"username":"student","password":"student123"}')

ACCESS_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.accessToken')
REFRESH_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.refreshToken')
SESSION_ID=$(printf '%s' "$TOKEN" | jq -r '.sessionId')

curl -X POST http://localhost:8089/api/users/register \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -H 'X-Caller-Key: demo-key' \
  -d '{"userId":"u-1","email":"alice@example.com","region":"APAC"}'

curl -X POST http://localhost:8089/api/auth/refresh \
  -H 'X-Session-Label: refreshed-laptop-browser' \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

curl -X POST http://localhost:8089/api/auth/logout \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

curl -X POST http://localhost:8089/api/auth/logout-all \
  -H 'Content-Type: application/json' \
  -d '{"username":"student","password":"student123"}'
```

If a consumed or revoked refresh token is reused, the API returns `refresh token reuse detected`.

Run the Postgres version:

```bash
cd spring
docker compose up -d
SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
```

For local secrets and runtime settings, start from the example files:

```bash
cp .env.example .env
cp spring/.env.example spring/.env
```

Then replace placeholder values such as `change-me` and `replace-with-32-plus-char-secret`.

Enable both messaging transports while you study event-driven flows:

```bash
cd spring
APP_MESSAGING_KAFKA_ENABLED=true \
APP_MESSAGING_RABBITMQ_ENABLED=true \
SPRING_PROFILES_ACTIVE=postgres \
./mvnw spring-boot:run
```

Local broker ports from `spring/docker-compose.yml`:

- Kafka: `localhost:19092`
- RabbitMQ AMQP: `localhost:5672`
- RabbitMQ UI: `http://localhost:15672`

Run Spring tests:

```bash
cd spring
./mvnw test
```

### 3. Notification service module

This module shows what happens after you split an event consumer into its own Spring Boot service.

Run it:

```bash
cd notification-service
./mvnw spring-boot:run
```

Or from the repository root:

```bash
./run.sh notification-service
```

The notification service script also loads:

```text
.env
notification-service/.env
```

Default behavior:

- listeners stay off until you explicitly enable them
- this keeps startup and tests clean when Kafka or RabbitMQ are not running

Enable both transports:

```bash
cp notification-service/.env.example notification-service/.env
./run.sh notification-service
```

Useful endpoints:

```text
GET /api/notifications/healthz
GET /api/notifications/inbox
```

## Git Safety

Before pushing:

- keep real secrets in local-only files such as `.env`, `application-local.properties`, or `application-secret.properties`
- start from `.env.example` or `spring/.env.example`, then change the placeholder values locally
- never hardcode API keys, passwords, tokens, private keys, or JDBC credentials in source files
- use the root `.gitignore` to keep local config and certificate files out of Git
- run the secret scan from the repository root:

```bash
chmod +x ./check-secrets.sh
./check-secrets.sh
```

The scanner only catches obvious patterns, so it is a safety net, not a guarantee.

## Notes

- This repo is for learning and experiments, so code may evolve frequently.
- Feel free to add more modules (JPA, Security, REST APIs, testing) over time.
