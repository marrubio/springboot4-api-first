package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.CreateUserDTO;
import es.marugi.spring.api.application.dto.UpdateUserDTO;
import es.marugi.spring.api.application.dto.UserDTO;

public interface UserCommandService {
    UserDTO createUser(CreateUserDTO user);
    UserDTO updateUser(Long id, UpdateUserDTO user);
}