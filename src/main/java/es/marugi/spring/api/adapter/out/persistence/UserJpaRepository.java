package es.marugi.spring.api.adapter.out.persistence;

import es.marugi.spring.api.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLoginAndIdNot(String login, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}