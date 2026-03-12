# Spring Learning Module

This module now teaches a small but realistic backend shape.

## Concepts Included

- Spring MVC controllers
- Request validation with `@Valid`
- Primary database with Spring Data JPA and H2
- Secondary analytics database with `JdbcTemplate` and H2
- Rate limiting with an in-memory fixed-window limiter
- Service boundaries that resemble a microservice-oriented design
- Connection pooling through HikariCP

## Main Endpoints

- `POST /api/users/register`
- `GET /api/users/{userId}`
- `GET /api/system/overview`
- `GET /hello?name=Spring`
- `GET /h2-console`

## Example Request

```bash
curl -X POST http://localhost:8089/api/users/register \
  -H 'Content-Type: application/json' \
  -H 'X-Caller-Key: demo-key' \
  -d '{
    "userId": "u-1",
    "email": "alice@example.com",
    "region": "APAC"
  }'
```

## Architecture Notes

- `profile` package: primary data model and JPA repository
- `registration` package: API contract and orchestration service
- `analytics` package: secondary database writes
- `notification` package: downstream client boundary
- `ratelimit` package: API throttling logic
- `system` package: diagnostics endpoint for learning

This is still one deployable Spring Boot app, but the boundaries intentionally mirror what would often become multiple services in a larger system.
