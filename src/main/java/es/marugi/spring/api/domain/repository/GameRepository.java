package es.marugi.spring.api.domain.repository;

import es.marugi.spring.api.domain.model.Game;
import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(Long id);
    List<Game> findAll();
    void deleteById(Long id);
}

