# Notification Service

This service is the first extracted microservice in the repo.

It consumes `UserRegisteredEvent` from:

- Kafka
- RabbitMQ

It exposes a simple inbox API so you can inspect what the service has received:

- `GET /api/notifications/healthz`
- `GET /api/notifications/inbox`

Run it:

```bash
cd notification-service
./mvnw spring-boot:run
```

Or from the repository root:

```bash
./run.sh notification-service
```

Useful notes:

- default port: `8099`
- default Kafka topic: `user-registered.v1`
- default Rabbit queue: `user.registration.queue`
- Kafka and RabbitMQ listeners are disabled by default
- message state is in-memory for learning, not persisted

To enable both transports locally:

```bash
cp .env.example .env
./mvnw spring-boot:run
```

Or from the repository root:

```bash
cp notification-service/.env.example notification-service/.env
./run.sh notification-service
```

Useful endpoints:

- `GET /api/notifications/healthz`
- `GET /api/notifications/inbox`
