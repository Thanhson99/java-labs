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
└── spring/   # Spring Boot practice project
```

## Getting Started

### 1. Basic Java module

From the `basic` folder, compile and run:

```bash
cd basic/src
javac Main.java
java Main
```

### 2. Spring Boot module

From the `spring` folder, start the app with Maven Wrapper:

```bash
cd spring
./mvnw spring-boot:run
```

If you are on Windows:

```bash
mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Notes

- This repo is for learning and experiments, so code may evolve frequently.
- Feel free to add more modules (JPA, Security, REST APIs, testing) over time.
