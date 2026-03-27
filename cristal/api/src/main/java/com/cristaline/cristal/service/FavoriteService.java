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

    public Favorite addFavorite(User user, Long gameId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndGameId(user.getId(), gameId);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Favorite favorite = new Favorite(user, gameId);
        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(User user, Long gameId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndGameId(user.getId(), gameId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        }
    }

    public boolean isFavorited(User user, Long gameId) {
        return favoriteRepository.findByUserIdAndGameId(user.getId(), gameId).isPresent();
    }

    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
}
