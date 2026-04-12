package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;

public interface GameCommandService {
    GameDTO createGame(CreateGameDTO game);
    GameDTO updateGame(Long id, UpdateGameDTO updateGameDTO);
    void deleteGame(Long id);
}

