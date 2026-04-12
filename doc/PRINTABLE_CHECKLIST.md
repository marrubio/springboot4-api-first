# ✅ CHECKLIST IMPRIMIBLE - Plan API First

**Proyecto:** Spring4 API First Sample  
**Fecha Inicio:** _______________  
**Fecha Fin:** _______________  
**Responsable:** _______________

---

## 📋 FASE 1: PREPARACIÓN (15-20 minutos)

### Tarea 1.1: Agregar Dependencias Maven

- [ ] Abrir `pom.xml`
- [ ] Agregar dependencia `springdoc-openapi-starter-webmvc-ui` (v2.8.2)
- [ ] Agregar dependencia `springdoc-openapi-starter-webmvc-api` (v2.8.2)
- [ ] Agregar plugin `openapi-generator-maven-plugin` (v7.6.0)
- [ ] Guardar `pom.xml`
- [ ] Ejecutar: `mvn clean package -DskipTests`
- [ ] ✅ **Validar:** Build exitoso sin errores

**Fecha completada:** _______________

---

### Tarea 1.2: Crear Estructura de Directorios

- [ ] Crear directorio: `src/main/resources/api/`
- [ ] Crear directorio: `src/main/resources/api/schemas/` (opcional)
- [ ] Verificar que directorios existen
- [ ] ✅ **Validar:** Directorios visibles en explorador

**Fecha completada:** _______________

---

## 📋 FASE 2: ESPECIFICACIÓN OpenAPI (20-30 minutos)

### Tarea 2.1: Crear openapi.yaml

- [ ] Crear archivo: `src/main/resources/api/openapi.yaml`
- [ ] Copiar contenido de `OPENAPI_SPEC.md`
- [ ] Verificar que archivo contiene 165 líneas YAML
- [ ] Guardar archivo
- [ ] ✅ **Validar:** Archivo existe y es legible

**Fecha completada:** _______________

---

### Tarea 2.2: Validar Especificación OpenAPI

- [ ] Ir a: https://editor.swagger.io/
- [ ] Abrir `src/main/resources/api/openapi.yaml`
- [ ] Pegar contenido en editor
- [ ] Verificar que no hay errores en rojo
- [ ] ✅ **Validar:** YAML válido sin errores

**Fecha completada:** _______________

---

## 📋 FASE 3: GENERACIÓN DE CÓDIGO (10-15 minutos)

### Tarea 3.1: Ejecutar Generación

- [ ] Abrir terminal/PowerShell
- [ ] Navegar a raíz del proyecto
- [ ] Ejecutar: `mvn clean compile`
- [ ] Esperar a que termine
- [ ] ✅ **Validar:** BUILD SUCCESS sin errores

**Fecha completada:** _______________

---

### Tarea 3.2: Verificar Código Generado

- [ ] Navegar a: `target/generated-sources/openapi/`
- [ ] Verificar estructura:
  - [ ] Carpeta `es/marugi/spring/api/generated/model/`
  - [ ] Carpeta `es/marugi/spring/api/generated/api/`
- [ ] Verificar archivos generados:
  - [ ] `GameDTO.java`
  - [ ] `CreateGameDTO.java`
  - [ ] `UpdateGameDTO.java`
  - [ ] `GameApi.java`
  - [ ] `GameApiDelegate.java`
- [ ] ✅ **Validar:** Todos los archivos presentes

**Fecha completada:** _______________

---

### Tarea 3.3: Configurar IDE

**Para IntelliJ IDEA:**
- [ ] Ir a: `File → Project Structure → Modules`
- [ ] Seleccionar módulo del proyecto
- [ ] Ir a pestaña `Sources`
- [ ] Marcar: `target/generated-sources/openapi/` como `Generated Sources`
- [ ] Click `Apply` y `OK`
- [ ] ✅ **Validar:** IDE reconoce código generado (sin errores rojos)

**Para Eclipse:**
- [ ] Click derecho en: `target/generated-sources/openapi/`
- [ ] `Build Path → Use as Source Folder`
- [ ] ✅ **Validar:** Carpeta marcada en rojo

**Fecha completada:** _______________

---

## 📋 FASE 4: IMPLEMENTACIÓN (60-90 minutos)

### Tarea 4.1: Crear GameApiDelegateImpl

- [ ] Crear archivo: `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
- [ ] Implementar clase `GameApiDelegateImpl` que:
  - [ ] Implementa `GameApiDelegate` (generada)
  - [ ] Inyecta `GameCommandService`
  - [ ] Inyecta `GameQueryService`
  - [ ] Inyecta `GameMapper`
  - [ ] Anotar con `@Component`
- [ ] Implementar método: `listGames(page, size, sort)`
- [ ] Implementar método: `getGameById(id)`
- [ ] Implementar método: `createGame(CreateGameDTO)`
- [ ] Implementar método: `updateGame(id, UpdateGameDTO)`
- [ ] Implementar método: `deleteGame(id)`
- [ ] ✅ **Validar:** Clase compila sin errores

**Fecha completada:** _______________

---

### Tarea 4.2: Implementar Métodos del Delegado

Para cada método (repetir para los 5):

- [ ] Recibir parámetros correctos
- [ ] Validar datos si es necesario
- [ ] Mapear DTO a entidad (usando GameMapper)
- [ ] Llamar servicio de aplicación correcto
- [ ] Mapear respuesta a DTO generado
- [ ] Retornar ResponseEntity con código HTTP apropiado

**Métodos completados:**
- [ ] listGames
- [ ] getGameById
- [ ] createGame (201)
- [ ] updateGame (200)
- [ ] deleteGame (204)

**Fecha completada:** _______________

---

### Tarea 4.3: Validar Mappers

- [ ] Abrir `src/main/java/.../application/mapper/GameMapper.java`
- [ ] Verificar que tiene métodos para:
  - [ ] `toGameDTO(Game): GameDTO`
  - [ ] `toGame(CreateGameDTO): Game`
  - [ ] `toGame(UpdateGameDTO): Game`
- [ ] Compilar: `mvn clean compile`
- [ ] ✅ **Validar:** MapStruct genera implementación sin errores

**Fecha completada:** _______________

---

## 📋 FASE 5: SWAGGER UI (15-20 minutos)

### Tarea 5.1: Configurar Propiedades

- [ ] Editar: `src/main/resources/application.properties`
- [ ] Agregar propiedades Springdoc:
  ```properties
  springdoc.api-docs.path=/api-docs
  springdoc.swagger-ui.path=/swagger-ui.html
  springdoc.swagger-ui.enabled=true
  springdoc.swagger-ui.operationsSorter=method
  springdoc.swagger-ui.tagsSorter=alpha
  ```
- [ ] Editar: `src/main/resources/application-local.properties`
- [ ] Agregar mismas propiedades
- [ ] Editar: `src/main/resources/application-test.properties`
- [ ] Agregar mismas propiedades
- [ ] ✅ **Validar:** Propiedades guardadas

**Fecha completada:** _______________

---

### Tarea 5.2: Crear Configuración Swagger (Opcional)

- [ ] Crear: `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java`
- [ ] Anotar con `@Configuration`
- [ ] Crear bean `@Bean public OpenAPI customOpenAPI()`
- [ ] Configurar:
  - [ ] Título: "Game API"
  - [ ] Versión: "1.0.0"
  - [ ] Descripción: "API para gestión de juegos"
  - [ ] Contacto: Team name y email
- [ ] Compilar: `mvn clean compile`
- [ ] ✅ **Validar:** Clase compila sin errores

**Fecha completada:** _______________

---

### Tarea 5.3: Validar Acceso a Swagger UI

- [ ] Ejecutar aplicación: `mvn spring-boot:run`
- [ ] Esperar: "Tomcat started on port 8080"
- [ ] Abrir navegador: `http://localhost:8080/swagger-ui.html`
- [ ] Verificar que carga correctamente
- [ ] Verificar que aparecen endpoints:
  - [ ] GET /api/games
  - [ ] POST /api/games
  - [ ] GET /api/games/{id}
  - [ ] PUT /api/games/{id}
  - [ ] DELETE /api/games/{id}
- [ ] Click en endpoint
- [ ] Verificar esquemas de request/response
- [ ] Click "Try it out" en un endpoint
- [ ] Probar si funciona (puede fallar por falta de BD, pero debe permitir intentar)
- [ ] ✅ **Validar:** UI accesible y funcional

**Fecha completada:** _______________

---

## 📋 FASE 6: TESTING (30-45 minutos)

### Tarea 6.1: Actualizar Tests

- [ ] Abrir: `src/test/java/.../GameControllerIntegrationTest.java`
- [ ] Verificar imports:
  - [ ] Importar DTOs de: `es.marugi.spring.api.generated.model`
  - [ ] NO importar DTOs viejos si existen
- [ ] Actualizar assertions para usar DTOs generados
- [ ] Si hay tests que crean DTOs:
  - [ ] Cambiar a usar `new CreateGameDTO()`
  - [ ] Verificar que setters/constructor funcionan
- [ ] Repetir para `SecurityIntegrationTest.java`
- [ ] ✅ **Validar:** Tests compilar sin errores

**Fecha completada:** _______________

---

### Tarea 6.2: Ejecutar Tests de Arquitectura

- [ ] Ejecutar: `mvn test -Dtest=OnionArchitectureTest`
- [ ] Esperado: BUILD SUCCESS
- [ ] Verificar que no hay violaciones de arquitectura
- [ ] ✅ **Validar:** Arquitectura hexagonal preservada

**Fecha completada:** _______________

---

### Tarea 6.3: Ejecutar Suite Completa

- [ ] Ejecutar: `mvn clean test`
- [ ] Esperado: BUILD SUCCESS
- [ ] Verificar numero de tests ejecutados
- [ ] Verificar que no hay failures
- [ ] ✅ **Validar:** Todos los tests pasando

**Fecha completada:** _______________

---

## 📋 FASE 7: DOCUMENTACIÓN (30-45 minutos)

### Tarea 7.1: Actualizar README.md

Usar: `README_UPDATE_GUIDE.md` como referencia

- [ ] Sección 1: Actualizar descripción principal
  - [ ] Agregar: "Implementa el paradigma API First"
  
- [ ] Sección 2: Agregar "API First Development" después de Arquitectura
  - [ ] Incluir: Información sobre OpenAPI, localización, auto-generated code
  
- [ ] Sección 3: Agregar "Swagger UI Configuration" después de Testing
  - [ ] Incluir: Propiedades, cómo acceder, cómo probar
  
- [ ] Sección 4: Actualizar "Functional Overview"
  - [ ] Reemplazar con nuevos endpoints
  
- [ ] Sección 5: Agregar "Development Workflow"
  - [ ] Explicar cómo agregar nuevos endpoints
  - [ ] Incluir: Cómo modificar existentes
  - [ ] Incluir: Troubleshooting
  
- [ ] Sección 6: Agregar "References & Further Reading"
  - [ ] Enlace a OpenAPI 3.1 spec
  - [ ] Enlace a Springdoc
  - [ ] Otros recursos útiles

- [ ] Validar que markdown está bien formateado
- [ ] ✅ **Validar:** README actualizado y coherente

**Fecha completada:** _______________

---

### Tarea 7.2: Crear Documentación Complementaria (Opcional)

- [ ] Crear: `ARCHITECTURE.md` (descripción de decisiones)
- [ ] Crear: `CONTRIBUTING.md` (guía para contribuidores)
- [ ] Crear: `API_DEVELOPMENT.md` (guía para agregar endpoints)
- [ ] ✅ **Validar:** Documentos creados y en formato markdown

**Fecha completada:** _______________

---

## ✅ VALIDACIÓN FINAL

### Checklist de Éxito

- [ ] **Swagger UI** accesible en http://localhost:8080/swagger-ui.html
- [ ] **Todos los endpoints** visibles en Swagger UI
- [ ] **OpenAPI JSON** disponible en http://localhost:8080/v3/api-docs
- [ ] **DTOs generados** presentes en `target/generated-sources/openapi/`
- [ ] **GameApiDelegateImpl** implementado y funcional
- [ ] **Todos los tests** pasando (incluyendo ArchUnit)
- [ ] **Mappers** generados correctamente por MapStruct
- [ ] **README.md** actualizado con 5 nuevas secciones
- [ ] **Documentación** completa y coherente
- [ ] **Equipo capacitado** en nuevo workflow API First

### Prueba End-to-End

- [ ] Iniciar aplicación: `mvn spring-boot:run`
- [ ] Ir a: http://localhost:8080/swagger-ui.html
- [ ] Probar endpoint: GET /api/games
- [ ] Probar endpoint: POST /api/games (crear nuevo)
- [ ] Probar endpoint: GET /api/games/{id}
- [ ] Probar endpoint: PUT /api/games/{id}
- [ ] Probar endpoint: DELETE /api/games/{id}
- [ ] Verificar respuestas tienen formato correcto
- [ ] ✅ **RESULTADO:** Todos funcionan correctamente

---

## 📊 RESUMEN DE COMPLETACIÓN

| Fase | Completada | Fecha | Responsable |
|------|-----------|-------|------------|
| 1. Preparación | [ ] | ___________ | __________ |
| 2. OpenAPI | [ ] | ___________ | __________ |
| 3. Generación | [ ] | ___________ | __________ |
| 4. Implementación | [ ] | ___________ | __________ |
| 5. Swagger UI | [ ] | ___________ | __________ |
| 6. Testing | [ ] | ___________ | __________ |
| 7. Documentación | [ ] | ___________ | __________ |

---

## 📝 NOTAS Y OBSERVACIONES

```
_________________________________________________________________

_________________________________________________________________

_________________________________________________________________

_________________________________________________________________

_________________________________________________________________
```

---

## 🎓 FIRMA DE COMPLETACIÓN

- [ ] **Implementador:** ___________________ Fecha: __________
- [ ] **Revisor Técnico:** ___________________ Fecha: __________
- [ ] **Aprobador:** ___________________ Fecha: __________

---

## 📞 SOPORTE DURANTE IMPLEMENTACIÓN

**Si tienes dudas, consulta:**
- QUICK_START.md - Pasos rápidos
- PLAN_API_FIRST.md - Detalles completos
- OPENAPI_SPEC.md - Especificación técnica
- README_UPDATE_GUIDE.md - Guía de documentación

**Herramientas online:**
- Swagger Editor: https://editor.swagger.io/
- Spectral: https://www.stoplight.io/

---

**Documento:** Checklist Imprimible | **Versión:** 1.0 | **Última actualización:** 2026-04-12

✅ **LISTO PARA USAR: Imprime y distribuye al equipo**

