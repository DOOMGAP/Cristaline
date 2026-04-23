package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.kafka.GameEvent;
import com.cristaline.cristal.kafka.GameEventProducer;
import com.cristaline.cristal.kafka.ImportRequestEvent;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final GameEventProducer gameEventProducer;

    public EventPublisherService(GameEventProducer gameEventProducer) {
        this.gameEventProducer = gameEventProducer;
    }

    /**
     * Publishes an asynchronous import request. In prod the Kafka consumer
     * executes the actual refresh to decouple the admin API from the import.
     */
    public void publishImportRequest(String requestedBy) {
        gameEventProducer.sendImportRequest(new ImportRequestEvent(
            "admin-import",
            requestedBy,
            Instant.now()
        ));
    }

    public void publishGameCreated(GameResponse game) {
        publishGameEvent("GAME_CREATED", game);
    }

    public void publishGameUpdated(GameResponse game) {
        publishGameEvent("GAME_UPDATED", game);
    }

    public void publishGameDeleted(GameResponse game) {
        publishGameEvent("GAME_DELETED", game);
    }

    /**
     * Emits game lifecycle events for downstream consumers.
     */
    private void publishGameEvent(String type, GameResponse game) {
        gameEventProducer.sendGameEvent(new GameEvent(
            type,
            game.id(),
            game.title(),
            game.genre(),
            game.releaseYear(),
            Instant.now()
        ));
    }
}
