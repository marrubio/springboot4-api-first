# Project instructions for coding agents

## Purpose

This repository contains a Spring Boot 4 sample backend that is being evolved toward an API First approach.
Use this file as persistent guidance for all future chats working on this project.

## Mandatory rules

1. **English only inside the repository**
   - All source code, comments, documentation, commit messages, and generated text stored in the repo must be written in English.
   - This rule is also documented in `LANGUAGE_REQUIREMENT.md`.

2. **Respect the existing architecture**
   - The project follows a hexagonal / onion style architecture.
   - Follow Clean Architecture principles and keep boundaries between layers explicit.
   - Keep business logic out of controllers and framework config classes.
   - Prefer minimal, targeted changes over broad refactors.

3. **Keep code readable and maintainable**
   - Ensure code is readable, maintainable, and well-structured.
   - Follow Clean Code principles in naming, method size, responsibility boundaries, and duplication control.

4. **Trust code and tests over outdated docs**
   - Some documentation describes an aspirational structure.
   - When documentation and implementation differ, use the current package structure and ArchUnit tests as the source of truth.

5. **Document relevant operational changes**
   - If you change startup behavior, configuration, profiles, endpoints, or developer workflow, update `README.md` as part of the same task.
   - This repository currently has a single top-level `README.md`; if the project later introduces division-specific READMEs, update the closest relevant README as well.

6. **Keep the project aligned with its API First goal**
   - The repository is a sample backend for API First development.
   - New externally exposed features should be implemented as REST APIs and aligned with the migration roadmap in `TASKS_TO_API_FIRST.md`.
   - Avoid introducing alternative external entry points unless a task explicitly requires them.

## Confirmed stack

- Java 25
- Spring Boot `4.0.2`
- Maven wrapper: `mvnw` / `mvnw.cmd`
- Spring Web
- Spring Security OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL for normal runtime
- H2 for tests
- MapStruct `1.6.3`
- ArchUnit `1.4.1`

## Additional Spring Boot guidance

- Use `.github/skills/java-springboot/SKILL.md` as a complementary Spring Boot best-practices reference.
- If the skill conflicts with this repository's current architecture, package layout, or ArchUnit rules, **follow this `AGENTS.md`, the existing code, and the tests first**.
- In particular, do **not** switch this codebase to package-by-feature just because the generic skill suggests it; this project currently uses an architecture-oriented package structure that must be preserved.
- Do **not** migrate from `application.properties` to YAML unless a task explicitly requires it.

Key practices from the Spring Boot skill that should be followed here:

- Prefer constructor injection for required dependencies.
- Keep injected dependencies as `private final` fields.
- Use the correct Spring stereotype annotations (`@RestController`, `@Service`, `@Repository`, `@Configuration`).
- Keep using DTOs in the REST layer; never expose JPA entities directly through the API.
- Validate request DTOs with Bean Validation and `@Valid`.
- Keep exception mapping centralized with `@ControllerAdvice` / `@ExceptionHandler`.
- Keep business logic in application services and use `@Transactional` at the appropriate service boundary.
- Externalize configuration, keep secrets out of source code, and prefer environment variables for sensitive values.
- Use `@ConfigurationProperties` for typed configuration when adding new grouped properties.
- Use SLF4J with parameterized logging.
- Prefer focused tests: unit tests with Mockito, controller tests, repository tests, and `@SpringBootTest` integration tests when full wiring matters.
- Keep using Spring Security for authentication and authorization concerns.

## Current project state

- The application is **not fully API First yet**.
- There is a detailed migration roadmap in `TASKS_TO_API_FIRST.md`.
- Current REST endpoints are implemented manually, mainly through `adapter.in.rest.controller.GameController`.
- Current external base path is configured with `server.servlet.context-path=/api`, so controller mappings under `/games` are exposed as `/api/games`.

## Package map to follow

Use the current package layout when adding or modifying code:

- `src/main/java/es/marugi/spring/api/adapter/in/rest/controller`
  - REST controllers
- `src/main/java/es/marugi/spring/api/adapter/in/rest/dto`
  - Request/response DTOs for the HTTP layer
- `src/main/java/es/marugi/spring/api/adapter/in/rest/mapper`
  - REST-layer mappers
- `src/main/java/es/marugi/spring/api/adapter/in/rest/error`
  - Exception handling and API error payloads
- `src/main/java/es/marugi/spring/api/application/service`
  - Application services and use-case orchestration
- `src/main/java/es/marugi/spring/api/application/dto`
  - Application-layer DTOs
- `src/main/java/es/marugi/spring/api/application/mapper`
  - MapStruct mappers between application DTOs and domain entities
- `src/main/java/es/marugi/spring/api/application/exception`
  - Application exceptions
- `src/main/java/es/marugi/spring/api/domain/model`
  - Domain entities
- `src/main/java/es/marugi/spring/api/domain/repository`
  - Domain repository ports
- `src/main/java/es/marugi/spring/api/adapter/out/persistence`
  - Persistence adapters and Spring Data repositories
- `src/main/java/es/marugi/spring/api/infrastructure/config`
  - Security and infrastructure configuration

## Architectural guardrails

- Domain code must stay independent from adapters and infrastructure.
- Controllers should remain thin and delegate to application services.
- Persistence details belong in `adapter.out.persistence`.
- Security and CORS belong in `infrastructure.config`.
- Service classes should remain in `..service..` packages and keep the `*Service` or `*ServiceImpl` naming expected by ArchUnit.
- Controller classes should keep the `*Controller` suffix and live in controller packages.

## Testing expectations

Before finishing a non-trivial change, run the most relevant tests.

Recommended commands on Windows PowerShell:

```powershell
.\mvnw.cmd clean package -DskipTests
.\mvnw.cmd test -Ptest
.\mvnw.cmd test -Dtest=OnionArchitectureTest,NamingConventionTest,ControllerRulesTest,DaoRulesTest -Ptest
```

Notes:

- The `test` profile uses H2 and `application-test.properties`.
- Integration tests such as `GameControllerIntegrationTest` use `@ActiveProfiles("test")`.
- If you change controller behavior, also review Web MVC and integration tests.
- If you change package structure or naming, run the ArchUnit tests.

## Runtime and security notes

- Default runtime configuration expects these environment variables:
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - `KC_BASE_URL`
- In non-test profiles:
  - `GET /games` and `GET /games/{id}` are public
  - `POST`, `PUT`, and `DELETE` on `/games` require authentication
- In the `test` profile, security is relaxed to permit all requests.

## API First working mode

When the task is related to API First migration:

1. Read `TASKS_TO_API_FIRST.md` first.
2. Execute tasks sequentially when possible.
3. Treat the OpenAPI contract as the future source of truth.
4. Keep generated code separated from handwritten code.
5. Prefer adapting the current controller flow incrementally rather than mixing old and new patterns without a clear boundary.

## Practical change guidance

- For endpoint changes:
  - review controller, REST DTOs, REST mapper, application DTOs/services, and related tests
- For persistence changes:
  - review domain model, repository port, persistence adapter, JPA repository, and tests
- For error-handling changes:
  - review `GlobalExceptionHandler` and integration tests asserting API error payloads
- For security changes:
  - review `SecurityConfig`, related properties, and `SecurityIntegrationTest`

## Important files to consult frequently

- `pom.xml`
- `README.md`
- `LANGUAGE_REQUIREMENT.md`
- `TASKS_TO_API_FIRST.md`
- `src/main/resources/application.properties`
- `src/main/resources/application-test.properties`
- `src/test/java/es/marugi/spring/api/arch/OnionArchitectureTest.java`
- `src/test/java/es/marugi/spring/api/arch/NamingConventionTest.java`
- `src/test/java/es/marugi/spring/api/arch/ControllerRulesTest.java`
- `src/test/java/es/marugi/spring/api/arch/DaoRulesTest.java`

## Decision rule for future chats

If unsure where to implement a change, follow this order:

1. Check the existing package where similar code already lives.
2. Check architecture tests to avoid violating package or naming rules.
3. Keep changes consistent with the current implementation first.
4. For API First tasks, align with `TASKS_TO_API_FIRST.md` and update progress clearly.

