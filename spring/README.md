# Spring Learning Module

This module now teaches a small but realistic backend shape.

## Concepts Included

- Spring MVC controllers
- Request validation with `@Valid`
- Primary database with Spring Data JPA and H2
- Secondary analytics database with `JdbcTemplate` and H2
- Rate limiting with an in-memory fixed-window limiter
- API key authentication for `/api/**` endpoints
- Transaction rollback demo on the primary database
- Service boundaries that resemble a microservice-oriented design
- Connection pooling through HikariCP

## Main Endpoints

- `POST /api/users/register`
- `POST /api/users/register-demo?failAfterAudit=true`
- `GET /api/users/{userId}`
- `GET /api/system/overview`
- `GET /hello?name=Spring`
- `GET /h2-console`

## Example Request

```bash
curl -X POST http://localhost:8089/api/users/register \
  -H 'Content-Type: application/json' \
  -H 'X-API-Key: dev-secret-key' \
  -H 'X-Caller-Key: demo-key' \
  -d '{
    "userId": "u-1",
    "email": "alice@example.com",
    "region": "APAC"
  }'
```

The protected `/api/**` endpoints currently require:

- header: `X-API-Key`
- value: `dev-secret-key`

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
