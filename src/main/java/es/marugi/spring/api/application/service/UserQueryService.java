package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.UserDTO;

import java.util.List;

public interface UserQueryService {
    List<UserDTO> getAllUsers();
}