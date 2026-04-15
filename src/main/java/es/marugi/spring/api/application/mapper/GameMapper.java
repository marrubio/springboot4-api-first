package es.marugi.spring.api.application.mapper;

import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO toGameDTO(Game game);

    Game toGame(GameDTO gameDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedAt", ignore = true)
    Game toGame(CreateGameDTO gameDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedAt", ignore = true)
    Game toGame(UpdateGameDTO gameDTO);

    default GameDTO toDTO(Game game) {
        return toGameDTO(game);
    }

    default Game toEntity(GameDTO gameDTO) {
        return toGame(gameDTO);
    }

    default Game toEntity(CreateGameDTO gameDTO) {
        return toGame(gameDTO);
    }

    default Game toEntity(UpdateGameDTO gameDTO) {
        return toGame(gameDTO);
    }
}

