package es.marugi.spring.api.application.service;

import es.marugi.spring.api.application.dto.CreateUserDTO;
import es.marugi.spring.api.application.dto.UpdateUserDTO;
import es.marugi.spring.api.application.dto.UserDTO;
import es.marugi.spring.api.application.exception.UserConflictException;
import es.marugi.spring.api.application.exception.UserNotFoundException;
import es.marugi.spring.api.application.mapper.UserMapper;
import es.marugi.spring.api.domain.model.User;
import es.marugi.spring.api.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserQueryService, UserCommandService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO user) {
        ensureUnique(user.login(), user.email(), null);
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO user) {
        User existing = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        ensureUnique(user.login(), user.email(), id);
        existing.setName(user.name());
        existing.setLogin(user.login());
        existing.setPassword(user.password());
        existing.setEmail(user.email());
        return userMapper.toDTO(userRepository.save(existing));
    }

    private void ensureUnique(String login, String email, Long currentId) {
        boolean loginInUse = currentId == null
            ? userRepository.existsByLogin(login)
            : userRepository.existsByLoginAndIdNot(login, currentId);
        boolean emailInUse = currentId == null
            ? userRepository.existsByEmail(email)
            : userRepository.existsByEmailAndIdNot(email, currentId);
        if (loginInUse || emailInUse) {
            throw new UserConflictException();
        }
    }
}