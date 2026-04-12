# Guía de Actualización del README.md

Este documento proporciona las secciones a agregar/actualizar en el README.md del proyecto para documentar la implementación del paradigma API First.

## Secciones a Agregar

### 1. Sección: "API First Development" (después de la sección de Arquitectura)

```markdown
## 🎯 API First Development

This project follows the **API First** paradigm, where the OpenAPI contract is defined first, and the implementation follows the contract. This ensures:

- **Contract-Driven Development**: The API contract is the single source of truth
- **Consistency**: Generated code ensures consistency with the defined specification
- **Documentation**: The OpenAPI specification automatically generates interactive documentation
- **Testability**: Contract-based testing ensures compliance with the specification

### OpenAPI Contract

The API contract is defined in:
```
src/main/resources/api/openapi.yaml
```

This file contains:
- Complete API specification (OpenAPI 3.1)
- All endpoints (GET, POST, PUT, DELETE)
- Request/response schemas
- Error definitions
- Security configuration (OAuth2)

### Auto-Generated Code

The following code is automatically generated from the OpenAPI contract:

```
target/generated-sources/openapi/
├── es/
│   └── marugi/
│       └── spring/
│           └── api/
│               └── generated/
│                   ├── model/
│                   │   ├── GameDTO.java
│                   │   ├── CreateGameDTO.java
│                   │   ├── UpdateGameDTO.java
│                   │   └── ErrorResponse.java
│                   └── api/
│                       ├── GameApi.java
│                       └── GameApiDelegate.java
```

Generation occurs automatically during `mvn clean compile`.

### Interactive API Documentation

Once the application is running, access the interactive Swagger UI documentation:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification (in JSON format) is available at:

```
http://localhost:8080/v3/api-docs
```

### Modifying the API Contract

To add or modify endpoints:

1. **Edit the OpenAPI specification** (`src/main/resources/api/openapi.yaml`)
2. **Regenerate code** with `mvn clean compile`
3. **Implement the generated delegate** in `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
4. **Update tests** to match the new contract
5. **Verify in Swagger UI** at http://localhost:8080/swagger-ui.html

See [PLAN_API_FIRST.md](PLAN_API_FIRST.md) for detailed implementation steps.
```

### 2. Sección: "📐 Swagger UI Configuration" (después de Testing)

```markdown
## 📐 Swagger UI Configuration

The project includes **Springdoc OpenAPI** for automatic API documentation generation.

### Configuration Properties

The following properties configure Swagger UI behavior:

**application.properties:**
```properties
# Swagger UI Endpoints
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# UI Customization
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.show-extensions=true

# Security Configuration
springdoc.swagger-ui.oauth2-redirect-url=http://localhost:8080/swagger-ui/oauth2-redirect.html
```

### Accessing Swagger UI

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Open Swagger UI in browser:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **View raw OpenAPI specification (JSON):**
   ```
   http://localhost:8080/v3/api-docs
   ```

4. **View raw OpenAPI specification (YAML):**
   ```
   http://localhost:8080/v3/api-docs.yaml
   ```

### Testing APIs from Swagger UI

1. Authenticate using OAuth2 (if configured in Keycloak)
2. Click "Try it out" on any endpoint
3. Fill in the parameters/body
4. Click "Execute"
5. View the response

### Customizing Swagger UI

To customize Swagger UI appearance and behavior, edit `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java`:

```java
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Game API")
                .version("1.0.0")
                .description("API para gestión de juegos")
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@example.com")));
    }
}
```
```

### 3. Actualizar sección "Functional Overview"

```markdown
## 📋 Functional Overview

The API provides the following functionality:

### Game Management
- **List Games**: GET `/api/games` - Retrieve paginated list of all games
- **Get Game**: GET `/api/games/{id}` - Retrieve a specific game by ID
- **Create Game**: POST `/api/games` - Create a new game
- **Update Game**: PUT `/api/games/{id}` - Update an existing game
- **Delete Game**: DELETE `/api/games/{id}` - Delete a game

### Authentication & Authorization
- Authentication via Keycloak (OAuth2)
- Scoped access (read:games, write:games, delete:games)
- Token validation on protected endpoints

### Database Integration
- PostgreSQL for production
- H2 in-memory database for testing
- JPA/Hibernate ORM

### API Documentation
- Interactive Swagger UI at `/swagger-ui.html`
- Auto-generated from OpenAPI 3.1 specification
- Real-time endpoint testing
```

### 4. Agregar nueva sección: "🔄 Development Workflow"

```markdown
## 🔄 Development Workflow

### Adding a New Endpoint

1. **Update OpenAPI Specification**
   - Edit `src/main/resources/api/openapi.yaml`
   - Add new path, operation, and schemas
   - Follow OpenAPI 3.1 specification

2. **Generate Code**
   ```bash
   mvn clean compile
   ```
   - New DTOs and interfaces are generated in `target/generated-sources/openapi/`

3. **Implement Delegate**
   - Edit `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
   - Implement the new method from generated interface
   - Use existing services to handle business logic

4. **Add Tests**
   - Update `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`
   - Test the new endpoint with various scenarios

5. **Verify**
   ```bash
   mvn test                                    # Run all tests
   mvn spring-boot:run                         # Start the application
   ```
   - Navigate to `http://localhost:8080/swagger-ui.html`
   - Test the endpoint using Swagger UI

### Modifying Existing Endpoints

1. Update the endpoint definition in `openapi.yaml`
2. Run `mvn clean compile` to regenerate code
3. Update the implementation in `GameApiDelegateImpl`
4. Update corresponding tests
5. Run `mvn test` to validate changes

### Code Generation Troubleshooting

If code generation fails:

1. **Validate OpenAPI specification:**
   ```bash
   npm install -g @stoplight/spectral
   spectral lint src/main/resources/api/openapi.yaml
   ```

2. **Check Maven output:**
   ```bash
   mvn clean compile -X  # Enable debug output
   ```

3. **Verify plugin configuration** in `pom.xml`
   - Check plugin version
   - Verify input spec path
   - Ensure package names are correct

4. **Clean and rebuild:**
   ```bash
   mvn clean
   rm -rf target/generated-sources/openapi/
   mvn compile
   ```
```

### 5. Agregar sección: "📚 References & Further Reading"

```markdown
## 📚 References & Further Reading

### API First & OpenAPI
- [OpenAPI 3.1 Specification](https://spec.openapis.org/oas/v3.1.0)
- [API First Design Best Practices](https://swagger.io/resources/articles/best-practices-in-api-first-design/)
- [Swagger Editor](https://editor.swagger.io/) - Online OpenAPI editor

### Tools & Libraries
- [Springdoc OpenAPI](https://springdoc.org/) - Auto-generate OpenAPI from Spring annotations
- [OpenAPI Generator](https://openapi-generator.tech/) - Generate code from OpenAPI specifications
- [Swagger UI](https://swagger.io/tools/swagger-ui/) - Interactive API documentation

### Spring Boot & Java
- [Spring Boot 4.0 Documentation](https://spring.io/projects/spring-boot)
- [Spring Security with OAuth2](https://spring.io/guides/topicals/spring-security-architecture/)
- [MapStruct Documentation](https://mapstruct.org/) - Object mapping

### Architecture
- [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
```

## Plan de Actualización del README

1. **Insertar** la sección "API First Development" después de "🏗️ Architecture" (línea 30)
2. **Insertar** la sección "Swagger UI Configuration" después de "📋 Functional Overview" (línea 173)
3. **Reemplazar** la sección "📋 Functional Overview" con la nueva versión mejorada
4. **Agregar** la sección "🔄 Development Workflow" (antes de "🔧 Java 25 Compatibility")
5. **Agregar** la sección "📚 References & Further Reading" al final (antes de "📧 Contact")

## Cambios de Contenido

### Cambios en la Descripción Principal

**Actual (líneas 1-4):**
```markdown
This module implements a java spring-boot REST API sample using API First paradigm. 
```

**Puede mantenerse igual** ya que ya menciona "API First", pero agregar más contexto:

```markdown
This module implements a Java Spring Boot REST API sample using the **API First** paradigm.
The API contract is defined first in an OpenAPI 3.1 specification, from which code is automatically generated.
Implementation and documentation are always aligned with the contract.
```

## Validación

Después de actualizar el README, validar:

1. ✅ Todos los enlaces son correctos
2. ✅ Los ejemplos de comandos funcionan
3. ✅ Las rutas de archivos son correctas
4. ✅ La estructura está bien formateada
5. ✅ El contenido es preciso y relevante

---

**Última actualización:** 2026-04-12
**Estado:** Listo para implementación manual en README.md

