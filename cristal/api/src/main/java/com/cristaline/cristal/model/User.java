package com.cristaline.cristal.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(nullable = false, length = 24)
    private String password;

    @Column(nullable = false, length = 120)
    private String email;

    @ElementCollection
    @CollectionTable(name = "user_favorite_games", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "game_id")
    private Set<Long> favoriteGamesIds = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratedGames = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public User() {}

    public User(Long id, String username, String email, String password)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void addFavorite(Long gameId) {
        favoriteGamesIds.add(gameId);
        this.updatedAt = Instant.now();
    }

    public void removeFavorite(Long gameId) {
        favoriteGamesIds.remove(gameId);
        this.updatedAt = Instant.now();
    }
    public void rateGame(Long gameId, int rating, String comment) {
        Optional<Rating> existing = ratedGames.stream()
                .filter(r -> r.getGameId().equals(gameId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().updateRating(rating, comment);
        } else {
            Rating newRating = new Rating(this, gameId, rating, comment);
            ratedGames.add(newRating);
        }

        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public Set<Long> getFavoriteGameIds() { return favoriteGamesIds; }

    public List<Rating> getRatings() { return ratedGames; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}
