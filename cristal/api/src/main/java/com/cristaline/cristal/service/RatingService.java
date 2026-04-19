package com.cristaline.cristal.service;

import com.cristaline.cristal.model.Rating;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.RatingRepository;
import com.cristaline.cristal.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    public Rating saveRating(User user, Long gameId, Double rating) {
        Optional<Rating> existing = ratingRepository.findByUserIdAndGameId(user.getId(), gameId);

        Rating ratingObj;
        if (existing.isPresent()) {
            ratingObj = existing.get();
            ratingObj.updateRating(rating);
        } else {
            ratingObj = new Rating(user, gameId, rating);
        }

        return ratingRepository.save(ratingObj);
    }
}
