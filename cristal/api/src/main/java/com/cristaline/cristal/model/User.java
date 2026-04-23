package com.cristaline.cristal.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String username;

    @Column(nullable = false, length = 2048)
    private String password; // Hash password

    @Column(nullable = false, length = 120)
    private String email;

    @ElementCollection
    @CollectionTable(name = "user_favorite_games", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "game_id")
    private Set<Long> favoriteGamesIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
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
