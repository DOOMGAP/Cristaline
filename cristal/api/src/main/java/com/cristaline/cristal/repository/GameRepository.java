package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
