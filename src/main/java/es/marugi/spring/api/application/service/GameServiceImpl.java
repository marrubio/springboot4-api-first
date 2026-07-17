package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.exception.GameNotFoundException;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.event.GameCreatedEvent;
import es.marugi.spring.api.application.event.GameDeletedEvent;
import es.marugi.spring.api.application.event.GameSnapshot;
import es.marugi.spring.api.application.event.GameUpdatedEvent;
import es.marugi.spring.api.application.mapper.GameMapper;
import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.port.GameEventOutboxWriter;
import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import es.marugi.spring.api.domain.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameQueryService, GameCommandService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final GameEventOutboxWriter eventOutboxWriter;
    private final Clock clock;

    public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper, GameEventOutboxWriter eventOutboxWriter,
                           Clock clock) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.eventOutboxWriter = eventOutboxWriter;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        Game game = findGameOrThrow(id);
        return gameMapper.toDTO(game);
    }

    @Override
    @Transactional
    public GameDTO createGame(CreateGameDTO game) {
        Game entity = gameMapper.toEntity(game);
        entity.setRecordedAt(LocalDateTime.now());
        Game savedGame = gameRepository.save(entity);
        eventOutboxWriter.enqueue(new GameCreatedEvent(UUID.randomUUID(), savedGame.getId(), Instant.now(clock), snapshotOf(savedGame)));
        logger.info("Game created with id {} and title '{}'", savedGame.getId(), savedGame.getTitle());
        return gameMapper.toDTO(savedGame);
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, UpdateGameDTO gameDTO) {
        Game existing = findGameOrThrow(id);
        existing.setTitle(gameDTO.title());
        existing.setDescription(gameDTO.description());
        existing.setDevelopmentYear(gameDTO.developmentYear());
        existing.setScore(gameDTO.score());
        Game updatedGame = gameRepository.save(existing);
        eventOutboxWriter.enqueue(new GameUpdatedEvent(UUID.randomUUID(), updatedGame.getId(), Instant.now(clock), snapshotOf(updatedGame)));
        logger.info("Game updated with id {} and title '{}'", updatedGame.getId(), updatedGame.getTitle());
        return gameMapper.toDTO(updatedGame);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        Game game = findGameOrThrow(id);
        gameRepository.deleteById(id);
        eventOutboxWriter.enqueue(new GameDeletedEvent(UUID.randomUUID(), game.getId(), Instant.now(clock), snapshotOf(game)));
        logger.info("Game deleted with id {} and title '{}'", game.getId(), game.getTitle());
    }

    private Game findGameOrThrow(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Game not found for id {}", id);
                return new GameNotFoundException(id);
            });
    }

    private GameSnapshot snapshotOf(Game game) {
        return new GameSnapshot(game.getId(), game.getTitle(), game.getDescription(), game.getScore(),
            game.getDevelopmentYear(), game.getRecordedAt());
    }

}

