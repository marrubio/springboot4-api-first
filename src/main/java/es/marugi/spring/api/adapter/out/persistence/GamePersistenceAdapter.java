package es.marugi.spring.api.adapter.out.persistence;

import es.marugi.spring.api.domain.model.Game;
import es.marugi.spring.api.domain.repository.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GamePersistenceAdapter implements GameRepository {

    private final GameJpaRepository gameRepository;

    public GamePersistenceAdapter(GameJpaRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game save(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }
}

