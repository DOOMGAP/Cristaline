package com.cristaline.cristal.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.kafka.GameEventProducer;
import com.cristaline.cristal.kafka.ImportRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceTest {

    @Mock
    private GameEventProducer gameEventProducer;

    private EventPublisherService eventPublisherService;

    @BeforeEach
    void setUp() {
        eventPublisherService = new EventPublisherService(gameEventProducer);
    }

    @Test
    void shouldPublishImportRequestEvent() {
        eventPublisherService.publishImportRequest("admin-ui");

        verify(gameEventProducer).sendImportRequest(argThat((ImportRequestEvent event) ->
            "admin-import".equals(event.source()) && "admin-ui".equals(event.requestedBy())
        ));
    }

    @Test
    void shouldPublishGameLifecycleEvents() {
        GameResponse game = new GameResponse(7L, "Hades", "Rogue-like", 2020, "desc", "cover");

        eventPublisherService.publishGameUpdated(game);

        verify(gameEventProducer).sendGameEvent(argThat(event ->
            "GAME_UPDATED".equals(event.type()) &&
            event.gameId().equals(7L) &&
            "Hades".equals(event.title())
        ));
    }
}
