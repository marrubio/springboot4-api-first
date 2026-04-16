package es.marugi.spring.api.adapter.in.rest.mapper;

import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameRestMapper {

    @Mapping(source = "genre", target = "description")
    @Mapping(source = "releaseYear", target = "developmentYear")
    @Mapping(target = "score", ignore = true)
    CreateGameDTO toApplicationDto(es.marugi.spring.api.generated.model.CreateGameDTO dto);

    @Mapping(source = "genre", target = "description")
    @Mapping(source = "releaseYear", target = "developmentYear")
    @Mapping(target = "score", ignore = true)
    UpdateGameDTO toApplicationDto(es.marugi.spring.api.generated.model.UpdateGameDTO dto);

    @Mapping(source = "description", target = "genre")
    @Mapping(source = "developmentYear", target = "releaseYear")
    @Mapping(target = "platform", ignore = true)
    es.marugi.spring.api.generated.model.GameDTO toGeneratedDto(GameDTO dto);
}

