package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.RatingRequest;
import com.cristaline.cristal.dto.RatingResponse;
import com.cristaline.cristal.model.Rating;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.RatingRepository;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/games/{gameId}/ratings/user")
    public ResponseEntity<?> getRating(@PathVariable Long gameId) {
        try {
            // Get or create default user
            User user = userRepository.findById(1L).orElseGet(() -> {
                User defaultUser = new User("demo_user", "demo@example.com", "password");
                return userRepository.save(defaultUser);
            });

            // Find existing rating
            var existingRating = ratingRepository.findByUserIdAndGameId(user.getId(), gameId);

            if (existingRating.isPresent()) {
                Rating rating = existingRating.get();
                RatingResponse response = new RatingResponse(
                        rating.getId(),
                        rating.getRating(),
                        rating.getRatedAt().toString()
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching rating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }

    @PostMapping("/games/{id}/ratings")
    public ResponseEntity<?> addRating(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @RequestBody RatingRequest request) {
        try {
            // Validate rating is between 1 and 10
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 10) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 10");
            }

            // Find or create default user if userId not provided
            User user;
            if (userId != null) {
                user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            } else {
                // Create or get default user for demo purposes
                user = userRepository.findById(1L).orElseGet(() -> {
                    User defaultUser = new User("demo_user", "demo@example.com", "password");
                    return userRepository.save(defaultUser);
                });
            }

            // Save the rating
            Rating rating = ratingService.saveRating(user, id, request.getRating());

            RatingResponse response = new RatingResponse(
                    rating.getId(),
                    rating.getRating(),
                    rating.getRatedAt().toString()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error saving rating: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur: " + e.getMessage());
        }
    }
}
