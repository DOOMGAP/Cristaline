package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.Game;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    Optional<Game> findByApiId(Long apiId);
    List<Game> findAllByApiIdNotNull();
}
