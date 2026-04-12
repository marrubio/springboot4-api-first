# 🔧 REFERENCIA RÁPIDA - Configuración y Comandos

**Para usar durante la implementación: Copiar/pegar directamente**

---

## 📦 DEPENDENCIAS MAVEN a Agregar en `pom.xml`

### Ubicación
En sección `<dependencies>` (dentro de `<project>`):

```xml
<!-- Springdoc OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.2</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.8.2</version>
</dependency>
```

### Ubicación Plugin
En sección `<build><plugins>`:

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.6.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/api/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <packageName>es.marugi.spring.api.generated</packageName>
                <modelPackage>es.marugi.spring.api.generated.model</modelPackage>
                <apiPackage>es.marugi.spring.api.generated.api</apiPackage>
                <configOptions>
                    <useSpringBoot3>true</useSpringBoot3>
                    <delegatePattern>true</delegatePattern>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## ⚙️ PROPIEDADES `application.properties`

### Ubicación
`src/main/resources/application.properties`

```properties
# Springdoc OpenAPI - Endpoints
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# Springdoc OpenAPI - UI Customization
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.show-extensions=true

# Springdoc OpenAPI - Metadata
springdoc.api-docs.title=Game API
springdoc.api-docs.description=API para gestión de juegos
springdoc.api-docs.version=1.0.0

# Springdoc OpenAPI - Security (si aplica)
springdoc.swagger-ui.oauth2-redirect-url=http://localhost:8080/swagger-ui/oauth2-redirect.html
```

---

## 💻 COMANDOS MAVEN CLAVE

```bash
# Limpiar y compilar (GENERA CÓDIGO)
mvn clean compile

# Compilar solo
mvn compile

# Tests
mvn test

# Tests específicos
mvn test -Dtest=GameControllerIntegrationTest
mvn test -Dtest=OnionArchitectureTest

# Build sin tests
mvn clean package -DskipTests

# Ejecutar aplicación
mvn spring-boot:run

# Debug output Maven
mvn clean compile -X

# Limpiar completo
mvn clean
rm -rf target/generated-sources/openapi/
mvn compile
```

---

## 🌐 URLs IMPORTANTES (Post-Implementación)

```
SWAGGER UI
http://localhost:8080/swagger-ui.html
└─ Documentación interactiva de la API

OPENAPI JSON
http://localhost:8080/v3/api-docs
└─ Especificación en formato JSON

OPENAPI YAML
http://localhost:8080/v3/api-docs.yaml
└─ Especificación en formato YAML

HEALTH CHECK
http://localhost:8080/actuator/health
└─ Estado de la aplicación

API ENDPOINTS
http://localhost:8080/api/games
├─ GET: Listar juegos
├─ POST: Crear juego
└─ /{id}
   ├─ GET: Obtener juego
   ├─ PUT: Actualizar juego
   └─ DELETE: Eliminar juego
```

---

## 📁 ESTRUCTURA DE DIRECTORIOS a CREAR

```bash
# Crear directorios de recursos OpenAPI
mkdir -p src/main/resources/api
mkdir -p src/main/resources/api/schemas

# Verificar estructura
tree src/main/resources/api/
# Resultado esperado:
# src/main/resources/api/
# ├── openapi.yaml
# └── schemas/
```

---

## 🎯 ARCHIVOS a CREAR

### 1. `src/main/resources/api/openapi.yaml`
- Copiar contenido completo de: `OPENAPI_SPEC.md`
- 165 líneas YAML
- Contiene: paths, schemas, security, responses

### 2. `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
- Implementa: `GameApiDelegate`
- Inyecta: `GameCommandService`, `GameQueryService`, `GameMapper`
- Métodos: `listGames`, `getGameById`, `createGame`, `updateGame`, `deleteGame`

### 3. `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java` (Opcional)
- Anotar: `@Configuration`
- Bean: `@Bean public OpenAPI customOpenAPI()`
- Configura: metadata de OpenAPI

---

## 🧪 TEMPLATE: GameApiDelegateImpl.java

```java
package es.marugi.spring.api.adapter.in.rest;

import es.marugi.spring.api.generated.api.GameApiDelegate;
import es.marugi.spring.api.generated.model.GameDTO;
import es.marugi.spring.api.generated.model.CreateGameDTO;
import es.marugi.spring.api.generated.model.UpdateGameDTO;
import es.marugi.spring.api.application.service.GameCommandService;
import es.marugi.spring.api.application.service.GameQueryService;
import es.marugi.spring.api.application.mapper.GameMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GameApiDelegateImpl implements GameApiDelegate {
    
    private final GameCommandService commandService;
    private final GameQueryService queryService;
    private final GameMapper mapper;
    
    public GameApiDelegateImpl(GameCommandService commandService, 
                              GameQueryService queryService, 
                              GameMapper mapper) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.mapper = mapper;
    }
    
    @Override
    public ResponseEntity<Void> deleteGame(Long id) {
        commandService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<GameDTO> getGameById(Long id) {
        var game = queryService.getGameById(id);
        return ResponseEntity.ok(mapper.toGameDTO(game));
    }
    
    @Override
    public ResponseEntity<GameDTO> createGame(CreateGameDTO createGameDTO) {
        var game = commandService.createGame(
            createGameDTO.getName(),
            createGameDTO.getDescription(),
            createGameDTO.getReleaseDate()
        );
        return ResponseEntity.status(201).body(mapper.toGameDTO(game));
    }
    
    @Override
    public ResponseEntity<GameDTO> updateGame(Long id, UpdateGameDTO updateGameDTO) {
        var game = commandService.updateGame(
            id,
            updateGameDTO.getName(),
            updateGameDTO.getDescription(),
            updateGameDTO.getReleaseDate()
        );
        return ResponseEntity.ok(mapper.toGameDTO(game));
    }
    
    @Override
    public ResponseEntity<List<GameDTO>> listGames(Integer page, Integer size, String sort) {
        var games = queryService.listGames(page, size, sort);
        return ResponseEntity.ok(games.stream()
            .map(mapper::toGameDTO)
            .toList());
    }
}
```

---

## 🧪 TEMPLATE: SwaggerConfig.java (Opcional)

```java
package es.marugi.spring.api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Game API")
                .version("1.0.0")
                .description("API REST para gestión de juegos")
                .contact(new Contact()
                    .name("Development Team")
                    .email("development@example.com")));
    }
}
```

---

## 📝 VALIDACIÓN: Checklist Rápido

```bash
# ✅ 1. Dependencies instaladas
mvn dependency:tree | grep springdoc

# ✅ 2. openapi.yaml existe
ls src/main/resources/api/openapi.yaml

# ✅ 3. Código generado
mvn clean compile
ls target/generated-sources/openapi/es/marugi/spring/api/generated/model/

# ✅ 4. GameApiDelegateImpl existe
ls src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java

# ✅ 5. Tests pasan
mvn test

# ✅ 6. Swagger UI accesible
mvn spring-boot:run
# Abrir: http://localhost:8080/swagger-ui.html
```

---

## 🚨 TROUBLESHOOTING RÁPIDO

### ❌ Error: "openapi.yaml not found"
```bash
# Solución: Verificar ruta exacta
ls src/main/resources/api/openapi.yaml
# Debe existir el archivo
```

### ❌ Error: "Cannot find symbol: GameApiDelegate"
```bash
# Solución: Regenerar código
mvn clean compile -X
# Verificar que se generó
ls target/generated-sources/openapi/
```

### ❌ Error: "IDE doesn't recognize generated code"
```
Solución: En IntelliJ IDEA
1. File → Project Structure → Modules
2. Seleccionar módulo
3. Pestaña "Sources"
4. Marcar: target/generated-sources/openapi/ como "Generated Sources"
5. Click Apply y OK
```

### ❌ Error: "Port 8080 already in use"
```bash
# Solución: Usar otro puerto
export SERVER_PORT=8081
mvn spring-boot:run
# O en PowerShell:
$env:SERVER_PORT=8081
mvn spring-boot:run
```

### ❌ Error: "YAML syntax error"
```bash
# Solución: Validar en línea
# Ir a: https://editor.swagger.io/
# Copiar contenido de openapi.yaml
# Verifica errores (líneas rojas)
```

### ❌ Tests fallan después de cambios
```bash
# Solución: Limpiar todo y regenerar
mvn clean
rm -rf target/generated-sources/
mvn compile
mvn test
```

---

## 📚 REFERENCIAS RÁPIDAS

```
OpenAPI 3.1 Spec
https://spec.openapis.org/oas/v3.1.0

Swagger Editor (Validar YAML)
https://editor.swagger.io/

Springdoc Documentation
https://springdoc.org/

OpenAPI Generator
https://openapi-generator.tech/

Spectral Linter
https://www.stoplight.io/open-source/spectral
```

---

## ✅ ANTES DE FINALIZAR

```bash
# 1. Compilar
mvn clean compile

# 2. Tests
mvn test

# 3. Ejecutar
mvn spring-boot:run

# 4. Verificar
# - Abrir http://localhost:8080/swagger-ui.html
# - Debe ver todos los endpoints
# - Probar un endpoint: GET /api/games

# 5. Documentación
# - Verificar README.md actualizado
# - Verificar todas las secciones nuevas

# 6. Equipo
# - Capacitar en nuevo workflow
# - Compartir documentación
```

---

## 📞 ACCESO RÁPIDO A DOCUMENTOS

```
Guía rápida (5 min)
→ QUICK_START.md

Plan detallado
→ PLAN_API_FIRST.md

Especificación OpenAPI
→ OPENAPI_SPEC.md

Actualizar README
→ README_UPDATE_GUIDE.md

Checklist imprimible
→ PRINTABLE_CHECKLIST.md

Diagramas visuales
→ VISUAL_DIAGRAMS.md

Una página
→ ONE_PAGE_SUMMARY.md

Índice de documentos
→ INDEX.md
```

---

**Última actualización:** 2026-04-12  
**Versión:** 1.0  
**Status:** ✅ Lista para usar

