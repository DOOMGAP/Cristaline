package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.GameRequest;
import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.exception.GameNotFoundException;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.repository.GameRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameResponse> listGames(String title, String genre, Integer year) {
        Specification<Game> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
                ));
            }
            if (genre != null && !genre.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("genre")),
                    genre.toLowerCase()
                ));
            }
            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("releaseYear"), year));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return gameRepository.findAll(specification).stream()
            .map(this::toResponse)
            .toList();
    }

    public GameResponse getGame(Long id) {
        return toResponse(findGame(id));
    }

    public GameResponse createGame(GameRequest request) {
        Game game = new Game();
        applyRequest(game, request);
        return toResponse(gameRepository.save(game));
    }

    public GameResponse updateGame(Long id, GameRequest request) {
        Game game = findGame(id);
        applyRequest(game, request);
        return toResponse(gameRepository.save(game));
    }

    public void deleteGame(Long id) {
        gameRepository.delete(findGame(id));
    }

    private Game findGame(Long id) {
        return gameRepository.findById(id)
            .orElseThrow(() -> new GameNotFoundException(id));
    }

    private void applyRequest(Game game, GameRequest request) {
        game.setTitle(request.title());
        game.setGenre(request.genre());
        game.setReleaseYear(request.releaseYear());
        game.setDescription(request.description());
        game.setCoverUrl(request.coverUrl());
    }

    private GameResponse toResponse(Game game) {
        return new GameResponse(
            game.getId(),
            game.getTitle(),
            game.getGenre(),
            game.getReleaseYear(),
            game.getDescription(),
            game.getCoverUrl()
        );
    }
}
