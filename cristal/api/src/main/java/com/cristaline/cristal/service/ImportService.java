package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.FreeToGameApiResponse;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<FreeToGameApiResponse> apiGames = freeToGameClient.fetchGames();
        List<Game> importedGames = apiGames.stream()
            .map(this::upsertGame)
            .toList();

        if (importedGames.isEmpty()) {
            return 0;
        }

        deleteRemovedApiGames(apiGames);
        gameRepository.saveAll(importedGames);
        return importedGames.size();
    }

    private void deleteRemovedApiGames(List<FreeToGameApiResponse> apiGames) {
        Set<Long> currentApiIds = apiGames.stream()
            .map(FreeToGameApiResponse::id)
            .filter(apiId -> apiId != null)
            .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        List<Game> gamesToDelete = gameRepository.findAllByApiIdNotNull().stream()
            .filter(game -> !currentApiIds.contains(game.getApiId()))
            .toList();

        if (!gamesToDelete.isEmpty()) {
            gameRepository.deleteAll(gamesToDelete);
        }
    }

    private Game upsertGame(FreeToGameApiResponse apiGame) {
        Game game = findExistingGame(apiGame.id()).orElseGet(Game::new);
        game.setApiId(apiGame.id());
        game.setTitle(truncate(apiGame.title(), 120));
        game.setGenre(truncate(defaultValue(apiGame.genre(), "Unknown"), 80));
        game.setReleaseYear(extractYear(apiGame.releaseDate()));
        game.setDescription(truncate(defaultValue(apiGame.shortDescription(), "Imported from FreeToGame."), 2000));
        game.setCoverUrl(truncate(apiGame.thumbnail(), 500));
        return game;
    }

    private Optional<Game> findExistingGame(Long apiId) {
        if (apiId == null) {
            return Optional.empty();
        }

        return gameRepository.findByApiId(apiId);
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
