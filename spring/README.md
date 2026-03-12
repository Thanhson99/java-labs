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
- Transaction rollback demo on the primary database
- Service boundaries that resemble a microservice-oriented design
- Connection pooling through HikariCP
- Testcontainers integration test against real Postgres

## Main Endpoints

- `POST /api/users/register`
- `POST /api/users/register-demo?failAfterAudit=true`
- `POST /api/auth/refresh`
- `GET /api/users/{userId}`
- `GET /api/system/overview`
- `GET /hello?name=Spring`
- `GET /h2-console`
- `POST /api/auth/token`

## Example Request

```bash
TOKEN=$(curl -s -X POST http://localhost:8089/api/auth/token \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "student",
    "password": "student123"
  }')

ACCESS_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.accessToken')
REFRESH_TOKEN=$(printf '%s' "$TOKEN" | jq -r '.refreshToken')

curl -X POST http://localhost:8089/api/users/register \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'X-Caller-Key: demo-key' \
  -d '{
    "userId": "u-1",
    "email": "alice@example.com",
    "region": "APAC"
  }'

curl -X POST http://localhost:8089/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}"
```

Demo credentials:

- `student` / `student123` -> role `USER`
- `admin` / `admin123` -> roles `ADMIN`, `USER`

Refresh tokens are opaque in-memory tokens intended for learning token rotation. They are single-use and expire based on `app.security.auth.refresh-expiration-seconds`.

Protected endpoints:

- `/api/users/**` -> `USER` or `ADMIN`
- `/api/system/**` -> `ADMIN`

## Architecture Notes

- `profile` package: primary data model and JPA repository
- `audit` package: primary-database audit writes that participate in the main transaction
- `registration` package: API contract and orchestration service
- `analytics` package: secondary database writes
- `notification` package: downstream client boundary
- `ratelimit` package: API throttling logic
- `system` package: diagnostics endpoint for learning

This is still one deployable Spring Boot app, but the boundaries intentionally mirror what would often become multiple services in a larger system.

## Running With Real Postgres

Start the two Postgres containers:

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

The test suite also includes a Testcontainers-based integration test that boots two disposable Postgres containers and verifies the registration flow end to end.
