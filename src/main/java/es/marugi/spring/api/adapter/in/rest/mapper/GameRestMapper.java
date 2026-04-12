package es.marugi.spring.api.adapter.in.rest.mapper;

import es.marugi.spring.api.adapter.in.rest.dto.GameResponseDTO;
import es.marugi.spring.api.adapter.in.rest.dto.UpdateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.dto.CreateGameRequestDTO;
import es.marugi.spring.api.application.dto.CreateGameDTO;
import es.marugi.spring.api.application.dto.GameDTO;
import es.marugi.spring.api.application.dto.UpdateGameDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameRestMapper {
    CreateGameDTO toDto(CreateGameRequestDTO dto);
    UpdateGameDTO toDto(UpdateGameRequestDTO dto);
    GameResponseDTO toResponseDTO(GameDTO dto);
}

