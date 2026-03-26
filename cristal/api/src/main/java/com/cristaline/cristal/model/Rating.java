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
    private int rating;

    private String comment;

    @Column(nullable = false)
    private Instant ratedAt;

    public Rating() {}

    public Rating(User user, Long gameId, int rating, String comment) {
        this.user = user;
        this.gameId = gameId;
        this.rating = rating;
        this.comment = comment;
        this.ratedAt = Instant.now();
    }

    public void updateRating(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        this.ratedAt = Instant.now();
    }

    public Long getId() { return id; }

    public Long getGameId() { return gameId; }

    public int getRating() { return rating; }

    public String getComment() { return comment; }

    public Instant getRatedAt() { return ratedAt; }

    public User getUser() { return user; }
}