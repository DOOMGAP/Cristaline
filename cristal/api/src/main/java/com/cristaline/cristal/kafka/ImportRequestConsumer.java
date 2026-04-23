package com.cristaline.cristal.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.cristaline.cristal.service.ImportService;

@Component
@Profile("prod")
public class ImportRequestConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRequestConsumer.class);

    private final ImportService importService;

    public ImportRequestConsumer(ImportService importService) {
        this.importService = importService;
    }

    /**
     * Only enabled in the prod profile because dev uses H2 and local synchronous
     * workflows. Docker/prod routes the admin import through Kafka.
     */
    @KafkaListener(topics = "${app.kafka.topics.import-requests}")
    public void consume(ImportRequestEvent event) {
        LOGGER.info("Received import request from {} requested by {}", event.source(), event.requestedBy());
        int importedCount = importService.refreshFromFreeToGame();
        LOGGER.info("Imported {} games after Kafka request", importedCount);
    }
}
