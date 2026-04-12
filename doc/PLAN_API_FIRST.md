# Plan: Implementación de Paradigma API First con OpenAPI

**Versión:** 1.0  
**Fecha:** 2026-04-12  
**Objetivo:** Migrar el proyecto a un paradigma "API First" definiendo un contrato OpenAPI 3.1, generando código automáticamente desde el contrato e integrando Swagger UI para documentación interactiva.

---

## 📋 Resumen Ejecutivo

Este plan define los pasos necesarios para implementar el paradigma "API First" en el proyecto actual. Se requiere:

1. Definir especificación OpenAPI 3.1 como fuente de verdad
2. Generar DTOs y controladores desde el contrato
3. Integrar Swagger UI para visualización de documentación
4. Adaptar la arquitectura hexagonal existente
5. Actualizar documentación del proyecto

**Tiempo estimado:** 4-6 horas  
**Complejidad:** Media (migraciones no son disruptivas)

---

## 🏗️ Arquitectura Actual

```
Adapter/In (REST)
    ↓
Application (DTOs, Services, Mappers)
    ↓
Domain (Modelos de negocio)
    ↓
Infrastructure (Config, Persistencia)
```

**Características existentes:**
- Spring Boot 4.0.2 con Java 25
- Arquitectura hexagonal (adapter/in, adapter/out, application, domain, infrastructure)
- DTOs manuales para Game CRUD
- MapStruct para mapeo DTO ↔ Entity
- Spring Security + OAuth2 (Keycloak)
- PostgreSQL + H2 para testing
- ArchUnit para validación de arquitectura

---

## 📝 Plan de Implementación

### **Fase 1: Preparación del Proyecto**

#### Tarea 1.1: Agregar Dependencias al `pom.xml`

**Descripción:** Añadir librerías necesarias para OpenAPI y generación de código.

**Dependencias a agregar:**

```xml
<!-- Springdoc OpenAPI (genera OpenAPI desde anotaciones) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.2</version>
</dependency>

<!-- Soporte para esquemas OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
    <version>2.8.2</version>
</dependency>
```

**Plugins Maven a agregar:**

```xml
<!-- OpenAPI Generator Maven Plugin (genera DTOs desde OpenAPI) -->
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
                    <java8>false</java8>
                    <delegatePattern>true</delegatePattern>
                    <documentationProvider>springfox</documentationProvider>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Archivos afectados:** `pom.xml`

---

#### Tarea 1.2: Crear Estructura de Directorios

**Descripción:** Crear directorio para almacenar la especificación OpenAPI.

```
src/
  main/
    resources/
      api/
        openapi.yaml         ← Contrato OpenAPI (nueva)
        schemas/             ← Esquemas reutilizables (opcional)
```

**Archivos afectados:** Crear directorio `src/main/resources/api/`

---

### **Fase 2: Definir Contrato OpenAPI**

#### Tarea 2.1: Crear Especificación OpenAPI 3.1 (`openapi.yaml`)

**Descripción:** Definir contrato completo de la API incluyendo:
- Información general (título, versión, descripción)
- Paths (endpoints) para Game CRUD
- Esquemas de datos (Game, CreateGameDTO, UpdateGameDTO)
- Respuestas de error estándar
- Seguridad OAuth2

**Contenido:** Ver sección **Apéndice A**

**Archivos afectados:** `src/main/resources/api/openapi.yaml` (nuevo)

---

#### Tarea 2.2: Validar Especificación OpenAPI

**Descripción:** Validar que la especificación es correcta usando herramientas online o locales.

**Herramientas recomendadas:**
- Swagger Editor: https://editor.swagger.io/
- OpenAPI Linter: `npm install -g @stoplight/spectral`

**Comando:**
```bash
spectral lint src/main/resources/api/openapi.yaml
```

---

### **Fase 3: Configurar Generación de Código**

#### Tarea 3.1: Generar Código desde OpenAPI

**Descripción:** Ejecutar Maven para generar DTOs, interfaces y validadores desde el contrato OpenAPI.

**Comando:**
```bash
mvn clean compile
```

**Resultado esperado:**
```
target/
  generated-sources/
    openapi/
      es/
        marugi/
          spring/
            api/
              generated/
                model/
                  Game.java
                  CreateGameDTO.java
                  UpdateGameDTO.java
                api/
                  GameApiDelegate.java
                  GameApi.java
```

**Archivos afectados:** Generación automática en `target/generated-sources/openapi/`

---

#### Tarea 3.2: Configurar IDE para Reconocer Fuentes Generadas

**Descripción:** Asegurar que el IDE (IntelliJ IDEA) reconoce las fuentes generadas.

**Para IntelliJ IDEA:**
1. Ir a: `File → Project Structure → Modules`
2. Seleccionar módulo del proyecto
3. Ir a pestaña `Sources`
4. Marcar `target/generated-sources/openapi/` como `Generated Sources`

**Para Eclipse:**
1. Click derecho en carpeta `target/generated-sources/openapi/`
2. `Build Path → Use as Source Folder`

**Archivos afectados:** Configuración IDE (no requiere cambios en código)

---

### **Fase 4: Adaptar Estructura del Proyecto**

#### Tarea 4.1: Actualizar DTOs en Capa de Aplicación

**Descripción:** Reemplazar DTOs manuales con DTOs generados desde OpenAPI.

**Cambios:**
- Importar `CreateGameDTO` desde `es.marugi.spring.api.generated.model`
- Importar `UpdateGameDTO` desde `es.marugi.spring.api.generated.model`
- Importar `GameDTO` desde `es.marugi.spring.api.generated.model`

**Archivos afectados:**
- `src/main/java/es/marugi/spring/api/application/dto/CreateGameDTO.java` (depender del generado)
- `src/main/java/es/marugi/spring/api/application/dto/UpdateGameDTO.java` (depender del generado)
- `src/main/java/es/marugi/spring/api/application/dto/GameDTO.java` (depender del generado)

**Estrategia:** 
- Opción A: Reemplazar completamente (si coinciden perfectamente)
- Opción B: Crear alias/imports de los generados (migrración gradual)
- Opción C: Mantener DTOs y sincronizar manualmente

**Recomendación:** Opción B (alias temporal) para validar que todo funciona antes de eliminar DTOs viejos.

---

#### Tarea 4.2: Crear Delegados para API Generada

**Descripción:** Implementar interfaz `GameApiDelegate` generada que conecte con los servicios existentes.

**Archivo nuevo:** `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`

**Responsabilidades:**
- Implementar métodos generados desde OpenAPI
- Inyectar servicios de aplicación (`GameCommandService`, `GameQueryService`)
- Delegar lógica a servicios existentes
- Mapear entre DTOs generados y entidades de dominio

**Ejemplo de estructura:**
```java
@Component
public class GameApiDelegateImpl implements GameApiDelegate {
    
    private final GameCommandService commandService;
    private final GameQueryService queryService;
    private final GameMapper mapper;
    
    @Override
    public ResponseEntity<GameDTO> createGame(CreateGameDTO createGameDTO) {
        // Mapear DTO a entidad
        // Llamar a comando de aplicación
        // Mapear resultado a DTO generado
        // Retornar ResponseEntity
    }
    
    // ... resto de métodos
}
```

**Archivos afectados:** 
- Crear: `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`

---

#### Tarea 4.3: Adaptar Controladores Existentes (si es necesario)

**Descripción:** Si existen controladores REST manuales, reemplazarlos con controladores generados o crear un adaptador.

**Opción A:** Usar controlador generado + delegado (recomendado)
**Opción B:** Mantener controlador actual y anotar con `@OpenAPI*` para que Springdoc genere la especificación

**Archivos afectados:**
- Revisar: `src/main/java/es/marugi/spring/api/adapter/in/rest/`

---

#### Tarea 4.4: Actualizar Mappers (MapStruct)

**Descripción:** Asegurar que MapStruct mapea correctamente entre DTOs generados y entidades de dominio.

**Cambios:**
- Verificar que `GameMapper` maneja DTOs generados
- Agregar métodos adicionales si es necesario
- Ejecutar `mvn clean compile` para regenerar implementaciones

**Archivos afectados:**
- `src/main/java/es/marugi/spring/api/application/mapper/GameMapper.java`

---

### **Fase 5: Configurar Swagger UI**

#### Tarea 5.1: Configurar Propiedades de Springdoc

**Descripción:** Configurar propiedades en `application.properties` para Swagger UI.

**Archivo:** `src/main/resources/application.properties`

**Propiedades a agregar:**
```properties
# Springdoc OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.show-extensions=true
springdoc.show-actuator=false

# Información de la API
springdoc.api-docs.title=Game API
springdoc.api-docs.description=API para gestión de juegos
springdoc.api-docs.version=1.0.0

# Seguridad OAuth2 (si aplica)
springdoc.swagger-ui.oauth2-redirect-url=http://localhost:8080/swagger-ui/oauth2-redirect.html
```

**Archivos afectados:**
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`
- `src/main/resources/application-test.properties`

---

#### Tarea 5.2: Crear Configuración Personalizada (opcional)

**Descripción:** Crear clase `@Configuration` para personalizar Swagger UI y documentación OpenAPI.

**Archivo nuevo:** `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java`

**Responsabilidades:**
- Definir metadata de la API (título, descripción, contacto)
- Configurar esquemas de seguridad OAuth2
- Personalizar apariencia de Swagger UI

**Ejemplo:**
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
                    .name("Equipo de Desarrollo")
                    .email("dev@example.com")));
    }
}
```

**Archivos afectados:**
- Crear: `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java`

---

#### Tarea 5.3: Validar Acceso a Swagger UI

**Descripción:** Verificar que Swagger UI es accesible en http://localhost:8080/swagger-ui.html

**Pasos:**
1. Ejecutar aplicación: `mvn spring-boot:run`
2. Navegar a: http://localhost:8080/swagger-ui.html
3. Verificar que la documentación interactiva se carga correctamente
4. Probar endpoints desde Swagger UI

**Archivos afectados:** Ninguno (validación manual)

---

### **Fase 6: Testing y Validación**

#### Tarea 6.1: Actualizar Tests de Controladores

**Descripción:** Actualizar tests de integración para usar DTOs generados y validar endpoints.

**Archivos afectados:**
- `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`
- `src/test/java/es/marugi/spring/api/SecurityIntegrationTest.java`

**Cambios:**
- Importar DTOs generados en lugar de manuales
- Usar `TestRestTemplate` o `WebTestClient` para validar endpoints
- Validar códigos de estado HTTP, headers, payloads

---

#### Tarea 6.2: Validar Reglas de Arquitectura (ArchUnit)

**Descripción:** Ejecutar tests de ArchUnit para asegurar que la nueva estructura mantiene la arquitectura hexagonal.

**Comando:**
```bash
mvn test -Dtest=OnionArchitectureTest
```

**Archivos afectados:**
- `src/test/java/es/marugi/spring/api/arch/OnionArchitectureTest.java`

---

#### Tarea 6.3: Ejecutar Suite Completa de Tests

**Descripción:** Ejecutar toda la suite de tests para validar que no hay regresiones.

**Comando:**
```bash
mvn clean test
```

**Archivos afectados:** Ninguno (validación manual)

---

### **Fase 7: Documentación**

#### Tarea 7.1: Actualizar README.md

**Descripción:** Actualizar documentación del proyecto con secciones sobre API First, OpenAPI y Swagger UI.

**Secciones a agregar/actualizar:**
1. Descripción general del paradigma API First
2. Ubicación del contrato OpenAPI
3. Cómo acceder a Swagger UI
4. Instrucciones para modificar el contrato
5. Proceso de regeneración de código
6. Estructura de directorios
7. Guía de desarrollo para nuevos endpoints

**Archivo:** `README.md`

---

#### Tarea 7.2: Crear Documentación Complementaria (opcional)

**Descripción:** Crear documentos adicionales para guiar desarrollo futuro.

**Archivos opcionales:**
- `ARCHITECTURE.md` - Descripción detallada de la arquitectura
- `CONTRIBUTING.md` - Guía para contribuidores
- `API_DEVELOPMENT.md` - Guía para desarrollar nuevos endpoints

---

## 🔄 Flujo de Trabajo Post-Implementación

### Para Agregar un Nuevo Endpoint

1. **Actualizar `openapi.yaml`** con nueva operación, esquemas y parámetros
2. **Ejecutar `mvn clean compile`** para generar código
3. **Implementar `GameApiDelegateImpl`** (o delegado correspondiente)
4. **Escribir tests** en `GameControllerIntegrationTest`
5. **Ejecutar `mvn test`** para validar
6. **Acceder a Swagger UI** en http://localhost:8080/swagger-ui.html para visualizar

### Para Modificar un Endpoint Existente

1. **Actualizar esquema/operación en `openapi.yaml`**
2. **Ejecutar `mvn clean compile`** para regenerar código
3. **Actualizar implementación en delegado**
4. **Actualizar tests**
5. **Verificar en Swagger UI**

---

## 📊 Dependencias Entre Tareas

```
Tarea 1.1 (Dependencias) → Tarea 3.1 (Generación)
Tarea 1.2 (Directorios) → Tarea 2.1 (OpenAPI)
Tarea 2.1 (OpenAPI) → Tarea 3.1 (Generación)
Tarea 3.1 (Generación) → Tarea 4.1 (DTOs)
Tarea 4.1 (DTOs) → Tarea 4.2 (Delegados)
Tarea 4.2 (Delegados) → Tarea 5.3 (Validar)
Tarea 5.1 (Propiedades) → Tarea 5.3 (Validar)
Tarea 5.2 (Config) → Tarea 5.3 (Validar) [opcional]
Tarea 6.1 (Tests) → Suite completa
Tarea 7.1 (README) → Finalización
```

---

## ✅ Criterios de Éxito

- [ ] Especificación OpenAPI 3.1 definida y validada
- [ ] Código generado sin errores desde `openapi.yaml`
- [ ] Swagger UI accesible en http://localhost:8080/swagger-ui.html
- [ ] DTOs generados integrados en capas de aplicación
- [ ] Delegados implementados y funcionando
- [ ] Todos los tests pasando (unit, integración, arquitectura)
- [ ] README actualizado con documentación completa
- [ ] Flujo de trabajo API First documentado y validado

---

## 🚀 Próximos Pasos (Post-Implementación)

1. **Documentar decisiones arquitectónicas** en ADR (Architecture Decision Records)
2. **Considerar API Versioning** (v1, v2) si planea cambios breaking en el futuro
3. **Integrar con CI/CD** para validar OpenAPI en cada build
4. **Considerar Mock Server** generado desde OpenAPI para desarrollo paralelo de frontend/backend
5. **Documentar cambios** en contrato OpenAPI en CHANGELOG

---

## 📚 Apéndice A: Especificación OpenAPI 3.1 (`openapi.yaml`)

Ver archivo: `src/main/resources/api/openapi.yaml`

**Contenido:**
- Info (título, versión, descripción)
- Servers (producción, desarrollo local)
- Paths (GET, POST, PUT, DELETE para Games)
- Componentes (esquemas, respuestas de error, seguridad)
- Seguridad OAuth2

---

## 📚 Apéndice B: Referencias y Recursos

### Documentación oficial
- OpenAPI 3.1 Spec: https://spec.openapis.org/oas/v3.1.0
- Springdoc OpenAPI: https://springdoc.org/
- OpenAPI Generator: https://openapi-generator.tech/
- MapStruct: https://mapstruct.org/

### Herramientas útiles
- Swagger Editor: https://editor.swagger.io/
- Spectral Linter: https://www.stoplight.io/open-source/spectral
- Swagger UI: https://swagger.io/tools/swagger-ui/

### Tutoriales relacionados
- Spring Boot + OpenAPI: https://www.baeldung.com/spring-boot-openapi
- API First Development: https://swagger.io/resources/articles/best-practices-in-api-first-design/

---

**Documento preparado por:** Sistema de Planificación Automatizado  
**Última actualización:** 2026-04-12  
**Estado:** Listo para implementación

