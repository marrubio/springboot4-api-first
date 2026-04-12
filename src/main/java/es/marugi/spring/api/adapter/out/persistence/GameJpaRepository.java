package es.marugi.spring.api.adapter.out.persistence;

import es.marugi.spring.api.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameJpaRepository extends JpaRepository<Game, Long> {
}

