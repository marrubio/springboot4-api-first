package es.marugi.spring.api.application.mapper;

import es.marugi.spring.api.application.dto.CreateUserDTO;
import es.marugi.spring.api.application.dto.UpdateUserDTO;
import es.marugi.spring.api.application.dto.UserDTO;
import es.marugi.spring.api.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDTO user);
}