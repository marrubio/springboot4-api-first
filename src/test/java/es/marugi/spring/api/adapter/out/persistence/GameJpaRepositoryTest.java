package es.marugi.spring.api.adapter.out.persistence;

import es.marugi.spring.api.DemoApplication;
import es.marugi.spring.api.domain.model.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
class GameJpaRepositoryTest {

    @Autowired
    private GameJpaRepository gameJpaRepository;

    @Test
    void saveAndFindByIdPersistsGame() {
        Game saved = gameJpaRepository.saveAndFlush(buildGame("Persisted Game", 8.0));

        assertThat(saved.getId()).isNotNull();
        assertThat(gameJpaRepository.findById(saved.getId()))
            .isPresent()
            .get()
            .extracting(Game::getTitle)
            .isEqualTo("Persisted Game");
    }

    @Test
    void findAllReturnsPersistedGames() {
        gameJpaRepository.saveAndFlush(buildGame("Game One", 8.0));
        gameJpaRepository.saveAndFlush(buildGame("Game Two", 9.0));

        assertThat(gameJpaRepository.findAll())
            .extracting(Game::getTitle)
            .contains("Game One", "Game Two");
    }

    @Test
    void deleteByIdRemovesPersistedGame() {
        Game saved = gameJpaRepository.saveAndFlush(buildGame("Disposable Game", 6.0));

        gameJpaRepository.deleteById(saved.getId());
        gameJpaRepository.flush();

        assertThat(gameJpaRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void savingDuplicateTitleThrowsDataIntegrityViolationException() {
        gameJpaRepository.saveAndFlush(buildGame("Unique Title", 8.0));

        assertThatThrownBy(() -> gameJpaRepository.saveAndFlush(buildGame("Unique Title", 9.0)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Game buildGame(String title, Double score) {
        Game game = new Game();
        game.setTitle(title);
        game.setDescription(title + " description");
        game.setDevelopmentYear(2026);
        game.setScore(score);
        game.setRecordedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        return game;
    }
}
