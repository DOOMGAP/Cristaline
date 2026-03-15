package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.FreeToGameApiResponse;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportService {

    private final GameRepository gameRepository;
    private final FreeToGameClient freeToGameClient;

    public ImportService(GameRepository gameRepository, FreeToGameClient freeToGameClient) {
        this.gameRepository = gameRepository;
        this.freeToGameClient = freeToGameClient;
    }

    @Transactional
    public int refreshFromFreeToGame() {
        List<Game> importedGames = freeToGameClient.fetchGames().stream()
            .map(this::toGame)
            .toList();

        if (importedGames.isEmpty()) {
            return 0;
        }

        gameRepository.deleteAllInBatch();
        gameRepository.saveAll(importedGames);
        return importedGames.size();
    }

    private Game toGame(FreeToGameApiResponse game) {
        return new Game(
            null,
            truncate(game.title(), 120),
            truncate(defaultValue(game.genre(), "Unknown"), 80),
            extractYear(game.releaseDate()),
            truncate(defaultValue(game.shortDescription(), "Imported from FreeToGame."), 2000),
            truncate(game.thumbnail(), 500)
        );
    }

    private Integer extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.isBlank()) {
            return 1970;
        }

        try {
            return LocalDate.parse(releaseDate).getYear();
        } catch (DateTimeParseException exception) {
            return 1970;
        }
    }

    private String defaultValue(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
