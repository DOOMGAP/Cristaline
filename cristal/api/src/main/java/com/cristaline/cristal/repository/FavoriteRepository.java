package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndGameId(Long userId, Long gameId);
    List<Favorite> findByUserId(Long userId);
}
