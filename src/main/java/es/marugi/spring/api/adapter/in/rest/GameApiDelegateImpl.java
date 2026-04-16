package es.marugi.spring.api.adapter.in.rest;

import es.marugi.spring.api.adapter.in.rest.mapper.GameRestMapper;
import es.marugi.spring.api.application.service.GameCommandService;
import es.marugi.spring.api.application.service.GameQueryService;
import es.marugi.spring.api.generated.api.V1ApiDelegate;
import es.marugi.spring.api.generated.model.CreateGameDTO;
import es.marugi.spring.api.generated.model.GameDTO;
import es.marugi.spring.api.generated.model.UpdateGameDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameApiDelegateImpl implements V1ApiDelegate {

    private final GameCommandService gameCommandService;
    private final GameQueryService gameQueryService;
    private final GameRestMapper gameRestMapper;

    public GameApiDelegateImpl(
        GameCommandService gameCommandService,
        GameQueryService gameQueryService,
        GameRestMapper gameRestMapper
    ) {
        this.gameCommandService = gameCommandService;
        this.gameQueryService = gameQueryService;
        this.gameRestMapper = gameRestMapper;
    }

    @Override
    public ResponseEntity<List<GameDTO>> listGames(Integer page, Integer size, String sort) {
        List<GameDTO> games = gameQueryService.getAllGames().stream()
            .map(gameRestMapper::toGeneratedDto)
            .toList();
        return ResponseEntity.ok(games);
    }

    @Override
    public ResponseEntity<GameDTO> getGameById(Long id) {
        GameDTO game = gameRestMapper.toGeneratedDto(gameQueryService.getGameById(id));
        return ResponseEntity.ok(game);
    }

    @Override
    public ResponseEntity<GameDTO> createGame(CreateGameDTO createGameDTO) {
        GameDTO created = gameRestMapper.toGeneratedDto(
            gameCommandService.createGame(gameRestMapper.toApplicationDto(createGameDTO))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<GameDTO> updateGame(UpdateGameDTO updateGameDTO, Long id) {
        GameDTO updated = gameRestMapper.toGeneratedDto(
            gameCommandService.updateGame(id, gameRestMapper.toApplicationDto(updateGameDTO))
        );
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<Void> deleteGame(Long id) {
        gameCommandService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}

