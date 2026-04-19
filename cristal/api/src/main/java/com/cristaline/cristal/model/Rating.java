package com.cristaline.cristal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private Instant ratedAt;

    public Rating() {}

    public Rating(User user, Long gameId, Double rating) {
        this.user = user;
        this.gameId = gameId;
        this.rating = rating;
        this.ratedAt = Instant.now();
    }

    public void updateRating(Double rating) {
        this.rating = rating;
        this.ratedAt = Instant.now();
    }

    public Long getId() { return id; }

    public Long getGameId() { return gameId; }

    public Double getRating() { return rating; }

    public Instant getRatedAt() { return ratedAt; }

    public User getUser() { return user; }
}