package es.marugi.spring.api.adapter.out.persistence;

import es.marugi.spring.api.DemoApplication;
import es.marugi.spring.api.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void savesFindsAndListsUsers() {
        User saved = userJpaRepository.saveAndFlush(buildUser("repository-user", "repository@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(userJpaRepository.findById(saved.getId())).isPresent();
        assertThat(userJpaRepository.findAll()).extracting(User::getLogin).contains("repository-user");
    }

    @Test
    void databaseRejectsDuplicateLoginAndEmail() {
        userJpaRepository.saveAndFlush(buildUser("duplicate-user", "duplicate@example.com"));

        assertThatThrownBy(() -> userJpaRepository.saveAndFlush(buildUser("duplicate-user", "other@example.com")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void databaseRejectsDuplicateEmail() {
        userJpaRepository.saveAndFlush(buildUser("first-user", "same@example.com"));

        assertThatThrownBy(() -> userJpaRepository.saveAndFlush(buildUser("second-user", "same@example.com")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private User buildUser(String login, String email) {
        User user = new User();
        user.setName("Repository User");
        user.setLogin(login);
        user.setPassword("plainpass");
        user.setEmail(email);
        return user;
    }
}