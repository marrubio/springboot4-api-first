# Guía Rápida - API First en 5 Minutos

> **Resumen ultra-rápido** del plan de implementación para los que no tienen mucho tiempo.

---

## 🎯 En Línea Recta

```
1️⃣ Agregar dependencias en pom.xml
   ↓
2️⃣ Crear openapi.yaml en src/main/resources/api/
   ↓
3️⃣ Ejecutar: mvn clean compile
   ↓
4️⃣ Implementar GameApiDelegateImpl.java
   ↓
5️⃣ Actualizar tests y validar
   ↓
6️⃣ Acceder a Swagger en http://localhost:8080/swagger-ui.html
```

**Tiempo Total:** 3-4 horas

---

## 📦 Paso 1: Dependencias (15 minutos)

### Agregar a `pom.xml` en `<dependencies>`:

```xml
<!-- Swagger/OpenAPI -->
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

### Agregar a `<build><plugins>`:

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

### Comando de validación:
```bash
mvn clean package -DskipTests   # Verificar que compila
```

---

## 📋 Paso 2: Crear OpenAPI Contract (30 minutos)

### Crear directorio:
```bash
mkdir -p src/main/resources/api
```

### Crear archivo: `src/main/resources/api/openapi.yaml`

→ **Copiar contenido completo de:** `OPENAPI_SPEC.md` (sección "Contenido de openapi.yaml")

### Validar (opcional):
```bash
# Online: https://editor.swagger.io/
# O instalar: npm install -g @stoplight/spectral && spectral lint src/main/resources/api/openapi.yaml
```

---

## ⚙️ Paso 3: Generar Código (10 minutos)

```bash
mvn clean compile
```

**Verificar que se generó:**
```bash
ls target/generated-sources/openapi/es/marugi/spring/api/generated/
```

Deberías ver:
- `model/` → GameDTO.java, CreateGameDTO.java, UpdateGameDTO.java
- `api/` → GameApi.java, GameApiDelegate.java

---

## 💻 Paso 4: Implementar Delegado (60-90 minutos)

### Crear: `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`

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

## 🧪 Paso 5: Validar (30-45 minutos)

```bash
# Compilar
mvn clean compile

# Tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

---

## 🌐 Paso 6: Usar Swagger UI (5 minutos)

1. Abrir navegador: **http://localhost:8080/swagger-ui.html**

2. Ver documentación completa de la API

3. Click en "Try it out" para probar endpoints

---

## 📝 Paso 7: Documentación (30-45 minutos)

Actualizar `README.md` usando: `README_UPDATE_GUIDE.md`

**Secciones principales a agregar:**
- API First Development
- Swagger UI Configuration  
- Development Workflow
- Referencias

---

## ⚡ Comandos Rápidos

```bash
# Limpiar y compilar (genera código)
mvn clean compile

# Tests
mvn test

# Ejecutar app
mvn spring-boot:run

# Validar OpenAPI (online)
# → https://editor.swagger.io/

# Acceder Swagger
# → http://localhost:8080/swagger-ui.html

# Ver contrato JSON
# → http://localhost:8080/v3/api-docs

# Ver contrato YAML
# → http://localhost:8080/v3/api-docs.yaml
```

---

## 🚨 Troubleshooting Rápido

### ❌ "openapi.yaml no encontrado"
→ Verificar path: `src/main/resources/api/openapi.yaml`

### ❌ "Código no generado"
```bash
mvn clean compile -X  # Ver detalles
```

### ❌ "IDE no reconoce código generado"
→ Marcar `target/generated-sources/openapi/` como Generated Sources en IDE

### ❌ "Swagger UI no aparece"
→ Verificar properties: `springdoc.swagger-ui.enabled=true`

### ❌ "Error en compilación de YAML"
→ Usar https://editor.swagger.io/ para validar sintaxis

---

## 📚 Documentos de Referencia

| Documento | Propósito |
|-----------|-----------|
| **PLAN_API_FIRST.md** | Plan detallado (7 fases, 15+ tareas) |
| **OPENAPI_SPEC.md** | Especificación OpenAPI completa |
| **README_UPDATE_GUIDE.md** | Cómo actualizar README |
| **IMPLEMENTATION_STATUS.md** | Checklist y status |
| **QUICK_START.md** | ⬅️ Este documento |

---

## ✅ Checklist Final

- [ ] ✅ Dependencias agregadas en pom.xml
- [ ] ✅ openapi.yaml creado en src/main/resources/api/
- [ ] ✅ mvn clean compile ejecutado sin errores
- [ ] ✅ Código generado en target/generated-sources/openapi/
- [ ] ✅ GameApiDelegateImpl implementado
- [ ] ✅ Tests pasando
- [ ] ✅ Aplicación ejecutándose
- [ ] ✅ Swagger UI accesible en http://localhost:8080/swagger-ui.html
- [ ] ✅ README actualizado

---

## 🎓 Próximo Paso: Agregar Nuevos Endpoints

Una vez implementado API First:

```
1. Editar openapi.yaml (agregar endpoint)
2. mvn clean compile (generar código)
3. Implementar en GameApiDelegateImpl
4. Escribir tests
5. Acceder a Swagger UI
```

¡Eso es todo! 🚀

---

**Última actualización:** 2026-04-12  
**Para detalles completos:** Consulta **PLAN_API_FIRST.md**

