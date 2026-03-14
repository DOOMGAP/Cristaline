package com.cristaline.cristal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cristaline.cristal.dto.GameRequest;
import com.cristaline.cristal.exception.GameNotFoundException;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(gameRepository);
    }

    @Test
    void shouldCreateGame() {
        GameRequest request = new GameRequest(
            "Dead Cells",
            "Rogue-like",
            2018,
            "Fast-paced action platformer.",
            "https://example.com/dead-cells.jpg"
        );

        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            game.setId(10L);
            return game;
        });

        var response = gameService.createGame(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Dead Cells");

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(captor.capture());
        assertThat(captor.getValue().getGenre()).isEqualTo("Rogue-like");
    }

    @Test
    void shouldReturnFilteredGames() {
        when(gameRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(new Game(1L, "Celeste", "Platformer", 2018, "desc", null)));

        var result = gameService.listGames("cele", "Platformer", 2018);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Celeste");
    }

    @Test
    void shouldThrowWhenGameDoesNotExist() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGame(99L))
            .isInstanceOf(GameNotFoundException.class)
            .hasMessageContaining("99");
    }
}
