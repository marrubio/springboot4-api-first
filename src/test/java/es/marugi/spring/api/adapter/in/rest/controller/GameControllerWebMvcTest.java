package es.marugi.spring.api.adapter.in.rest.controller;

import es.marugi.spring.api.adapter.in.rest.dto.CreateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.dto.GameResponseDTO;
import es.marugi.spring.api.adapter.in.rest.dto.UpdateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.error.ApiErrorResponse;
import es.marugi.spring.api.adapter.in.rest.error.GlobalExceptionHandler;
import es.marugi.spring.api.adapter.in.rest.mapper.GameRestMapper;
import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import es.marugi.spring.api.application.exception.GameNotFoundException;
import es.marugi.spring.api.application.service.GameCommandService;
import es.marugi.spring.api.application.service.GameQueryService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerWebMvcTest {

    @Mock
    private GameQueryService gameQueryService;

    @Mock
    private GameCommandService gameCommandService;

    @Mock
    private GameRestMapper gameRestMapper;

    private Validator validator;
    private GlobalExceptionHandler globalExceptionHandler;
    private GameController gameController;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        globalExceptionHandler = new GlobalExceptionHandler();
        gameController = new GameController(gameQueryService, gameCommandService, gameRestMapper);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getGamesReturnsMappedResponse() {
        GameDTO gameDto = new GameDTO(8.0, 2026, "Arcade game", "Test Game", 1L, LocalDateTime.of(2026, 1, 1, 10, 0));
        GameResponseDTO responseDto = new GameResponseDTO(1L, "Test Game", "Arcade game", 8, 2026, LocalDateTime.of(2026, 1, 1, 10, 0));

        when(gameQueryService.getAllGames()).thenReturn(List.of(gameDto));
        when(gameRestMapper.toResponseDTO(gameDto)).thenReturn(responseDto);

        List<GameResponseDTO> response = gameController.getGames();

        assertThat(response)
            .hasSize(1)
            .first()
            .extracting(GameResponseDTO::id, GameResponseDTO::title, GameResponseDTO::score)
            .containsExactly(1L, "Test Game", 8);
    }

    @Test
    void getGameByIdReturnsOkResponse() {
        GameDTO gameDto = new GameDTO(8.0, 2026, "Arcade game", "Test Game", 1L, LocalDateTime.of(2026, 1, 1, 10, 0));
        GameResponseDTO responseDto = new GameResponseDTO(1L, "Test Game", "Arcade game", 8, 2026, LocalDateTime.of(2026, 1, 1, 10, 0));

        when(gameQueryService.getGameById(1L)).thenReturn(gameDto);
        when(gameRestMapper.toResponseDTO(gameDto)).thenReturn(responseDto);

        ResponseEntity<GameResponseDTO> response = gameController.getGameById(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    void createGameReturnsCreatedWithLocation() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/games");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setScheme("http");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        CreateGameRequestDTO createRequest = new CreateGameRequestDTO("Created Game", "Created description", 2026, 8.0);
        CreateGameDTO createGameDTO = new CreateGameDTO("Created Game", "Created description", 2026, 8.0);
        GameDTO createdGame = new GameDTO(8.0, 2026, "Created description", "Created Game", 15L, LocalDateTime.of(2026, 1, 1, 12, 0));
        GameResponseDTO responseDto = new GameResponseDTO(15L, "Created Game", "Created description", 8, 2026, LocalDateTime.of(2026, 1, 1, 12, 0));

        when(gameRestMapper.toDto(createRequest)).thenReturn(createGameDTO);
        when(gameCommandService.createGame(createGameDTO)).thenReturn(createdGame);
        when(gameRestMapper.toResponseDTO(createdGame)).thenReturn(responseDto);

        ResponseEntity<GameResponseDTO> response = gameController.createGame(createRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).hasToString("http://localhost:8080/games/15");
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    void updateGameReturnsOk() {
        UpdateGameRequestDTO request = new UpdateGameRequestDTO("Updated Game", "Updated description", 2026, 9.0);
        UpdateGameDTO updateGameDTO = new UpdateGameDTO("Updated Game", "Updated description", 2026, 9.0);
        GameDTO updatedGame = new GameDTO(9.0, 2026, "Updated description", "Updated Game", 7L, LocalDateTime.of(2026, 1, 1, 13, 0));
        GameResponseDTO responseDto = new GameResponseDTO(7L, "Updated Game", "Updated description", 9, 2026, LocalDateTime.of(2026, 1, 1, 13, 0));

        when(gameRestMapper.toDto(request)).thenReturn(updateGameDTO);
        when(gameCommandService.updateGame(7L, updateGameDTO)).thenReturn(updatedGame);
        when(gameRestMapper.toResponseDTO(updatedGame)).thenReturn(responseDto);

        ResponseEntity<GameResponseDTO> response = gameController.updateGame(7L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(responseDto);
    }

    @Test
    void createGameRequestValidationRejectsInvalidPayload() {
        CreateGameRequestDTO invalidRequest = new CreateGameRequestDTO("", "Valid description", 2026, 11.0);

        Set<ConstraintViolation<CreateGameRequestDTO>> violations = validator.validate(invalidRequest);

        assertThat(violations)
            .extracting(violation -> violation.getPropertyPath().toString())
            .contains("title", "score");
    }

    @Test
    void gameNotFoundExceptionIsMappedToNotFoundResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/games/99");
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGameNotFoundException(
            new GameNotFoundException(99L),
            request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Game with id 99 not found");
        assertThat(response.getBody().path()).isEqualTo("/games/99");
    }

    @Test
    void genericExceptionIsMappedToInternalServerErrorResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/games/1");
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGenericException(
            new IllegalStateException("boom"),
            request
        );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().path()).isEqualTo("/games/1");
    }
}

