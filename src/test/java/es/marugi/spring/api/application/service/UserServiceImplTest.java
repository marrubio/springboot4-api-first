package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.CreateUserDTO;
import es.marugi.spring.api.application.dto.UpdateUserDTO;
import es.marugi.spring.api.application.dto.UserDTO;
import es.marugi.spring.api.application.exception.UserConflictException;
import es.marugi.spring.api.application.exception.UserNotFoundException;
import es.marugi.spring.api.application.mapper.UserMapper;
import es.marugi.spring.api.domain.model.User;
import es.marugi.spring.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final UserServiceImpl service = new UserServiceImpl(userRepository, userMapper);

    @Test
    void listsUsers() {
        User user = user(1L, "list-user", "list@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO(1L, "List User", "list-user", "list@example.com"));

        assertThat(service.getAllUsers()).containsExactly(new UserDTO(1L, "List User", "list-user", "list@example.com"));
    }

    @Test
    void createsUserWhenUnique() {
        CreateUserDTO request = new CreateUserDTO("New User", "new-user", "plainpass", "new@example.com");
        User entity = user(null, request.login(), request.email());
        User saved = user(2L, request.login(), request.email());
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(new UserDTO(2L, request.name(), request.login(), request.email()));

        assertThat(service.createUser(request).id()).isEqualTo(2L);
    }

    @Test
    void rejectsDuplicateLoginAndMissingUpdate() {
        CreateUserDTO create = new CreateUserDTO("Duplicate", "duplicate", "plainpass", "duplicate@example.com");
        when(userRepository.existsByLogin(create.login())).thenReturn(true);
        assertThatThrownBy(() -> service.createUser(create)).isInstanceOf(UserConflictException.class);

        CreateUserDTO duplicateEmail = new CreateUserDTO(
            "Duplicate Email", "different-login", "plainpass", "duplicate@example.com"
        );
        when(userRepository.existsByLogin(duplicateEmail.login())).thenReturn(false);
        when(userRepository.existsByEmail(duplicateEmail.email())).thenReturn(true);
        assertThatThrownBy(() -> service.createUser(duplicateEmail)).isInstanceOf(UserConflictException.class);

        UpdateUserDTO update = new UpdateUserDTO("Missing", "missing", "plainpass", "missing@example.com");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateUser(99L, update)).isInstanceOf(UserNotFoundException.class);
    }

    private User user(Long id, String login, String email) {
        User user = new User();
        user.setId(id);
        user.setName("Test User");
        user.setLogin(login);
        user.setPassword("plainpass");
        user.setEmail(email);
        return user;
    }
}