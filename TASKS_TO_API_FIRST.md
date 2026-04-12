# API First Implementation Roadmap

**Project:** spring4-api-first-sample  
**Objective:** Convert the project to API First paradigm  
**Status:** Not Started  
**Last Updated:** 2026-04-12

> **IMPORTANT:** All code and documentation must be written in English

---

## Overview

This document contains the complete checklist of tasks to convert the project to API First paradigm. Tasks should be executed sequentially and each one should be validated before moving to the next.

**Estimated Total Time:** 3-4 hours  
**Complexity:** Medium  
**Risk Level:** Low

---

## Phase 1: Project Preparation (15-20 minutes)

### ☐ Task 1.1: Add Maven Dependencies

**Description:** Add Springdoc and OpenAPI Generator dependencies to `pom.xml`

**Changes required:**
- [ ] Add `springdoc-openapi-starter-webmvc-ui` v2.8.2 to `<dependencies>`
- [ ] Add `springdoc-openapi-starter-webmvc-api` v2.8.2 to `<dependencies>`
- [ ] Add `openapi-generator-maven-plugin` v7.6.0 to `<build><plugins>`
- [ ] Configure plugin with correct package names and settings

**Validation:**
- [ ] Execute: `mvn clean package -DskipTests`
- [ ] Build successful without errors
- [ ] No dependency conflicts

**Files affected:** `pom.xml`

---

### ☐ Task 1.2: Create Directory Structure

**Description:** Create directories for OpenAPI resources

**Changes required:**
- [ ] Create directory: `src/main/resources/api/`
- [ ] Create directory: `src/main/resources/api/schemas/` (optional)

**Validation:**
- [ ] Directories exist and are visible in IDE explorer
- [ ] Correct path structure

**Files affected:** Filesystem only

---

## Phase 2: OpenAPI Specification (20-30 minutes)

### ☐ Task 2.1: Create OpenAPI Specification

**Description:** Create the OpenAPI 3.1 specification file (`openapi.yaml`)

**Changes required:**
- [ ] Create file: `src/main/resources/api/openapi.yaml`
- [ ] Define API metadata (title, version, description)
- [ ] Define 5 endpoints:
  - [ ] GET /api/games (list games with pagination)
  - [ ] GET /api/games/{id} (get single game)
  - [ ] POST /api/games (create game)
  - [ ] PUT /api/games/{id} (update game)
  - [ ] DELETE /api/games/{id} (delete game)
- [ ] Define schemas:
  - [ ] GameDTO (complete game data)
  - [ ] CreateGameDTO (for POST requests)
  - [ ] UpdateGameDTO (for PUT requests)
  - [ ] ErrorResponse (for error handling)
- [ ] Define responses for all HTTP status codes (200, 201, 204, 400, 401, 404, 500)
- [ ] Configure OAuth2 security scheme
- [ ] Add validation rules (min/max length, required fields, etc.)

**Specification content:**
```
✓ Info section with API metadata
✓ Servers section (local and production)
✓ Paths section with all endpoints
✓ Components section with schemas and security
✓ Global security requirements
```

**Validation:**
- [ ] YAML syntax is valid
- [ ] Test in Swagger Editor: https://editor.swagger.io/
- [ ] No linting errors
- [ ] All endpoints documented
- [ ] All schemas complete

**Files affected:** `src/main/resources/api/openapi.yaml` (NEW)

---

### ☐ Task 2.2: Validate OpenAPI Specification

**Description:** Validate the YAML specification

**Changes required:**
- [ ] Verify YAML syntax is correct
- [ ] Check that all paths are properly formatted
- [ ] Verify schema definitions
- [ ] Validate security configuration

**Validation:**
- [ ] Use Swagger Editor online: https://editor.swagger.io/
- [ ] Copy `openapi.yaml` content and paste into Swagger Editor
- [ ] No red error lines should appear
- [ ] UI renders correctly with all endpoints visible

**Files affected:** None (validation only)

---

## Phase 3: Code Generation (10-15 minutes)

### ☐ Task 3.1: Generate Code from OpenAPI

**Description:** Generate DTOs, interfaces and validators from the OpenAPI specification

**Changes required:**
- [ ] Execute Maven compilation: `mvn clean compile`
- [ ] Wait for code generation to complete

**Validation:**
- [ ] Build status: BUILD SUCCESS
- [ ] Check generated code in `target/generated-sources/openapi/`
- [ ] Verify generated structure:
  ```
  target/generated-sources/openapi/
  └── es/marugi/spring/api/generated/
      ├── model/
      │   ├── GameDTO.java
      │   ├── CreateGameDTO.java
      │   ├── UpdateGameDTO.java
      │   └── ErrorResponse.java
      └── api/
          ├── GameApi.java
          └── GameApiDelegate.java
  ```
- [ ] All expected files are present
- [ ] Files contain correct package names
- [ ] No compilation errors in generated code

**Files affected:** `target/generated-sources/openapi/` (GENERATED)

---

### ☐ Task 3.2: Configure IDE

**Description:** Configure IDE to recognize generated sources

**For IntelliJ IDEA:**
- [ ] Go to: `File → Project Structure → Modules`
- [ ] Select the project module
- [ ] Go to `Sources` tab
- [ ] Mark `target/generated-sources/openapi/` as `Generated Sources`
- [ ] Click `Apply` and `OK`

**For Eclipse:**
- [ ] Right-click on `target/generated-sources/openapi/`
- [ ] Select `Build Path → Use as Source Folder`

**Validation:**
- [ ] IDE recognizes generated classes
- [ ] No red error squiggles in generated code
- [ ] IDE can navigate to generated classes

**Files affected:** IDE configuration only

---

## Phase 4: Implementation (60-90 minutes)

### ☐ Task 4.1: Create GameApiDelegateImpl

**Description:** Create the implementation of the generated GameApiDelegate interface

**Changes required:**
- [ ] Create file: `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
- [ ] Class name: `GameApiDelegateImpl`
- [ ] Implement: `GameApiDelegate` (generated interface)
- [ ] Annotate with: `@Component`
- [ ] Inject dependencies:
  - [ ] `GameCommandService` via constructor
  - [ ] `GameQueryService` via constructor
  - [ ] `GameMapper` via constructor
- [ ] Implement all methods from delegate:
  - [ ] `listGames(page, size, sort): ResponseEntity<List<GameDTO>>`
  - [ ] `getGameById(id): ResponseEntity<GameDTO>`
  - [ ] `createGame(createGameDTO): ResponseEntity<GameDTO>`
  - [ ] `updateGame(id, updateGameDTO): ResponseEntity<GameDTO>`
  - [ ] `deleteGame(id): ResponseEntity<Void>`

**Implementation requirements for each method:**
- [ ] Receive correct parameters
- [ ] Validate input if necessary
- [ ] Map DTO to domain entity using `GameMapper`
- [ ] Call appropriate service method
- [ ] Map response back to DTO using `GameMapper`
- [ ] Return `ResponseEntity` with correct HTTP status code:
  - GET/PUT: 200 (OK)
  - POST: 201 (Created)
  - DELETE: 204 (No Content)

**Validation:**
- [ ] Class compiles without errors
- [ ] All methods implemented
- [ ] All dependencies injected correctly
- [ ] Return types are correct
- [ ] HTTP status codes are appropriate

**Files affected:** `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java` (NEW)

---

### ☐ Task 4.2: Update Mappers

**Description:** Ensure MapStruct mappers handle generated DTOs correctly

**Changes required:**
- [ ] Open: `src/main/java/es/marugi/spring/api/application/mapper/GameMapper.java`
- [ ] Verify mapper has methods for:
  - [ ] `GameDTO toGameDTO(Game entity)`
  - [ ] `Game toGame(CreateGameDTO dto)`
  - [ ] `Game toGame(UpdateGameDTO dto)`
- [ ] If methods don't exist, add them to interface
- [ ] Let MapStruct regenerate implementation

**Validation:**
- [ ] Execute: `mvn clean compile`
- [ ] MapStruct generates implementation without errors
- [ ] All mapping methods are present
- [ ] Check generated implementation in `target/generated-sources/annotations/`

**Files affected:** `src/main/java/es/marugi/spring/api/application/mapper/GameMapper.java`

---

### ☐ Task 4.3: Review and Remove Old Controllers (if applicable)

**Description:** Check if there are old REST controllers to remove or adapt

**Changes required:**
- [ ] Check: `src/main/java/es/marugi/spring/api/adapter/in/rest/`
- [ ] If old controllers exist that duplicate functionality:
  - [ ] Review their endpoints
  - [ ] Compare with generated API
  - [ ] Option 1: Remove old controllers (if fully covered by generated API)
  - [ ] Option 2: Keep if they provide additional functionality
- [ ] Ensure no duplicate endpoints

**Validation:**
- [ ] No endpoint conflicts
- [ ] One source of truth per endpoint
- [ ] Clean adapter layer

**Files affected:** Potentially existing controller files

---

## Phase 5: Configure Swagger UI (15-20 minutes)

### ☐ Task 5.1: Update Application Properties

**Description:** Configure Springdoc properties for Swagger UI

**Changes required:**

For `src/main/resources/application.properties`:
- [ ] Add: `springdoc.api-docs.path=/api-docs`
- [ ] Add: `springdoc.swagger-ui.path=/swagger-ui.html`
- [ ] Add: `springdoc.swagger-ui.enabled=true`
- [ ] Add: `springdoc.swagger-ui.operationsSorter=method`
- [ ] Add: `springdoc.swagger-ui.tagsSorter=alpha`
- [ ] Add: `springdoc.swagger-ui.show-extensions=true`

For `src/main/resources/application-local.properties`:
- [ ] Add same properties as above

For `src/main/resources/application-test.properties`:
- [ ] Add same properties as above

**Validation:**
- [ ] Properties saved in all files
- [ ] No typos in property names
- [ ] File format is correct

**Files affected:**
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`
- `src/main/resources/application-test.properties`

---

### ☐ Task 5.2: Create Swagger Configuration (Optional)

**Description:** Create customized Swagger configuration class

**Changes required:**
- [ ] Create file: `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java`
- [ ] Annotate class with: `@Configuration`
- [ ] Create method annotated with: `@Bean public OpenAPI customOpenAPI()`
- [ ] Configure OpenAPI object:
  - [ ] Set title: "Game API"
  - [ ] Set version: "1.0.0"
  - [ ] Set description: "REST API for game management using API First paradigm"
  - [ ] Add contact information:
    - [ ] Name: "Development Team"
    - [ ] Email: "development@example.com"

**Validation:**
- [ ] Class compiles without errors
- [ ] Bean is created successfully
- [ ] Configuration is applied

**Files affected:** `src/main/java/es/marugi/spring/api/infrastructure/config/SwaggerConfig.java` (NEW, OPTIONAL)

---

### ☐ Task 5.3: Validate Swagger UI Access

**Description:** Start application and verify Swagger UI is accessible

**Changes required:**
- [ ] Execute: `mvn spring-boot:run`
- [ ] Wait for "Tomcat started on port 8080" message
- [ ] Open browser to: `http://localhost:8080/swagger-ui.html`

**Validation:**
- [ ] Swagger UI page loads successfully
- [ ] All 5 endpoints are visible:
  - [ ] GET /api/games
  - [ ] POST /api/games
  - [ ] GET /api/games/{id}
  - [ ] PUT /api/games/{id}
  - [ ] DELETE /api/games/{id}
- [ ] Endpoint details are visible when expanded
- [ ] Request/response schemas are shown
- [ ] "Try it out" button is functional
- [ ] OpenAPI JSON available at: `http://localhost:8080/v3/api-docs`

**Files affected:** None (validation only)

---

## Phase 6: Testing and Validation (30-45 minutes)

### ☐ Task 6.1: Update Integration Tests

**Description:** Update test files to use generated DTOs

**Changes required:**

For `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`:
- [ ] Update imports:
  - [ ] Replace old DTO imports with generated DTO imports
  - [ ] Import from: `es.marugi.spring.api.generated.model.*`
- [ ] Update test fixtures:
  - [ ] Create test data using generated DTO classes
  - [ ] Verify constructors/setters match generated code
- [ ] Update assertions:
  - [ ] Update property names if they changed
  - [ ] Verify field access patterns

For `src/test/java/es/marugi/spring/api/SecurityIntegrationTest.java`:
- [ ] Update imports for generated DTOs
- [ ] Update any security-related test assertions
- [ ] Verify authentication flow still works with new DTOs

**Validation:**
- [ ] All tests compile without errors
- [ ] No import errors
- [ ] IDE shows no red squiggles
- [ ] Tests can be parsed and understood

**Files affected:**
- `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`
- `src/test/java/es/marugi/spring/api/SecurityIntegrationTest.java`

---

### ☐ Task 6.2: Run Architecture Tests

**Description:** Verify Hexagonal Architecture rules are still enforced

**Changes required:**
- [ ] Execute: `mvn test -Dtest=OnionArchitectureTest`

**Validation:**
- [ ] BUILD SUCCESS
- [ ] No architecture rule violations
- [ ] All tests passed:
  - [ ] Domain independence
  - [ ] Layer direction
  - [ ] No cycles
  - [ ] Naming conventions

**Files affected:** None (validation only)

---

### ☐ Task 6.3: Run Complete Test Suite

**Description:** Execute all tests to ensure no regressions

**Changes required:**
- [ ] Execute: `mvn clean test`
- [ ] Wait for all tests to complete

**Validation:**
- [ ] BUILD SUCCESS
- [ ] All tests passed (unit + integration + architecture)
- [ ] No failures or errors
- [ ] Test coverage maintained or improved
- [ ] No warnings

**Files affected:** None (validation only)

---

## Phase 7: Documentation (30-45 minutes)

### ☐ Task 7.1: Update README.md

**Description:** Update README with API First and Swagger UI information

**Changes required:**

In `README.md`, add the following sections (use English):

**Section 1: After "🏗️ Architecture" (around line 30)**
- [ ] New section: "🎯 API First Development"
- [ ] Explain API First paradigm
- [ ] Location of OpenAPI contract: `src/main/resources/api/openapi.yaml`
- [ ] Auto-generated code location
- [ ] How to access Swagger UI

**Section 2: After "📋 Functional Overview" (around line 173)**
- [ ] New section: "📐 Swagger UI Configuration"
- [ ] Configuration properties
- [ ] How to access Swagger UI: `http://localhost:8080/swagger-ui.html`
- [ ] How to view OpenAPI JSON
- [ ] How to test endpoints from UI

**Section 3: Update "📋 Functional Overview" section**
- [ ] Replace with new endpoint descriptions
- [ ] List all CRUD endpoints
- [ ] Add OAuth2 information
- [ ] Add database integration notes

**Section 4: Add new section "🔄 Development Workflow"**
- [ ] How to add a new endpoint
- [ ] How to modify existing endpoints
- [ ] Code generation troubleshooting

**Section 5: Add new section "📚 References & Further Reading"**
- [ ] Links to OpenAPI 3.1 specification
- [ ] Links to Springdoc documentation
- [ ] Links to Spring Boot guides

**Validation:**
- [ ] All sections are properly formatted
- [ ] Markdown syntax is correct
- [ ] All links are working
- [ ] Content is clear and helpful
- [ ] No spelling or grammar errors

**Files affected:** `README.md`

---

### ☐ Task 7.2: Create API Development Guide (Optional)

**Description:** Create additional documentation for API development

**Changes required:**
- [ ] Create file: `API_DEVELOPMENT.md` (optional)
- [ ] Document the workflow for adding new endpoints:
  - [ ] Step 1: Edit `openapi.yaml`
  - [ ] Step 2: Run `mvn clean compile`
  - [ ] Step 3: Implement delegate method
  - [ ] Step 4: Write tests
  - [ ] Step 5: Verify in Swagger UI

**Validation:**
- [ ] Guide is clear and complete
- [ ] Examples are provided
- [ ] Steps are in correct order

**Files affected:** `API_DEVELOPMENT.md` (NEW, OPTIONAL)

---

## Validation Checklist

After completing all phases, verify:

### Functionality
- [ ] Application starts without errors: `mvn spring-boot:run`
- [ ] Swagger UI loads at `http://localhost:8080/swagger-ui.html`
- [ ] All 5 endpoints are listed in Swagger UI
- [ ] OpenAPI JSON is available at `/v3/api-docs`

### Code Quality
- [ ] All tests pass: `mvn test`
- [ ] Architecture tests pass: `mvn test -Dtest=OnionArchitectureTest`
- [ ] No compilation warnings
- [ ] No unused imports

### Documentation
- [ ] README.md updated with API First information
- [ ] Swagger UI shows all endpoint details
- [ ] Generated code is properly documented
- [ ] Comments are in English

### Integration
- [ ] Generated code integrates with existing services
- [ ] Mappers work correctly
- [ ] Dependency injection works
- [ ] Security configuration is maintained

---

## Success Criteria

Project is successfully converted to API First when:

✅ Swagger UI is accessible and shows all endpoints  
✅ All tests pass (unit, integration, architecture)  
✅ Generated DTOs are used throughout the application  
✅ OpenAPI specification is the single source of truth  
✅ README is updated with API First information  
✅ Code and documentation are in English  
✅ New endpoints can be added quickly (15-20 minutes)  
✅ Documentation is automatically updated  

---

## Summary

| Phase | Task | Status | Time | Completion |
|-------|------|--------|------|-----------|
| 1 | Preparation | ☐ Not Started | 15-20 min | ___ / ___ |
| 2 | OpenAPI Specification | ☐ Not Started | 20-30 min | ___ / ___ |
| 3 | Code Generation | ☐ Not Started | 10-15 min | ___ / ___ |
| 4 | Implementation | ☐ Not Started | 60-90 min | ___ / ___ |
| 5 | Swagger UI | ☐ Not Started | 15-20 min | ___ / ___ |
| 6 | Testing | ☐ Not Started | 30-45 min | ___ / ___ |
| 7 | Documentation | ☐ Not Started | 30-45 min | ___ / ___ |
| **TOTAL** | **API First Ready** | **☐ Not Started** | **3-4 hours** | ___ / ___ |

---

## Notes

- Execute tasks sequentially
- Validate each task before proceeding to the next
- All code and documentation must be in English
- Reference OpenAPI 3.1 specification: https://spec.openapis.org/oas/v3.1.0
- For Swagger validation use: https://editor.swagger.io/

---

**Last Updated:** 2026-04-12  
**Version:** 1.0  
**Status:** Ready to Execute

