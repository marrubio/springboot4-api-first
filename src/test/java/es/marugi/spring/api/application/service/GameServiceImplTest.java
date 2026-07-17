package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import es.marugi.spring.api.application.event.GameCreatedEvent;
import es.marugi.spring.api.application.event.GameDeletedEvent;
import es.marugi.spring.api.application.event.GameEvent;
import es.marugi.spring.api.application.event.GameUpdatedEvent;
import es.marugi.spring.api.application.exception.GameNotFoundException;
import es.marugi.spring.api.application.mapper.GameMapper;
import es.marugi.spring.api.application.port.GameEventOutboxWriter;
import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.domain.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private GameEventOutboxWriter eventOutboxWriter;

    @Mock
    private Clock clock;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void getAllGamesReturnsMappedGames() {
        Game firstGame = buildGame(1L, "First Game", 8.0);
        Game secondGame = buildGame(2L, "Second Game", 9.0);
        GameDTO firstDto = buildGameDto(1L, "First Game", 8.0);
        GameDTO secondDto = buildGameDto(2L, "Second Game", 9.0);

        when(gameRepository.findAll()).thenReturn(List.of(firstGame, secondGame));
        when(gameMapper.toDTO(firstGame)).thenReturn(firstDto);
        when(gameMapper.toDTO(secondGame)).thenReturn(secondDto);

        List<GameDTO> result = gameService.getAllGames();

        assertThat(result).containsExactly(firstDto, secondDto);
    }

    @Test
    void getGameByIdReturnsMappedGameWhenItExists() {
        Game game = buildGame(1L, "Existing Game", 8.5);
        GameDTO expected = buildGameDto(1L, "Existing Game", 8.5);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameMapper.toDTO(game)).thenReturn(expected);

        GameDTO result = gameService.getGameById(1L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getGameByIdThrowsWhenGameDoesNotExist() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGameById(99L))
            .isInstanceOf(GameNotFoundException.class)
            .hasMessage("Game with id 99 not found");
    }

    @Test
    void createGameSetsRecordedAtAndReturnsMappedGame() {
        CreateGameDTO request = new CreateGameDTO("Created Game", "Created description", 2026, 9.0);
        Game entityToCreate = new Game();
        entityToCreate.setTitle("Created Game");
        entityToCreate.setDescription("Created description");
        entityToCreate.setDevelopmentYear(2026);
        entityToCreate.setScore(9.0);

        Game savedGame = buildGame(1L, "Created Game", 9.0);
        GameDTO expected = buildGameDto(1L, "Created Game", 9.0);

        when(gameMapper.toEntity(request)).thenReturn(entityToCreate);
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);
        when(gameMapper.toDTO(savedGame)).thenReturn(expected);
        when(clock.instant()).thenReturn(Instant.parse("2026-07-17T10:00:00Z"));

        GameDTO result = gameService.createGame(request);

        ArgumentCaptor<Game> savedCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(savedCaptor.capture());
        assertThat(savedCaptor.getValue().getRecordedAt()).isNotNull();
        assertThat(savedCaptor.getValue().getTitle()).isEqualTo("Created Game");
        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<GameEvent> eventCaptor = ArgumentCaptor.forClass(GameEvent.class);
        verify(eventOutboxWriter).enqueue(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(GameCreatedEvent.class);
        assertThat(eventCaptor.getValue().snapshot().title()).isEqualTo("Created Game");
    }

    @Test
    void updateGameUpdatesFieldsAndReturnsMappedGame() {
        Game existingGame = buildGame(1L, "Old Title", 6.0);
        UpdateGameDTO request = new UpdateGameDTO("Updated Title", "Updated description", 2027, 9.5);
        Game updatedGame = buildGame(1L, "Updated Title", 9.5);
        GameDTO expected = buildGameDto(1L, "Updated Title", 9.5);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(existingGame)).thenReturn(updatedGame);
        when(gameMapper.toDTO(updatedGame)).thenReturn(expected);
        when(clock.instant()).thenReturn(Instant.parse("2026-07-17T10:00:00Z"));

        GameDTO result = gameService.updateGame(1L, request);

        assertThat(existingGame.getTitle()).isEqualTo("Updated Title");
        assertThat(existingGame.getDescription()).isEqualTo("Updated description");
        assertThat(existingGame.getDevelopmentYear()).isEqualTo(2027);
        assertThat(existingGame.getScore()).isEqualTo(9.5);
        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<GameEvent> eventCaptor = ArgumentCaptor.forClass(GameEvent.class);
        verify(eventOutboxWriter).enqueue(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(GameUpdatedEvent.class);
        assertThat(eventCaptor.getValue().snapshot().title()).isEqualTo("Updated Title");
    }

    @Test
    void updateGameThrowsWhenGameDoesNotExist() {
        when(gameRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.updateGame(55L, new UpdateGameDTO("Title", "Description", 2025, 7.0)))
            .isInstanceOf(GameNotFoundException.class)
            .hasMessage("Game with id 55 not found");

        verify(gameRepository, never()).save(any(Game.class));
        verify(eventOutboxWriter, never()).enqueue(any(GameEvent.class));
    }

    @Test
    void deleteGameDeletesExistingGame() {
        Game existingGame = buildGame(3L, "Delete Me", 5.0);
        when(gameRepository.findById(3L)).thenReturn(Optional.of(existingGame));
        when(clock.instant()).thenReturn(Instant.parse("2026-07-17T10:00:00Z"));

        gameService.deleteGame(3L);

        verify(gameRepository).deleteById(3L);
        ArgumentCaptor<GameEvent> eventCaptor = ArgumentCaptor.forClass(GameEvent.class);
        verify(eventOutboxWriter).enqueue(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(GameDeletedEvent.class);
        assertThat(eventCaptor.getValue().snapshot().title()).isEqualTo("Delete Me");
    }

    @Test
    void deleteGameThrowsWhenGameDoesNotExist() {
        when(gameRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.deleteGame(77L))
            .isInstanceOf(GameNotFoundException.class)
            .hasMessage("Game with id 77 not found");

        verify(gameRepository, never()).deleteById(any(Long.class));
        verify(eventOutboxWriter, never()).enqueue(any(GameEvent.class));
    }

    private Game buildGame(Long id, String title, Double score) {
        Game game = new Game();
        game.setId(id);
        game.setTitle(title);
        game.setDescription(title + " description");
        game.setDevelopmentYear(2026);
        game.setScore(score);
        game.setRecordedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        return game;
    }

    private GameDTO buildGameDto(Long id, String title, Double score) {
        return new GameDTO(
            score,
            2026,
            title + " description",
            title,
            id,
            LocalDateTime.of(2026, 1, 1, 10, 0)
        );
    }
}

