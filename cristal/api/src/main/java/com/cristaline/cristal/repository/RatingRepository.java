package com.cristaline.cristal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cristaline.cristal.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndGameId(Long userId, Long gameId);

    @Query("select avg(r.rating) from Rating r where r.gameId = :gameId")
    Double findAverageByGameId(@Param("gameId") Long gameId);

    Long countByGameId(Long gameId);
}
