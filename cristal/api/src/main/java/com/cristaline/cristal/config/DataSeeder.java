package com.cristaline.cristal.config;

import com.cristaline.cristal.service.ImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedGames(ImportService importService) {
        return args -> {
            int importedCount = importService.refreshFromFreeToGame();
            LOGGER.info("Imported {} games from FreeToGame during startup", importedCount);
        };
    }
}
