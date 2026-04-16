# API Development Guide

This guide describes the workflow for adding or modifying REST endpoints following the **API First** approach used in this project.

## Prerequisites

- Java 25 installed
- Maven wrapper available (`mvnw` / `mvnw.cmd`)
- IDE configured to recognize `target/generated-sources/openapi/` as a generated-sources folder

## Workflow

### Step 1: Edit the OpenAPI Contract

Open `src/main/resources/api/openapi.yaml` and make your changes:

- **New endpoint** — Add a new path under `paths:` with the HTTP method, parameters, request body, and responses.
- **New schema** — Add or update entries under `components > schemas`.
- **Validation rules** — Use OpenAPI constraints (`minLength`, `maxLength`, `minimum`, `pattern`, `required`, etc.) so that Jakarta Bean Validation annotations are generated automatically.

> Tip: Paste the YAML into [Swagger Editor](https://editor.swagger.io/) to validate syntax and preview the documentation before compiling.

### Step 2: Regenerate Code

Run:

```powershell
.\mvnw.cmd clean compile
```

The `openapi-generator-maven-plugin` (phase `generate-sources`) will produce updated files in:

```
target/generated-sources/openapi/es/marugi/spring/api/generated/
├── api/    # V1Api, V1ApiController, V1ApiDelegate
└── model/  # DTOs with validation annotations
```

> **Never edit generated files.** They are overwritten on every build.

### Step 3: Implement the Delegate

The generated `V1ApiDelegate` interface contains default methods that return `501 Not Implemented`. Override the relevant method(s) in:

```
src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java
```

If your new endpoint belongs to a different API tag, create a new `@Component` class that implements the corresponding delegate interface.

**Implementation checklist:**

1. Map the generated DTO to a domain object using `GameRestMapper` (or a new mapper).
2. Call the appropriate application service.
3. Map the result back to the generated response DTO.
4. Return a `ResponseEntity` with the correct HTTP status code.

### Step 4: Write Tests

| Test type | Location | Purpose |
|-----------|----------|---------|
| Unit test | `src/test/java/.../application/service/` | Verify service logic with mocked dependencies |
| Integration test | `src/test/java/.../GameControllerIntegrationTest.java` | Verify HTTP layer end-to-end with `@SpringBootTest` |
| Architecture test | `src/test/java/.../arch/` | Ensure new code respects hexagonal-architecture rules |

Run the full suite:

```powershell
.\mvnw.cmd test -Ptest
```

### Step 5: Verify in Swagger UI

Start the application with the test profile:

```powershell
.\mvnw.cmd "-Dspring-boot.run.useTestClasspath=true" "-Dspring-boot.run.profiles=test" spring-boot:run
```

Then open:

- **Swagger UI:** `http://localhost:8080/api/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/api/api-docs`

Confirm the new endpoint is listed, expand it, and use **Try it out** to send a test request.

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Generated classes not found in IDE | Re-import the Maven project or mark `target/generated-sources/openapi/` as generated sources |
| `501 Not Implemented` at runtime | You forgot to override the delegate method in `GameApiDelegateImpl` |
| Validation not applied | Check that your OpenAPI schema includes the correct constraints and that `spring-boot-starter-validation` is on the classpath |
| Compilation error after changing YAML | Paste the YAML into Swagger Editor to find syntax issues, then run `.\mvnw.cmd clean compile` again |
| MapStruct mapping error | Ensure field names match between generated DTOs and domain objects, or add `@Mapping` annotations |

## Quick Reference

| Action | Command |
|--------|---------|
| Compile + generate | `.\mvnw.cmd clean compile` |
| Run tests | `.\mvnw.cmd test -Ptest` |
| Run architecture tests only | `.\mvnw.cmd test -Dtest=OnionArchitectureTest,NamingConventionTest,ControllerRulesTest,DaoRulesTest -Ptest` |
| Start app (test profile) | `.\mvnw.cmd "-Dspring-boot.run.useTestClasspath=true" "-Dspring-boot.run.profiles=test" spring-boot:run` |
| Validate YAML online | [https://editor.swagger.io/](https://editor.swagger.io/) |

