# ☕ Spring 4 Api First Sample

This module implements a java spring-boot REST API sample using API First paradigm. 
It is built with Spring Boot and provides endpoints for managing sample game entities, authentication via Keycloak, and integration with PostgreSQL or H2 databases.

## 🏗️ Architecture

This project follows **Hexagonal Architecture** (also known as Ports and Adapters) to decouple the core business logic from frameworks, UI, and external agents.
Using the best practices for spring boot development, it ensures maintainability, testability, and scalability.

### 📂 Folder Structure

The code is organized into three main layers:

```
src/main/java/es/marugi/spring/api/
├── adapter/              # Adapters (Interaction with external world)
│   ├── in/               # Driving Adapters (Input)
│   │   └── web/          # REST Controllers
│   └── out/              # Driven Adapters (Output)
│       └── persistence/  # JPA Repositories & Implementations
├── application/          # Application Layer (Orchestration)
│   ├── service/          # Use Case Implementations
│   └── port/             # Input/Output Ports (Optional, if strict)
├── domain/               # Domain Layer (Core Business Logic)
│   ├── model/            # Entities and Value Objects
│   └── repository/       # Repository Interfaces (Ports)
└── infrastructure/       # Framework configuration & cross-cutting concerns
```

### 🧠 Key Concepts

1.  **Domain (`domain/`)**: The heart of the software. It contains enterprise business rules. It has **no dependencies** on frameworks (like Spring or JPA).
  *   **Repository Interfaces**: Defined here (e.g., `UserRepository`), but implemented in the adapter layer.

2.  **Application (`application/`)**: Orchestrates the domain logic to fulfill use cases.
  *   **Services**: Coordinators that handle transactions and direct traffic between the ports and the domain.

3.  **Adapters (`adapter/`)**: The translation layer.
  *   **In (Driving)**: Converts HTTP requests to domain calls (e.g., Rest Controllers).
  *   **Out (Driven)**: Implements domain interfaces to talk to external systems (e.g., `JpaUserRepository` implementation).

## 🎯 API First Development

This project follows the **API First** paradigm: the OpenAPI specification is written before any implementation code, and serves as the single source of truth for the REST API contract.

### How It Works

1. The API contract is defined in **`src/main/resources/api/openapi.yaml`** (OpenAPI 3.1).
2. During the Maven `generate-sources` phase, the **openapi-generator-maven-plugin** produces:
   - DTOs with Jakarta Bean Validation annotations
   - A Spring `@RestController` (`V1ApiController`) that delegates to a `V1ApiDelegate` interface
3. The handwritten class **`GameApiDelegateImpl`** implements `V1ApiDelegate`, maps generated DTOs to domain objects, and calls the application services.

### Generated Code Location

```
target/generated-sources/openapi/
└── es/marugi/spring/api/generated/
    ├── api/        # V1Api, V1ApiController, V1ApiDelegate, ApiUtil
    └── model/      # GameDTO, CreateGameDTO, UpdateGameDTO, ErrorResponse
```

> **Important:** Never edit files under `target/generated-sources/`. They are regenerated on every build.

### Swagger UI

When the application is running, interactive API documentation is available at:

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8080/api/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/api/api-docs` |

To start the app locally with the test profile (H2, no external dependencies):

```powershell
.\mvnw.cmd "-Dspring-boot.run.useTestClasspath=true" "-Dspring-boot.run.profiles=test" spring-boot:run
```

## 📦 Build and Package

Compile and package the application (without running tests):

```bash
mvn clean package -DskipTests
```

**Note:** This project uses Java 25 and includes configuration to handle JDK module access restrictions. The build automatically suppresses deprecation warnings related to `sun.misc.Unsafe` through JVM arguments configured in `.mvn/jvm.config`.

## 🚀 Running the App

```bash
mvn clean spring-boot:run
```


## ⚙️ Maven Profiles

- `default`: Uses PostgreSQL as datasource.
- `test`: Uses H2 in-memory database for testing. Activates `application-test.properties`.

To run tests with the H2 profile:

```bash
mvn test -f backend/pom.xml -Dspring.profiles.active=test
```

## 💻 Running Locally

You must provide database credentials and URL as environment variables. These are not stored in `application.properties` for security reasons.

### 🐘 Running PostgreSQL Locally via Podman (Dev Profile)

To run a local instance of PostgreSQL using **Podman** properly configured for the `dev` profile (`app_user`, `pass123`, `postgres` database, and `app` schema), execute the following commands in your terminal:

```bash
# 1. Start the PostgreSQL container
podman run -d \
  --name local-postgres \
  -e POSTGRES_DB=postgres \
  -e POSTGRES_USER=app_user \
  -e POSTGRES_PASSWORD=pass123 \
  -p 5432:5432 \
  postgres:15

# 2. Wait a few seconds for the database to boot, then create the "app" schema
podman exec -it local-postgres psql -U app_user -d postgres -c "CREATE SCHEMA IF NOT EXISTS app;"
```

### 🔑 Required Environment Variables

- `DB_URL`: JDBC connection URL (e.g. `jdbc:postgresql://localhost:5432/testdb?currentSchema=app`)
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `KC_BASE_URL`: Keycloak server URL

#### Example (PowerShell):
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/testdb?currentSchema=app"
$env:DB_USERNAME="user"
$env:DB_PASSWORD="password"
$env:KC_BASE_URL="https://keycloak/realms/testapp"
mvn spring-boot:run
```

#### Example (Linux/macOS):
```bash
export DB_URL="jdbc:postgresql://localhost:5432/testdb?currentSchema=app"
export DB_USERNAME="user"
export DB_PASSWORD="password"
export KC_BASE_URL="https://keycloak/realms/testapp"
mvn spring-boot:run
```

## 🐳 Docker Image

Build the Docker image using the Dockerfile in folder:

```bash
docker build -t sample-backend:latest -f Dockerfile .
```

Build image using `podman`:

```bash
podman build -t sample-backend:latest -f Dockerfile .
```

Run image locally, exposing port 3000:

```bash
podman run -p 8081:8081 sample-backend:latest
```

## 🌐 CORS Configuration for Deployment

To allow access from different origins (e.g., from the OpenShift frontend route), configure the external property `app.cors.allowed-origins`.

You can do this by adding it to your `application.properties` or as an environment variable:

**application.properties:**
```
app.cors.allowed-origins=http://localhost:3000,https://frontend-mario-rubio-dev.apps.rm1.0a51.p1.openshiftapps.com
```

**Environment Variable (OpenShift):**
```
APP_CORS_ALLOWED_ORIGINS=http://localhost:3000,https://frontend-mario-rubio-dev.apps.rm1.0a51.p1.openshiftapps.com
```

This ensures a "Build Once, Deploy Anywhere" approach without recompiling the backend for different environments.

## 🔒 Security Recommendations

- Never store database credentials in source code or properties files.
- Use environment variables, secrets, or configmaps for sensitive information.
- Limit database user privileges and restrict allowed hosts in PostgreSQL configuration.

## 🧪 Testing

- Tests use the H2 in-memory database and initialize sample data via `data.sql`.
- To verify data initialization, check that the `Game` entity contains at least one record after test startup.

### 📐 Architecture Tests (ArchUnit)

This project integrates **ArchUnit** to ensure the code adheres to the defined Hexagonal Architecture and prevents structural erosion.

**Key rules enforced:**
- **Domain Independence**: The `domain` package must not depend on `adapter`, `application`, or `infrastructure` packages.
- **Layer Direction**: Adapters can depend on Application and Domain, but not vice-versa.
- **Cycle Free**: No cyclic dependencies between packages.
- **Naming Conventions**: Controllers should end with `Controller`, Repositories with `Repository`, etc.

To run these tests specifically:

```bash
mvn test -Dtest=*ArchitectureTest
```

## 📋 Functional Overview

### REST Endpoints

| Method | Path | Auth Required | Description |
|--------|------|:------------:|-------------|
| `GET` | `/api/v1/games` | No | List games (supports `page`, `size`, `sort` params) |
| `GET` | `/api/v1/games/{id}` | No | Get a single game by ID |
| `POST` | `/api/v1/games` | Yes (OAuth2) | Create a new game |
| `PUT` | `/api/v1/games/{id}` | Yes (OAuth2) | Update an existing game |
| `DELETE` | `/api/v1/games/{id}` | Yes (OAuth2) | Delete a game |

> The legacy endpoints at `/api/games` have been removed. All traffic is now served by the API First endpoints above.

### Key Integrations

- **Authentication:** OAuth2 Resource Server via Keycloak. In the `test` profile, security is relaxed to permit all requests.
- **Database:** PostgreSQL for production/dev; H2 in-memory for tests.
- **Validation:** Request payloads are validated automatically via Jakarta Bean Validation constraints defined in the OpenAPI spec.

## 📐 Swagger UI Configuration

Springdoc is configured in `application.properties` (and per-profile overrides) with:

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

### Accessing the Documentation

1. Start the application (see [Running the App](#-running-the-app)).
2. Open **Swagger UI**: `http://localhost:8080/api/swagger-ui/index.html`
3. View raw **OpenAPI JSON**: `http://localhost:8080/api/api-docs`
4. Use the **"Try it out"** button on any endpoint to send requests directly from the browser.

## 🔄 Development Workflow

### Adding a New Endpoint

1. **Edit the contract** — Add the path and schemas in `src/main/resources/api/openapi.yaml`.
2. **Regenerate code** — Run `.\mvnw.cmd clean compile`. New interfaces and DTOs appear in `target/generated-sources/openapi/`.
3. **Implement the delegate** — Add or update the corresponding method in `GameApiDelegateImpl` (or create a new delegate for a different API tag).
4. **Write tests** — Add unit tests for the service layer and integration tests for the endpoint.
5. **Verify in Swagger UI** — Start the app and confirm the new endpoint is visible and functional.

## 🔧 Java 25 Compatibility

This project uses **Java 25**, which has stricter module access controls. The following configurations have been implemented to ensure clean builds without warnings:

### Changes Made:
1. **Updated Dependencies:**
   - ArchUnit: 1.5.0 (improved Java 25 support)
   - MapStruct: 1.6.3 (improved Java 25 support)
   - Maven Compiler Plugin: 3.13.0 (Java 25 support)
   - Surefire/Failsafe: 3.2.5 (Java 25 support)

2. **JVM Configuration (`.mvn/jvm.config`):**
   - `--add-opens java.base/sun.misc=ALL-UNNAMED`: Allows Guice to access internal sun.misc.Unsafe APIs
   - `--add-opens java.base/java.lang.invoke=ALL-UNNAMED`: Allows reflection on java.lang.invoke module
   - `-Dfile.encoding=UTF-8`: Ensures consistent UTF-8 encoding across platforms

3. **Maven Configuration:**
   - Maven Compiler Plugin configured with explicit source/target/release set to 25
   - Surefire and Failsafe plugins configured with module access options
   - Both build and test profiles include JVM arguments to suppress Unsafe deprecation warnings

This configuration eliminates the following warnings that appeared during build:
```
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::staticFieldBase has been called by com.google.inject.internal.aop.HiddenClassDefiner
```

## 📝 Contribution & Documentation Guidelines

- All source code, comments, and documentation must be written in **English**.
- Any change related to configuration or startup procedures in the backend must be documented in this `README.md`.
- Follow **Clean Code** and **Clean Architecture** principles for all backend code.

## 📧 Contact

For questions or support, contact the repository maintainer.

## 📚 References & Further Reading

- [OpenAPI 3.1 Specification](https://spec.openapis.org/oas/v3.1.0)
- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Generator Maven Plugin](https://openapi-generator.tech/docs/plugins/maven/)
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/reference/)
- [Swagger Editor (online validator)](https://editor.swagger.io/)

