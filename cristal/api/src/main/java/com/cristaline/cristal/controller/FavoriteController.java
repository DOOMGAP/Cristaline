package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.FavoriteStatusResponse;
import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.model.Favorite;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.GameRepository;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.service.FavoriteService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @PostMapping("/games/{id}/favorites")
    public ResponseEntity<?> addFavorite(@PathVariable Long id) {
        try {
            // Get or create default user
            User user = userRepository.findById(1L).orElseGet(() -> {
                User defaultUser = new User(1L, "demo_user", "demo@example.com", "password");
                return userRepository.save(defaultUser);
            });

            // Check if game exists
            Game game = gameRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

            // Add to favorites
            Favorite favorite = favoriteService.addFavorite(user, id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error adding favorite: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    @DeleteMapping("/games/{id}/favorites")
    public ResponseEntity<?> removeFavorite(@PathVariable Long id) {
        try {
            // Get or create default user
            User user = userRepository.findById(1L).orElseGet(() -> {
                User defaultUser = new User(1L, "demo_user", "demo@example.com", "password");
                return userRepository.save(defaultUser);
            });

            // Remove from favorites
            favoriteService.removeFavorite(user, id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error removing favorite: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/games/{gameId}/favorites/user")
    public ResponseEntity<FavoriteStatusResponse> isFavorited(@PathVariable Long gameId) {
        try {
            // Get or create default user
            User user = userRepository.findById(1L).orElseGet(() -> {
                User defaultUser = new User(1L, "demo_user", "demo@example.com", "password");
                return userRepository.save(defaultUser);
            });

            boolean isFavorited = favoriteService.isFavorited(user, gameId);
            return ResponseEntity.ok(new FavoriteStatusResponse(isFavorited));
        } catch (Exception e) {
            System.err.println("Error checking favorite: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<?> getMyFavorites() {
        try {
            // Get or create default user
            User user = userRepository.findById(1L).orElseGet(() -> {
                User defaultUser = new User(1L, "demo_user", "demo@example.com", "password");
                return userRepository.save(defaultUser);
            });

            List<Favorite> favorites = favoriteService.getUserFavorites(user.getId());

            // Convert to GameResponse list
            List<GameResponse> games = favorites.stream()
                    .map(fav -> gameRepository.findById(fav.getGameId()))
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .map(this::toGameResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(games);
        } catch (Exception e) {
            System.err.println("Error fetching favorites: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    private GameResponse toGameResponse(Game game) {
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
