package es.marugi.spring.api.adapter.in.rest.controller;

import es.marugi.spring.api.adapter.in.rest.dto.GameResponseDTO;
import es.marugi.spring.api.adapter.in.rest.dto.CreateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.dto.UpdateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.mapper.GameRestMapper;
import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.service.GameCommandService;
import es.marugi.spring.api.application.service.GameQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/games")
@Deprecated(since = "API First migration to /v1/games")
public class GameController {
    private final GameQueryService gameQueryService;
    private final GameCommandService gameCommandService;
    private final GameRestMapper gameMapper;

    public GameController(
        GameQueryService gameQueryService,
        GameCommandService gameCommandService,
        GameRestMapper gameMapper
    ) {
        this.gameQueryService = gameQueryService;
        this.gameCommandService = gameCommandService;
        this.gameMapper = gameMapper;
    }

    @GetMapping
    public List<GameResponseDTO> getGames() {
        return gameQueryService.getAllGames().stream()
            .map(gameMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getGameById(@PathVariable Long id) {
        GameDTO game = gameQueryService.getGameById(id);
        return ResponseEntity.ok(gameMapper.toResponseDTO(game));
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@Valid @RequestBody CreateGameRequestDTO createRequest) {
        CreateGameDTO createGameDTO = gameMapper.toDto(createRequest);
        GameDTO created = gameCommandService.createGame(createGameDTO);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();

        return ResponseEntity
            .created(location)
            .body(gameMapper.toResponseDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponseDTO> updateGame(@PathVariable Long id, @Valid @RequestBody UpdateGameRequestDTO updateRequest) {
        GameDTO updated = gameCommandService.updateGame(id, gameMapper.toDto(updateRequest));
        return ResponseEntity.ok(gameMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameCommandService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

}
