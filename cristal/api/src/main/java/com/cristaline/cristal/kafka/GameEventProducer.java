package com.cristaline.cristal.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GameEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String gameEventsTopic;
    private final String importRequestsTopic;

    public GameEventProducer(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.game-events}") String gameEventsTopic,
        @Value("${app.kafka.topics.import-requests}") String importRequestsTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.gameEventsTopic = gameEventsTopic;
        this.importRequestsTopic = importRequestsTopic;
    }

    public void sendGameEvent(GameEvent event) {
        kafkaTemplate.send(gameEventsTopic, String.valueOf(event.gameId()), event);
    }

    public void sendImportRequest(ImportRequestEvent event) {
        kafkaTemplate.send(importRequestsTopic, event.source(), event);
    }
}
