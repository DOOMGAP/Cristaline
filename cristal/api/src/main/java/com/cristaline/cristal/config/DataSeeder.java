package com.cristaline.cristal.config;

import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import com.cristaline.cristal.service.ImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedGames(GameRepository gameRepository, ImportService importService) {
        return args -> {
            if (gameRepository.count() > 0) {
                return;
            }

            try {
                int importedCount = importService.refreshFromFreeToGame();
                if (importedCount > 0) {
                    LOGGER.info("Imported {} games from FreeToGame", importedCount);
                    return;
                }
            } catch (RuntimeException exception) {
                LOGGER.warn("FreeToGame import failed, falling back to local seed data: {}", exception.getMessage());
            }

            gameRepository.save(new Game(null, "Hades", "Rogue-like", 2020,
                "Un dungeon crawler nerveux inspire de la mythologie grecque.",
                "https://images.igdb.com/igdb/image/upload/t_cover_big/co2lbd.jpg"));
            gameRepository.save(new Game(null, "Celeste", "Platformer", 2018,
                "Un platformer exigeant autour du depassement de soi.",
                "https://images.igdb.com/igdb/image/upload/t_cover_big/co1tmu.jpg"));
            gameRepository.save(new Game(null, "Disco Elysium", "RPG", 2019,
                "Une enquete narrative dense et singuliere.",
                "https://images.igdb.com/igdb/image/upload/t_cover_big/co2n99.jpg"));
            gameRepository.save(new Game(null, "Stardew Valley", "Simulation", 2016,
                "Une simulation de ferme cosy avec progression libre.",
                "https://images.igdb.com/igdb/image/upload/t_cover_big/co1r7f.jpg"));
        };
    }
}
