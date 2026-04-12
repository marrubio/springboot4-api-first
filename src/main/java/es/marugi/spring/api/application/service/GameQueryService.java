package es.marugi.spring.api.application.service;


import es.marugi.spring.api.application.dto.GameDTO;

import java.util.List;

public interface GameQueryService {
    List<GameDTO> getAllGames();
    GameDTO getGameById(Long id);
}

