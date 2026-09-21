package es.marugi.spring.api.adapter.in.rest;

import es.marugi.spring.api.adapter.in.rest.mapper.UserRestMapper;
import es.marugi.spring.api.application.service.UserCommandService;
import es.marugi.spring.api.application.service.UserQueryService;
import es.marugi.spring.api.generated.api.UsersApiDelegate;
import es.marugi.spring.api.generated.model.CreateUserDTO;
import es.marugi.spring.api.generated.model.UpdateUserDTO;
import es.marugi.spring.api.generated.model.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersApiDelegateImpl implements UsersApiDelegate {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final UserRestMapper userRestMapper;

    public UsersApiDelegateImpl(
        UserCommandService userCommandService,
        UserQueryService userQueryService,
        UserRestMapper userRestMapper
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.userRestMapper = userRestMapper;
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsers() {
        return ResponseEntity.ok(userQueryService.getAllUsers().stream()
            .map(userRestMapper::toGeneratedDto)
            .toList());
    }

    @Override
    public ResponseEntity<UserDTO> createUser(CreateUserDTO createUserDTO) {
        UserDTO created = userRestMapper.toGeneratedDto(
            userCommandService.createUser(userRestMapper.toApplicationDto(createUserDTO))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<UserDTO> updateUser(UpdateUserDTO updateUserDTO, Long id) {
        UserDTO updated = userRestMapper.toGeneratedDto(
            userCommandService.updateUser(id, userRestMapper.toApplicationDto(updateUserDTO))
        );
        return ResponseEntity.ok(updated);
    }
}