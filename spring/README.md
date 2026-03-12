# Spring Learning Module

This module now teaches a small but realistic backend shape.

## Concepts Included

- Spring MVC controllers
- Request validation with `@Valid`
- Primary database with Spring Data JPA and H2
- Flyway migrations for the primary database schema
- Secondary analytics database with `JdbcTemplate` and SQL-based schema initialization
- Rate limiting with an in-memory fixed-window limiter
- JWT authentication with role-based authorization
- Refresh token rotation for issuing new access tokens
- Database-backed refresh token persistence and logout revocation
- Hashed refresh token storage with session metadata
- Refresh token reuse detection and logout-all revocation
- Transaction rollback demo on the primary database
- Event-driven registration with both Kafka and RabbitMQ publishers
- Kafka and RabbitMQ consumers with a shared event processor
- Service boundaries that resemble a microservice-oriented design
- Connection pooling through HikariCP
- Actuator health and metrics endpoints
- Custom business metrics for auth, registration, and messaging
- Testcontainers integration test against real Postgres

## Main Endpoints

- `POST /api/users/register`
- `POST /api/users/register-demo?failAfterAudit=true`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/logout-all`
- `GET /api/users/{userId}`
- `GET /api/system/overview`
- `GET /api/system/dashboard`
- `GET /hello?name=Spring`
- `GET /h2-console`
- `GET /actuator/health`
- `GET /actuator/info`
- `POST /api/auth/token`

## Example Request

```bash
TOKEN=$(curl -s -X POST http://localhost:8089/api/auth/token \
  -H 'X-Session-Label: laptop-browser' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "student",
    "password": "student123"
  }')

ACCESS_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.accessToken')
REFRESH_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.refreshToken')
SESSION_ID=$(printf '%s' "$TOKEN" | jq -r '.sessionId')

curl -X POST http://localhost:8089/api/users/register \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'X-Caller-Key: demo-key' \
  -d '{
    "userId": "u-1",
    "email": "alice@example.com",
    "region": "APAC"
  }'

curl -X POST http://localhost:8089/api/auth/refresh \
  -H 'X-Session-Label: refreshed-laptop-browser' \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

curl -X POST http://localhost:8089/api/auth/logout \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"

curl -X POST http://localhost:8089/api/auth/logout-all \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "student",
    "password": "student123"
  }'
```

Demo credentials:

- `student` / `student123` -> role `USER`
- `admin` / `admin123` -> roles `ADMIN`, `USER`

Refresh tokens are stored in the primary database as SHA-256 hashes, are single-use, and expire based on `app.security.auth.refresh-expiration-seconds`.

If a consumed or revoked refresh token is submitted again, the API now returns `refresh token reuse detected`.

Each issued refresh token now belongs to a session and returns:

- `sessionId`
- the new raw `refreshToken` shown once to the client
- an optional session label from the `X-Session-Label` header

Protected endpoints:

- `/api/users/**` -> `USER` or `ADMIN`
- `/api/system/**` -> `ADMIN`

## Architecture Notes

- `profile` package: primary data model and JPA repository
- `audit` package: primary-database audit writes that participate in the main transaction
- `registration` package: API contract and orchestration service
- `analytics` package: secondary database writes
- `notification` package: downstream client boundary
- `messaging` package: domain event fan-out to Kafka and RabbitMQ
- `ratelimit` package: API throttling logic
- `system` package: diagnostics endpoint for learning

This is still one deployable Spring Boot app, but the boundaries intentionally mirror what would often become multiple services in a larger system.

The repository now also contains a separate [`notification-service`](/Users/hopee/Downloads/java-labs/notification-service/README.md) module so you can compare:

- in-process consumers inside `spring/`
- a standalone consumer service in `notification-service/`

The standalone consumer keeps Kafka and RabbitMQ listeners disabled by default. Enable them through `notification-service/.env` when you want to study cross-service event flow.

## Running With Real Postgres

Start the local infrastructure:

```bash
cd spring
docker compose up -d
```

Run the app with the `postgres` profile:

```bash
SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
```

This uses:

- `usersdb` on `localhost:15432`
- `analyticsdb` on `localhost:15433`
- Kafka on `localhost:19092`
- RabbitMQ on `localhost:5672`
- RabbitMQ management UI on `http://localhost:15672`

The test suite also includes a Testcontainers-based integration test that boots two disposable Postgres containers and verifies the registration flow end to end.

## Kafka And RabbitMQ Learning

The registration flow now emits the same `UserRegisteredEvent` through a fan-out publisher.

Consumer side:

- `KafkaUserRegistrationEventListener` receives Kafka messages
- `RabbitUserRegistrationEventListener` receives RabbitMQ messages
- `UserRegistrationEventProcessor` handles both transports through one shared path
- `/api/system/overview` shows which transports are enabled and how many events each transport has consumed

- Kafka is better for durable event streams, replay, partitions, and consumer groups.
- RabbitMQ is better for traditional queueing, routing patterns, and work distribution.
- This project includes both so you can compare the code paths directly.

Default behavior:

- Kafka publishing is disabled
- RabbitMQ publishing is disabled
- the app still runs and tests without local brokers

Local configuration:

- use [`.env.example`](/Users/hopee/Downloads/java-labs/.env.example) for repository-wide placeholders
- use [`spring/.env.example`](/Users/hopee/Downloads/java-labs/spring/.env.example) when you want Docker Compose and Spring to share the same local settings
- do not commit `.env` files with real credentials
- `run.sh` and `run.ps1` automatically load `.env` and `spring/.env` when those files exist

Enable both transports for local study:

```bash
cd spring
APP_MESSAGING_KAFKA_ENABLED=true \
APP_MESSAGING_RABBITMQ_ENABLED=true \
SPRING_PROFILES_ACTIVE=postgres \
./mvnw spring-boot:run
```

Useful properties:

- `APP_MESSAGING_KAFKA_ENABLED=true`
- `APP_MESSAGING_KAFKA_TOPIC=user-registered.v1`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:19092`
- `APP_MESSAGING_RABBITMQ_ENABLED=true`
- `APP_MESSAGING_RABBITMQ_EXCHANGE=user.registration.exchange`
- `APP_MESSAGING_RABBITMQ_QUEUE=user.registration.queue`
- `APP_MESSAGING_RABBITMQ_ROUTING_KEY=user.registered`
- `SPRING_RABBITMQ_HOST=localhost`
- `SPRING_RABBITMQ_PORT=5672`

Check `/api/system/overview` as `admin` to see which transports are enabled.

## Browser Playground

The root page `/` is now a small learning dashboard rather than a plain welcome page.

It gives you:

- a quick summary of the backend architecture
- public runtime and observability cards
- important endpoints
- a copyable login curl
- a browser-side playground for:
  - `GET /hello`
  - `POST /api/auth/token`
  - `GET /api/system/overview`

## Observability

This module now exposes a basic observability path for study:

- `GET /actuator/health` is public
- `GET /actuator/info` is public
- `GET /actuator/metrics` is available for `ADMIN`
- `GET /api/system/dashboard` includes safe runtime and business-metric summaries for the home page

Business metrics currently track:

- auth token issuance and refresh flow
- logout and logout-all actions
- registration success, failure, rate limiting, and average duration
- published registration events by transport
