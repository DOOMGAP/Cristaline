package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
