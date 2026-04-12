package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.exception.GameNotFoundException;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.mapper.GameMapper;
import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import es.marugi.spring.api.domain.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameQueryService, GameCommandService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll().stream()
                .map(gameMapper::toDTO)
                .collect(Collectors.toList());
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
        logger.info("Game updated with id {} and title '{}'", updatedGame.getId(), updatedGame.getTitle());
        return gameMapper.toDTO(updatedGame);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        Game game = findGameOrThrow(id);
        gameRepository.deleteById(id);
        logger.info("Game deleted with id {} and title '{}'", game.getId(), game.getTitle());
    }

    private Game findGameOrThrow(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Game not found for id {}", id);
                return new GameNotFoundException(id);
            });
    }

}

