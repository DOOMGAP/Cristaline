package com.cristaline.cristal.service;

import com.cristaline.cristal.model.Favorite;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.FavoriteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    /**
     * Adds a favorite for the given user and game, reusing the existing row when
     * the association already exists.
     */
    public Favorite addFavorite(User user, Long gameId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndGameId(user.getId(), gameId);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Favorite favorite = new Favorite(user, gameId);
        return favoriteRepository.save(favorite);
    }

    /**
     * Removes the favorite association when present. Missing favorites are a
     * no-op to keep the endpoint idempotent.
     */
    public void removeFavorite(User user, Long gameId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndGameId(user.getId(), gameId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        }
    }

    public boolean isFavorited(User user, Long gameId) {
        return favoriteRepository.findByUserIdAndGameId(user.getId(), gameId).isPresent();
    }

    /**
     * Returns all favorites for a user so controllers can resolve the linked games.
     */
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
