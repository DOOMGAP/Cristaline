package com.cristaline.cristal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
        verify(gameRepository).deleteAllInBatch();

        ArgumentCaptor<List<Game>> captor = ArgumentCaptor.forClass(List.class);
        verify(gameRepository).saveAll(captor.capture());
        assertThat(captor.getValue())
            .singleElement()
            .extracting(Game::getTitle, Game::getGenre, Game::getReleaseYear, Game::getCoverUrl)
            .containsExactly("Warframe", "Shooter", 2013, "https://example.com/warframe.jpg");
    }
}
