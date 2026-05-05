# java-labs

Reorganized Java lab workspace with multiple learning sites and GitHub Pages friendly entrypoints.

Live site: [Java Labs](https://thanhson99.github.io/java-labs/docs/index.html)

Java Labs is a Java backend learning repository and a static GitHub Pages learning portal at the same time.

The learning portal currently includes:
- `Interview Practice Site`: topic-based study with questions, answers, explanations, code examples, and self-practice prompts.
- `Interview Levels Site`: interview question hub grouped by `Basic / Intermediate / Advanced`.
- `Quiz Site`: randomized quiz sets to evaluate your level across `Fresher / Interview / Senior`.
- `Roadmap Site`: Java backend learning roadmap, technology map, integration flow, and knowledge tree.

## Learning Sites

- [Interview Practice Site](https://thanhson99.github.io/java-labs/docs/interview.html)
- [Interview Levels Site](https://thanhson99.github.io/java-labs/docs/interview-levels.html)
- [Quiz Site](https://thanhson99.github.io/java-labs/docs/quiz.html)
- [Roadmap Site](https://thanhson99.github.io/java-labs/docs/roadmap.html)

## Main Structure

```text
java-labs/
├── index.html              # Redirects to the portal inside docs
├── docs/                   # Static learning portal for GitHub Pages
├── basic/                  # Java core practice
├── spring/                 # Spring Boot practice project
└── notification-service/   # Event consumer service
```

## GitHub Pages

There are two practical ways to host the portal:

1. Host from the repository root
- Root `index.html` redirects to `docs/index.html`

2. Host from the `docs` folder
- Open `Settings -> Pages`
- Choose `Deploy from a branch`
- Select your main branch and the `/docs` folder

If your repository is `java-labs` under the `thanhson99` account, the live URL will look like:

`https://thanhson99.github.io/java-labs/`

## Static Learning Portal

The portal inside `docs/` uses:
- `HTML`
- `CSS`
- `JavaScript`
- `JSON`

There is no database. Content is loaded from JSON files so the portal can keep growing with:
- theory questions
- interview questions
- code questions and trick questions
- static quizzes
- roadmap and technology guides

## Getting Started

### 1. Basic Java module

```bash
cd basic
./mvnw -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

Run tests:

```bash
./mvnw test
```

Or from the repository root:

```bash
./run.sh basic
```

### 2. Spring Boot module

```bash
cd spring
./mvnw spring-boot:run
```

Or from the repository root:

```bash
./run.sh spring
```

Example:

```bash
./run.sh spring
JAVA_VERSION=17 PORT=8095 ./run.sh spring
```

The Spring module currently includes:
- REST API
- request validation
- H2 + JPA + Flyway
- analytics JDBC datasource
- transaction rollback demo
- JWT authentication
- refresh token rotation
- logout / logout-all
- Kafka + RabbitMQ publishers/consumers
- outbox pattern
- retry + dead-letter handling
- Actuator health + metrics
- Postgres profile with Docker Compose
- Testcontainers integration tests

Useful endpoints:

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
