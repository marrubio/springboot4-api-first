package es.marugi.spring.api.adapter.in.rest.mapper;

import es.marugi.spring.api.application.dto.CreateUserDTO;
import es.marugi.spring.api.application.dto.UpdateUserDTO;
import es.marugi.spring.api.application.dto.UserDTO;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserRestMapper {
    CreateUserDTO toApplicationDto(es.marugi.spring.api.generated.model.CreateUserDTO dto);
    UpdateUserDTO toApplicationDto(es.marugi.spring.api.generated.model.UpdateUserDTO dto);

    es.marugi.spring.api.generated.model.UserDTO toGeneratedDto(UserDTO dto);
}