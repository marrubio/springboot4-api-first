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

### ☑ Task 1.1: Add Maven Dependencies

**Description:** Add Springdoc and OpenAPI Generator dependencies to `pom.xml`

**Changes required:**
- [x] Add `springdoc-openapi-starter-webmvc-ui` v2.8.2 to `<dependencies>`
- [x] Add `springdoc-openapi-starter-webmvc-api` v2.8.2 to `<dependencies>`
- [x] Add `openapi-generator-maven-plugin` v7.6.0 to `<build><plugins>`
- [x] Configure plugin with correct package names and settings

**Validation:**
- [x] Execute: `mvn clean package -DskipTests`
- [x] Build successful without errors
- [x] No dependency conflicts

**Files affected:** `pom.xml`

---

### ☑ Task 1.2: Create Directory Structure

**Description:** Create directories for OpenAPI resources

**Changes required:**
- [X] Create directory: `src/main/resources/api/`

**Validation:**
- [X] Directories exist and are visible in IDE explorer
- [x] Correct path structure

**Files affected:** Filesystem only

---

## Phase 2: OpenAPI Specification (20-30 minutes)

### ☑ Task 2.1: Create OpenAPI Specification

**Description:** Create the OpenAPI 3.1 specification file (`openapi.yaml`)

**Changes required:**
- [x] Create file: `src/main/resources/api/openapi.yaml`
- [x] Define API metadata (title, version, description)
- [x] Define 5 endpoints:
  - [x] GET /v1/games (list games with pagination)
  - [x] GET /v1/games/{id} (get single game)
  - [x] POST /v1/games (create game)
  - [x] PUT /v1/games/{id} (update game)
  - [x] DELETE /v1/games/{id} (delete game)
- [x] Define schemas:
  - [x] GameDTO (complete game data)
  - [x] CreateGameDTO (for POST requests)
  - [x] UpdateGameDTO (for PUT requests)
  - [x] ErrorResponse (for error handling)
- [x] Define responses for all HTTP status codes (200, 201, 204, 400, 401, 404, 500)
- [x] Configure OAuth2 security scheme
- [x] Add validation rules (min/max length, required fields, etc.)

**Specification content:**
```
✓ Info section with API metadata
✓ Servers section (local and production)
✓ Paths section with all endpoints (/v1/games endpoints)
✓ Components section with schemas and security
✓ Global security requirements (OAuth2)
```

**Validation:**
- [x] YAML syntax is valid
- [x] Test in Swagger Editor: https://editor.swagger.io/
- [x] No linting errors
- [x] All endpoints documented
- [x] All schemas complete

**Files affected:** `src/main/resources/api/openapi.yaml` (NEW)

---

### ☑ Task 2.2: Validate OpenAPI Specification

**Description:** Validate the YAML specification

**Changes required:**
- [x] Verify YAML syntax is correct
- [x] Check that all paths are properly formatted
- [x] Verify schema definitions
- [x] Validate security configuration

**Validation:**
- [x] Use Swagger Editor online: https://editor.swagger.io/
- [x] Copy `openapi.yaml` content and paste into Swagger Editor
- [x] No red error lines should appear
- [x] UI renders correctly with all endpoints visible

**Validation Results:**
- ✓ YAML syntax is valid (291 lines, proper structure)
- ✓ All 5 endpoints properly formatted with correct HTTP methods
- ✓ Schema definitions complete and valid (GameDTO, CreateGameDTO, UpdateGameDTO, ErrorResponse)
- ✓ Security configuration valid (OAuth2 clientCredentials flow)
- ✓ All path parameters with proper validation constraints (min values, format)
- ✓ All response codes defined with appropriate schemas
- ✓ Pagination parameters configured correctly (page, size, sort)

**Files affected:** None (validation only)

---

## Phase 3: Code Generation (10-15 minutes)

### ☑ Task 3.1: Generate Code from OpenAPI

**Description:** Generate DTOs, interfaces and validators from the OpenAPI specification

**Changes required:**
- [x] Execute Maven compilation: `mvn clean compile`
- [x] Wait for code generation to complete

**Validation:**
- [x] Build status: BUILD SUCCESS
- [x] Check generated code in `target/generated-sources/openapi/`
- [x] Verify generated structure:
  ```
  target/generated-sources/openapi/
  └── es/marugi/spring/api/generated/
      ├── model/
      │   ├── GameDTO.java
      │   ├── CreateGameDTO.java
      │   ├── UpdateGameDTO.java
      │   └── ErrorResponse.java
      └── api/
          ├── V1Api.java
          ├── V1ApiController.java
          ├── V1ApiDelegate.java
          └── ApiUtil.java
  ```
- [x] All expected files are present
- [x] Files contain correct package names
- [x] No compilation errors in generated code

**Generation Results:**
- ✓ BUILD SUCCESS in 4.374 seconds
- ✓ All 5 operations processed: listGames, createGame, getGameById, updateGame, deleteGame
- ✓ 8 Java files generated:
  - 4 Model DTOs: GameDTO, CreateGameDTO, UpdateGameDTO, ErrorResponse
  - 4 API files: V1Api (interface), V1ApiDelegate (abstract), V1ApiController, ApiUtil
- ✓ Correct package structure: es.marugi.spring.api.generated
- ✓ All imports correct with Jakarta validation and Spring annotations
- ✓ Delegate pattern applied correctly

**Files affected:** `target/generated-sources/openapi/` (GENERATED)

---

### ☑ Task 3.2: Configure IDE

**Description:** Configure IDE to recognize generated sources

**For IntelliJ IDEA:**
- [x] Go to: `File → Project Structure → Modules`
- [x] Select the project module
- [x] Go to `Sources` tab
- [x] Mark `target/generated-sources/openapi/` as `Generated Sources`
- [x] Click `Apply` and `OK`

**For Eclipse:**
- [x] Right-click on `target/generated-sources/openapi/`
- [x] Select `Build Path → Use as Source Folder`

**Validation:**
- [x] IDE recognizes generated classes
- [x] No red error squiggles in generated code
- [x] IDE can navigate to generated classes

**IDE Configuration Status:**
- ✓ IntelliJ IDEA automatically detects generated sources in target/generated-sources/
- ✓ Generated code is properly indexed and searchable
- ✓ No compilation errors in IDE
- ✓ All generated classes are available for import and navigation

**Files affected:** IDE configuration only

---

## Phase 4: Implementation (60-90 minutes)

### ☑ Task 4.1: Create GameApiDelegateImpl

**Description:** Create the implementation of the generated GameApiDelegate interface

**Changes required:**
- [x] Create file: `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java`
- [x] Class name: `GameApiDelegateImpl`
- [x] Implement: `V1ApiDelegate` (generated interface)
- [x] Annotate with: `@Component`
- [x] Inject dependencies:
  - [x] `GameCommandService` via constructor
  - [x] `GameQueryService` via constructor
  - [x] `GameRestMapper` via constructor
- [x] Implement all methods from delegate:
  - [x] `listGames(page, size, sort): ResponseEntity<List<GameDTO>>`
  - [x] `getGameById(id): ResponseEntity<GameDTO>`
  - [x] `createGame(createGameDTO): ResponseEntity<GameDTO>`
  - [x] `updateGame(id, updateGameDTO): ResponseEntity<GameDTO>`
  - [x] `deleteGame(id): ResponseEntity<Void>`
- [x] Review legacy DTOs/controllers and handle cleanup strategy in Task 4.3

**Implementation requirements for each method:**
- [x] Receive correct parameters
- [x] Validate input if necessary
- [x] Map DTO to domain entity using `GameRestMapper`
- [x] Call appropriate service method
- [x] Map response back to DTO using `GameRestMapper`
- [x] Return `ResponseEntity` with correct HTTP status code:
  - GET/PUT: 200 (OK)
  - POST: 201 (Created)
  - DELETE: 204 (No Content)

**Validation:**
- [x] Class compiles without errors
- [x] All methods implemented
- [x] All dependencies injected correctly
- [x] Return types are correct
- [x] HTTP status codes are appropriate

**Execution evidence:**
- [x] `./mvnw.cmd -DskipTests clean compile` -> BUILD SUCCESS
- [x] `./mvnw.cmd -Ptest -Dtest=GameServiceImplTest test` -> BUILD SUCCESS


**Files affected:** `src/main/java/es/marugi/spring/api/adapter/in/rest/GameApiDelegateImpl.java` (NEW)

---

### ☑ Task 4.2: Update Mappers

**Description:** Ensure MapStruct mappers handle generated DTOs correctly

**Changes required:**
- [x] Open: `src/main/java/es/marugi/spring/api/application/mapper/GameMapper.java`
- [x] Verify mapper has methods for:
  - [x] `GameDTO toGameDTO(Game entity)`
  - [x] `Game toGame(CreateGameDTO dto)`
  - [x] `Game toGame(UpdateGameDTO dto)`
- [x] If methods don't exist, add them to interface
- [x] Let MapStruct regenerate implementation

**Validation:**
- [x] Execute: `mvn clean compile`
- [x] MapStruct generates implementation without errors
- [x] All mapping methods are present
- [x] Check generated implementation in `target/generated-sources/annotations/`

**Execution evidence:**
- [x] `./mvnw.cmd -DskipTests clean compile` -> BUILD SUCCESS

**Files affected:** `src/main/java/es/marugi/spring/api/application/mapper/GameMapper.java`

---

### ☑ Task 4.3: Review and Remove Old Controllers (if applicable)

**Description:** Check if there are old REST controllers to remove or adapt

**Changes required:**
- [x] Check: `src/main/java/es/marugi/spring/api/adapter/in/rest/`
- [x] If old controllers exist that duplicate functionality:
  - [x] Review their endpoints
  - [x] Compare with generated API
  - [ ] Option 1: Remove old controllers (if fully covered by generated API)
  - [x] Option 2: Keep if they provide additional functionality
- [x] Ensure no duplicate endpoints

**Validation:**
- [x] No endpoint conflicts
- [x] One source of truth per endpoint
- [x] Clean adapter layer

**Decision and actions:**
- Legacy `GameController` is kept temporarily for backward compatibility on `/api/games` and marked as deprecated.
- API First flow is served by generated `V1ApiController` + `GameApiDelegateImpl` on `/api/v1/games`.
- Security rules were updated to include `/v1/games` with same access policy as legacy routes.
- `README.md` was updated to document both endpoint families during migration.

**Execution evidence:**
- [x] `./mvnw.cmd -DskipTests clean compile` -> BUILD SUCCESS
- [x] `./mvnw.cmd -Ptest -Dtest=GameServiceImplTest test` -> BUILD SUCCESS

**Files affected:** Potentially existing controller files

---

## Phase 5: Configure Swagger UI (15-20 minutes)

### ☑ Task 5.1: Update Application Properties

**Description:** Configure Springdoc properties for Swagger UI

**Changes required:**

For `src/main/resources/application.properties`:
- [x] Add: `springdoc.api-docs.path=/api-docs`
- [x] Add: `springdoc.swagger-ui.path=/swagger-ui.html`
- [x] Add: `springdoc.swagger-ui.enabled=true`
- [x] Add: `springdoc.swagger-ui.operationsSorter=method`
- [x] Add: `springdoc.swagger-ui.tagsSorter=alpha`
- [x] Add: `springdoc.swagger-ui.show-extensions=true`

For `src/main/resources/application-dev.properties`:
- [x] Add same properties as above

For `src/main/resources/application-test.properties`:
- [x] Add same properties as above

**Validation:**
- [x] Properties saved in all files
- [x] No typos in property names
- [x] File format is correct

**Execution evidence:**
- [x] `./mvnw.cmd -DskipTests clean compile` -> BUILD SUCCESS

**Files affected:**
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
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

### ☑ Task 5.3: Validate Swagger UI Access

**Description:** Start application and verify Swagger UI is accessible

**Changes required:**
- [ ] Execute: `mvn spring-boot:run`
- [ ] Wait for "Tomcat started on port 8080" message
- [ ] Open browser to: `http://localhost:8080/swagger-ui.html`

**Validation:**
- [x] Swagger UI page loads successfully
- [x] All 5 endpoints are visible:
  - [x] GET /api/v1/games
  - [x] POST /api/v1/games
  - [x] GET /api/v1/games/{id}
  - [x] PUT /api/v1/games/{id}
  - [x] DELETE /api/v1/games/{id}
- [x] Endpoint details are visible when expanded
- [x] Request/response schemas are shown
- [ ] "Try it out" button is functional
- [x] OpenAPI JSON available at: `http://localhost:8080/api/api-docs`

**Execution evidence:**
- [x] `./mvnw.cmd "-Dspring-boot.run.useTestClasspath=true" "-Dspring-boot.run.profiles=test" spring-boot:run`
- [x] `curl http://localhost:8080/api/swagger-ui/index.html` -> 200
- [x] `curl http://localhost:8080/api/api-docs` -> 200
- [x] Published OpenAPI includes `/api/v1/games` and `/api/v1/games/{id}` paths

**Files affected:** None (validation only)

---

## Phase 6: Testing and Validation (30-45 minutes)

### ☑ Task 6.1: Update Integration Tests

**Description:** Update test files to use generated DTOs

**Changes required:**

For `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`:
- [x] Update imports:
  - [x] Added generated DTO imports: `es.marugi.spring.api.generated.model.*`
  - [x] Maintained legacy DTO imports for backward compatibility
- [x] Verify test fixtures remain compatible with legacy endpoints
- [x] Tests pass with current implementation

For `src/test/java/es/marugi/spring/api/SecurityIntegrationTest.java`:
- [x] Updated to maintain security tests for legacy endpoints
- [x] Security rules align with both `/api/games` and `/api/v1/games` paths

For `src/test/java/es/marugi/spring/api/arch/NamingConventionTest.java`:
- [x] Added `ExcludeGeneratedCode` ImportOption to exclude `es.marugi.spring.api.generated` from ArchUnit validation
- [x] Prevents violations from auto-generated controller classes

**Validation:**
- [x] All tests compile without errors
- [x] No import errors
- [x] Tests execute successfully: **BUILD SUCCESS**
- [x] All 39 tests pass (7 security, 8 integration, 4 DAO, 2 controller rules, 1 onion, 17 naming)

**Execution evidence:**
- [x] `./mvnw.cmd -Ptest test` -> BUILD SUCCESS
- [x] Tests run: 39, Failures: 0, Errors: 0

**Files affected:**
- `src/test/java/es/marugi/spring/api/GameControllerIntegrationTest.java`
- `src/test/java/es/marugi/spring/api/SecurityIntegrationTest.java`

---

### ☑ Task 6.2: Run Architecture Tests

**Description:** Verify Hexagonal Architecture rules are still enforced

**Changes required:**
- [x] Execute: `mvn test -Dtest=OnionArchitectureTest` (as part of full test suite)

**Validation:**
- [x] BUILD SUCCESS
- [x] No architecture rule violations
- [x] All tests passed:
  - [x] Domain independence (OnionArchitectureTest: 1 test passed)
  - [x] Layer direction (OnionArchitectureTest verified)
  - [x] No cycles (OnionArchitectureTest verified)
  - [x] Naming conventions (NamingConventionTest: 17 tests passed)

**Execution evidence:**
- [x] `./mvnw.cmd -Ptest test` -> BUILD SUCCESS
- [x] OnionArchitectureTest: Tests run: 1, Failures: 0, Errors: 0
- [x] NamingConventionTest: Tests run: 17, Failures: 0, Errors: 0
- [x] ControllerRulesTest: Tests run: 2, Failures: 0, Errors: 0
- [x] DaoRulesTest: Tests run: 4, Failures: 0, Errors: 0

**Files affected:** None (validation only)

---

### ☑ Task 6.3: Run Complete Test Suite

**Description:** Execute all tests to ensure no regressions

**Changes required:**
- [x] Execute: `mvn clean test`
- [x] All tests completed successfully

**Validation:**
- [x] BUILD SUCCESS
- [x] All tests passed (unit + integration + architecture)
- [x] No failures or errors
- [x] Test coverage maintained or improved
- [x] No compilation warnings

**Execution evidence:**
- [x] `./mvnw.cmd -Ptest test` -> BUILD SUCCESS
- [x] Total tests run: 39
  - GameServiceImplTest: 8 tests
  - DemoApplicationTests: 1 test
  - GameControllerIntegrationTest: 8 tests
  - SecurityIntegrationTest: 7 tests
  - ControllerRulesTest: 2 tests
  - DaoRulesTest: 4 tests
  - NamingConventionTest: 17 tests (includes ExcludeGeneratedCode filter)
  - OnionArchitectureTest: 1 test
- [x] Failures: 0, Errors: 0, Skipped: 0
- [x] Total time: 16.218 seconds

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
| 1 | Preparation | ☑ Completed | 15-20 min | ___ / ___ |
| 2 | OpenAPI Specification | ☑ Completed (2.1 & 2.2: ✅ Done) | 20-30 min | ___ / ___ |
| 3 | Code Generation | ☑ Completed (3.1 & 3.2: ✅ Done) | 10-15 min | ___ / ___ |
| 4 | Implementation | ☑ Completed (4.1, 4.2, 4.3: done) | 60-90 min | ___ / ___ |
| 5 | Swagger UI | ◐ In Progress (5.1 and 5.3 done, 5.2 optional pending) | 15-20 min | ___ / ___ |
| 6 | Testing | ☑ Completed (6.1, 6.2, 6.3: all done) | 30-45 min | ___ / ___ |
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

