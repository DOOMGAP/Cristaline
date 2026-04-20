package com.cristaline.cristal.model;

import jakarta.persistence.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(nullable = false, length = 24)
    private String password; // Hash password

    @Column(nullable = false, length = 120)
    private String email;

    @ElementCollection
    @CollectionTable(name = "user_favorite_games", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "game_id")
    private Set<Long> favoriteGamesIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratedGames = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public User() {}

    public User(String username, String email, String password)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        roles.add("USER");
    }

    public User(String username, String email, String password, String role)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        roles.add(role);
    }

    public void addFavorite(Long gameId) {
        favoriteGamesIds.add(gameId);
        this.updatedAt = Instant.now();
    }

    public void removeFavorite(Long gameId) {
        favoriteGamesIds.remove(gameId);
        this.updatedAt = Instant.now();
    }
    public void rateGame(Long gameId, Double rating) {
        Optional<Rating> existing = ratedGames.stream()
                .filter(r -> r.getGameId().equals(gameId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().updateRating(rating);
        } else {
            Rating newRating = new Rating(this, gameId, rating);
            ratedGames.add(newRating);
        }

        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String getEmail() { return email; }

    public Set<Long> getFavoriteGameIds() { return favoriteGamesIds; }

    public List<Rating> getRatings() { return ratedGames; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public String getPassword() { return password; }

    public Set<String> getRoles() { return roles; }
}
