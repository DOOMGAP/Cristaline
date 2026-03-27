package com.cristaline.cristal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private Instant favoritedAt;

    public Favorite() {}

    public Favorite(User user, Long gameId) {
        this.user = user;
        this.gameId = gameId;
        this.favoritedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Long getGameId() { return gameId; }

    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Instant getFavoritedAt() { return favoritedAt; }

    public void setFavoritedAt(Instant favoritedAt) { this.favoritedAt = favoritedAt; }
}
