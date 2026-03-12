# Basic Java Learning Module

This module is a reading-first Java playground.

The code is intentionally split into small classes so you can study one concept at a time:

- `ControlFlowExamples`: variables, conditionals, loops, validation
- `StringToolkit`: strings, iteration, formatting, simple algorithms
- `BankAccount`: classes, encapsulation, invariants, exceptions
- `Student` and `StudentAnalytics`: records, collections, streams, comparators
- `Shape`, `Circle`, and `Rectangle`: interfaces, sealed hierarchies, polymorphism
- `AsyncExamples`: `CompletableFuture` and asynchronous composition
- `DifficultyLevel`: enums and fixed sets of constants
- `Pair`: generics and reusable type-safe containers
- `InventoryItem` and `InventoryAnalytics`: maps, grouping, sorting, and filtering
- `ExceptionPlayground`: validation and exception design
- `FileReport`: file I/O with `Path` and `Files`
- `FixedWindowRateLimiter`: fixed-window throttling for API-like workloads
- `SimpleConnectionPool` and `FakeDatabaseConnection`: connection reuse and pool limits
- `UserProfileRepository` family: repository abstraction, in-memory database, and region-based routing
- `RegistrationService`: a microservice-style orchestration layer with persistence and downstream notification
- `LearningApp`: a single runnable entry point that exercises the examples

## Commands

Run the learning app:

```bash
./mvnw -q -DskipTests compile
java -cp target/classes com.example.javalabs.basic.LearningApp
```

Run unit tests:

```bash
./mvnw test
```

Package the module:

```bash
./mvnw package
```

## How To Read This Module

1. Start with `LearningApp` to see the big picture.
2. Open one class at a time and read the JavaDoc above each method.
3. Read the corresponding test class to understand expected behavior.
4. Change a method, rerun the tests, and observe what breaks.

## Suggested Study Order

1. `ControlFlowExamples`
2. `StringToolkit`
3. `BankAccount`
4. `StudentAnalytics`
5. `Shape`
6. `AsyncExamples`
7. `DifficultyLevel` and `Pair`
8. `InventoryAnalytics`
9. `ExceptionPlayground`
10. `FileReport`
11. `FixedWindowRateLimiter`
12. `SimpleConnectionPool`
13. `MultiDatabaseUserProfileRepository`
14. `RegistrationService`
