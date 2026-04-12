# Especificación OpenAPI 3.1 - Game API

Este documento describe el contrato OpenAPI 3.1 para la Game API.

## Ubicación del archivo

El archivo de especificación debe ubicarse en:
```
src/main/resources/api/openapi.yaml
```

## Contenido de `openapi.yaml`

```yaml
openapi: 3.1.0
info:
  title: Game API
  description: |
    API REST para gestión de juegos. 
    Implementa el paradigma API First con contrato OpenAPI.
    
    ## Características
    - CRUD completo de juegos
    - Validación de entrada automática
    - Manejo de errores estándar
    - Seguridad con OAuth2
  version: 1.0.0
  contact:
    name: Equipo de Desarrollo
    email: development@example.com
  license:
    name: MIT

servers:
  - url: http://localhost:8080
    description: Entorno local
    variables:
      port:
        default: '8080'
  - url: https://api.example.com
    description: Entorno de producción

tags:
  - name: Games
    description: Operaciones relacionadas con juegos

paths:
  /api/games:
    get:
      summary: Obtener listado de juegos
      description: Retorna una lista paginada de todos los juegos disponibles
      operationId: listGames
      tags:
        - Games
      parameters:
        - name: page
          in: query
          description: Número de página (0-based)
          required: false
          schema:
            type: integer
            default: 0
            minimum: 0
        - name: size
          in: query
          description: Tamaño de página
          required: false
          schema:
            type: integer
            default: 20
            minimum: 1
        - name: sort
          in: query
          description: Criterio de ordenamiento
          required: false
          schema:
            type: string
            default: id,desc
      responses:
        '200':
          description: Listado de juegos recuperado exitosamente
          content:
            application/json:
              schema:
                type: object
                properties:
                  content:
                    type: array
                    items:
                      $ref: '#/components/schemas/GameDTO'
                  totalElements:
                    type: integer
                  totalPages:
                    type: integer
                  currentPage:
                    type: integer
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '500':
          $ref: '#/components/responses/InternalServerError'
      security:
        - oauth2:
            - read:games
    post:
      summary: Crear nuevo juego
      description: Crea un nuevo juego en el sistema
      operationId: createGame
      tags:
        - Games
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateGameDTO'
      responses:
        '201':
          description: Juego creado exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/GameDTO'
          headers:
            Location:
              schema:
                type: string
              description: URL del recurso creado
        '400':
          $ref: '#/components/responses/BadRequestError'
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '500':
          $ref: '#/components/responses/InternalServerError'
      security:
        - oauth2:
            - write:games

  /api/games/{id}:
    get:
      summary: Obtener juego por ID
      description: Retorna los detalles de un juego específico
      operationId: getGameById
      tags:
        - Games
      parameters:
        - name: id
          in: path
          description: ID del juego
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Juego recuperado exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/GameDTO'
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '404':
          $ref: '#/components/responses/NotFoundError'
        '500':
          $ref: '#/components/responses/InternalServerError'
      security:
        - oauth2:
            - read:games
    put:
      summary: Actualizar juego
      description: Actualiza los datos de un juego existente
      operationId: updateGame
      tags:
        - Games
      parameters:
        - name: id
          in: path
          description: ID del juego a actualizar
          required: true
          schema:
            type: integer
            format: int64
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UpdateGameDTO'
      responses:
        '200':
          description: Juego actualizado exitosamente
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/GameDTO'
        '400':
          $ref: '#/components/responses/BadRequestError'
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '404':
          $ref: '#/components/responses/NotFoundError'
        '500':
          $ref: '#/components/responses/InternalServerError'
      security:
        - oauth2:
            - write:games
    delete:
      summary: Eliminar juego
      description: Elimina un juego del sistema
      operationId: deleteGame
      tags:
        - Games
      parameters:
        - name: id
          in: path
          description: ID del juego a eliminar
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '204':
          description: Juego eliminado exitosamente
        '401':
          $ref: '#/components/responses/UnauthorizedError'
        '404':
          $ref: '#/components/responses/NotFoundError'
        '500':
          $ref: '#/components/responses/InternalServerError'
      security:
        - oauth2:
            - delete:games

components:
  schemas:
    GameDTO:
      type: object
      description: Representación completa de un juego
      required:
        - id
        - name
        - description
        - releaseDate
        - createdAt
        - updatedAt
      properties:
        id:
          type: integer
          format: int64
          description: Identificador único del juego
          example: 1
        name:
          type: string
          description: Nombre del juego
          minLength: 1
          maxLength: 255
          example: "The Legend of Zelda"
        description:
          type: string
          description: Descripción del juego
          minLength: 1
          maxLength: 1000
          example: "Un clásico juego de aventura"
        releaseDate:
          type: string
          format: date
          description: Fecha de lanzamiento
          example: "1986-02-21"
        createdAt:
          type: string
          format: date-time
          description: Fecha de creación del registro
          readOnly: true
          example: "2024-01-15T10:30:00Z"
        updatedAt:
          type: string
          format: date-time
          description: Fecha de última actualización
          readOnly: true
          example: "2024-01-15T10:30:00Z"

    CreateGameDTO:
      type: object
      description: Datos para crear un nuevo juego
      required:
        - name
        - description
        - releaseDate
      properties:
        name:
          type: string
          description: Nombre del juego
          minLength: 1
          maxLength: 255
          example: "The Legend of Zelda"
        description:
          type: string
          description: Descripción del juego
          minLength: 1
          maxLength: 1000
          example: "Un clásico juego de aventura"
        releaseDate:
          type: string
          format: date
          description: Fecha de lanzamiento
          example: "1986-02-21"

    UpdateGameDTO:
      type: object
      description: Datos para actualizar un juego existente
      properties:
        name:
          type: string
          description: Nombre del juego
          minLength: 1
          maxLength: 255
          example: "The Legend of Zelda: A Link to the Past"
        description:
          type: string
          description: Descripción del juego
          minLength: 1
          maxLength: 1000
          example: "Un clásico juego de aventura mejorado"
        releaseDate:
          type: string
          format: date
          description: Fecha de lanzamiento
          example: "1991-11-21"

    ErrorResponse:
      type: object
      description: Respuesta de error estándar
      required:
        - status
        - message
        - timestamp
      properties:
        status:
          type: integer
          description: Código de estado HTTP
          example: 404
        message:
          type: string
          description: Mensaje de error
          example: "Juego no encontrado"
        details:
          type: string
          description: Detalles adicionales del error
          example: "No existe juego con ID 999"
        timestamp:
          type: string
          format: date-time
          description: Marca de tiempo del error
          example: "2024-01-15T10:30:00Z"
        path:
          type: string
          description: Ruta del endpoint que causó el error
          example: "/api/games/999"

  responses:
    BadRequestError:
      description: Solicitud inválida
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            status: 400
            message: "Solicitud inválida"
            details: "El campo 'name' es requerido"
            timestamp: "2024-01-15T10:30:00Z"
            path: "/api/games"

    UnauthorizedError:
      description: No autorizado - Se requiere autenticación
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            status: 401
            message: "No autenticado"
            details: "Token de acceso ausente o inválido"
            timestamp: "2024-01-15T10:30:00Z"
            path: "/api/games"

    NotFoundError:
      description: Recurso no encontrado
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            status: 404
            message: "Juego no encontrado"
            details: "No existe juego con ID 999"
            timestamp: "2024-01-15T10:30:00Z"
            path: "/api/games/999"

    InternalServerError:
      description: Error interno del servidor
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/ErrorResponse'
          example:
            status: 500
            message: "Error interno del servidor"
            details: "Ocurrió un error inesperado procesando la solicitud"
            timestamp: "2024-01-15T10:30:00Z"
            path: "/api/games"

  securitySchemes:
    oauth2:
      type: oauth2
      description: OAuth 2.0 - Authorization Code Flow
      flows:
        authorizationCode:
          authorizationUrl: https://auth.example.com/oauth/authorize
          tokenUrl: https://auth.example.com/oauth/token
          refreshUrl: https://auth.example.com/oauth/refresh
          scopes:
            read:games: Leer información de juegos
            write:games: Crear y actualizar juegos
            delete:games: Eliminar juegos

security:
  - oauth2:
      - read:games
      - write:games
```

## Validación de la Especificación

Para validar que el archivo YAML es correcto, ejecutar:

```bash
# Instalar Spectral (si no está instalado)
npm install -g @stoplight/spectral

# Validar el archivo
spectral lint src/main/resources/api/openapi.yaml
```

Alternativamente, usar Swagger Editor online: https://editor.swagger.io/

## Próximos Pasos

1. Crear el archivo en `src/main/resources/api/openapi.yaml`
2. Copiar el contenido YAML anterior
3. Ejecutar `mvn clean compile` para generar código desde la especificación
4. Verificar que el código generado aparece en `target/generated-sources/openapi/`

## Notas Importantes

- **Versión OpenAPI:** Se usa 3.1.0 (última versión estable)
- **Formato de cambios:** Cambios en esta especificación requieren regeneración de código
- **Versionado:** La API version es 1.0.0. Para cambios breaking en el futuro, incrementar a 2.0.0
- **Seguridad:** OAuth2 se define pero debe configurarse correctamente en `SecurityConfig.java`
- **Validación:** Los esquemas incluyen restricciones de tamaño, formato y campos requeridos

---

**Última actualización:** 2026-04-12

