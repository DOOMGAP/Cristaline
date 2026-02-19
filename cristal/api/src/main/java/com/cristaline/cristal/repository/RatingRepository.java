package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
