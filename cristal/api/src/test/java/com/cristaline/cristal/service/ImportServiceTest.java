package com.cristaline.cristal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cristaline.cristal.dto.FreeToGameApiResponse;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private FreeToGameClient freeToGameClient;

    private ImportService importService;

    @BeforeEach
    void setUp() {
        importService = new ImportService(gameRepository, freeToGameClient);
    }

    @Test
    void shouldRefreshCatalogFromFreeToGame() {
        when(freeToGameClient.fetchGames()).thenReturn(List.of(
            new FreeToGameApiResponse(
                1L,
                "Warframe",
                "Shooter",
                "https://example.com/warframe.jpg",
                "Fast-paced co-op action.",
                "2013-03-25"
            )
        ));
        when(gameRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        int importedCount = importService.refreshFromFreeToGame();

        assertThat(importedCount).isEqualTo(1);
        verify(gameRepository, never()).deleteAllInBatch();

        ArgumentCaptor<List<Game>> captor = ArgumentCaptor.forClass(List.class);
        verify(gameRepository).saveAll(captor.capture());
        assertThat(captor.getValue())
            .singleElement()
            .extracting(Game::getApiId, Game::getTitle, Game::getGenre, Game::getReleaseYear, Game::getCoverUrl)
            .containsExactly(1L, "Warframe", "Shooter", 2013, "https://example.com/warframe.jpg");
    }

    @Test
    void shouldUpdateExistingGameWhenApiIdAlreadyExists() {
        Game existingGame = new Game(
            10L,
            1L,
            "Old title",
            "Old genre",
            2010,
            "Old description",
            "https://example.com/old.jpg"
        );
        when(freeToGameClient.fetchGames()).thenReturn(List.of(
            new FreeToGameApiResponse(
                1L,
                "Warframe",
                "Shooter",
                "https://example.com/warframe.jpg",
                "Fast-paced co-op action.",
                "2013-03-25"
            )
        ));
        when(gameRepository.findByApiId(1L)).thenReturn(java.util.Optional.of(existingGame));
        when(gameRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        int importedCount = importService.refreshFromFreeToGame();

        assertThat(importedCount).isEqualTo(1);
        ArgumentCaptor<List<Game>> captor = ArgumentCaptor.forClass(List.class);
        verify(gameRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).singleElement().isSameAs(existingGame);
        assertThat(existingGame.getTitle()).isEqualTo("Warframe");
        assertThat(existingGame.getGenre()).isEqualTo("Shooter");
        assertThat(existingGame.getReleaseYear()).isEqualTo(2013);
        assertThat(existingGame.getDescription()).isEqualTo("Fast-paced co-op action.");
        assertThat(existingGame.getCoverUrl()).isEqualTo("https://example.com/warframe.jpg");
    }

    @Test
    void shouldDeleteImportedGamesMissingFromLatestApiPayload() {
        Game removedApiGame = new Game(
            20L,
            99L,
            "Removed game",
            "RPG",
            2015,
            "Old imported game",
            "https://example.com/removed.jpg"
        );
        Game keptApiGame = new Game(
            10L,
            1L,
            "Old title",
            "Old genre",
            2010,
            "Old description",
            "https://example.com/old.jpg"
        );
        when(freeToGameClient.fetchGames()).thenReturn(List.of(
            new FreeToGameApiResponse(
                1L,
                "Warframe",
                "Shooter",
                "https://example.com/warframe.jpg",
                "Fast-paced co-op action.",
                "2013-03-25"
            )
        ));
        when(gameRepository.findByApiId(1L)).thenReturn(java.util.Optional.of(keptApiGame));
        when(gameRepository.findAllByApiIdNotNull()).thenReturn(List.of(keptApiGame, removedApiGame));
        when(gameRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        int importedCount = importService.refreshFromFreeToGame();

        assertThat(importedCount).isEqualTo(1);
        verify(gameRepository).deleteAll(List.of(removedApiGame));
        verify(gameRepository).saveAll(anyList());
    }
}
