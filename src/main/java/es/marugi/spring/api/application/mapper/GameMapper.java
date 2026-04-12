package es.marugi.spring.api.application.mapper;

import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.application.dto.GameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO toDTO(Game game);
    Game toEntity(GameDTO gameDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedAt", ignore = true)
    Game toEntity(CreateGameDTO gameDTO);
}

