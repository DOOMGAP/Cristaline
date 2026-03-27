package com.cristaline.cristal.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.cristaline.cristal.model.Game;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    void shouldPersistAndReadGame() {
        Game savedGame = gameRepository.save(new Game(
            null,
            null,
            "Outer Wilds",
            "Adventure",
            2019,
            "Space exploration mystery.",
            "https://example.com/outer-wilds.jpg"
        ));

        List<Game> games = gameRepository.findAll();

        assertThat(games)
            .extracting(Game::getId, Game::getApiId, Game::getTitle, Game::getGenre, Game::getReleaseYear)
            .contains(tuple(savedGame.getId(), null, "Outer Wilds", "Adventure", 2019));
    }
}
