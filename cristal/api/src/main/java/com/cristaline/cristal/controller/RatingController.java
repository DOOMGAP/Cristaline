package com.cristaline.cristal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cristaline.cristal.dto.RatingRequest;
import com.cristaline.cristal.dto.RatingResponse;
import com.cristaline.cristal.dto.RatingSummaryResponse;
import com.cristaline.cristal.model.Rating;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.RatingRepository;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.service.RatingService;

@RestController
@RequestMapping
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/games/{gameId}/ratings/summary")
    public ResponseEntity<RatingSummaryResponse> getRatingSummary(@PathVariable Long gameId) {
        Double average = ratingService.getAverageRating(gameId);
        Long count = ratingService.getRatingsCount(gameId);
        return ResponseEntity.ok(new RatingSummaryResponse(average, count));
    }

    @GetMapping("/games/{gameId}/ratings/user")
    public ResponseEntity<?> getRating(@PathVariable Long gameId, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

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
            @RequestBody RatingRequest request,
            Authentication authentication) {
        try {
            // Validate rating is between 1 and 10
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 10) {
                return ResponseEntity.badRequest().body("Rating must be between 1 and 10");
            }

            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

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
