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

Or from the repository root:

```bash
./run.sh spring
```

The script will:
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

## Git Safety

Before pushing:

- keep real secrets in local-only files such as `.env`, `application-local.properties`, or `application-secret.properties`
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
