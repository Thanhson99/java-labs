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
- Service boundaries that resemble a microservice-oriented design
- Connection pooling through HikariCP
- Testcontainers integration test against real Postgres

## Main Endpoints

- `POST /api/users/register`
- `POST /api/users/register-demo?failAfterAudit=true`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/logout-all`
- `GET /api/users/{userId}`
- `GET /api/system/overview`
- `GET /hello?name=Spring`
- `GET /h2-console`
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

- Kafka is better for durable event streams, replay, partitions, and consumer groups.
- RabbitMQ is better for traditional queueing, routing patterns, and work distribution.
- This project includes both so you can compare the code paths directly.

Default behavior:

- Kafka publishing is disabled
- RabbitMQ publishing is disabled
- the app still runs and tests without local brokers

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
