package com.cristaline.cristal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cristaline.cristal.model.Rating;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.RatingRepository;
import com.cristaline.cristal.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService();
        ReflectionTestUtils.setField(ratingService, "ratingRepository", ratingRepository);
        ReflectionTestUtils.setField(ratingService, "userRepository", userRepository);
    }

    @Test
    void shouldCreateRatingWhenMissing() {
        User user = new User("link", "link@hyrule.com", "encoded");

        when(ratingRepository.findByUserIdAndGameId(user.getId(), 15L)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rating rating = ratingService.saveRating(user, 15L, 8.5);

        assertThat(rating.getGameId()).isEqualTo(15L);
        assertThat(rating.getRating()).isEqualTo(8.5);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void shouldUpdateExistingRating() {
        User user = new User("zelda", "zelda@hyrule.com", "encoded");
        Rating rating = new Rating(user, 2L, 6.0);

        when(ratingRepository.findByUserIdAndGameId(user.getId(), 2L)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(rating)).thenReturn(rating);

        Rating saved = ratingService.saveRating(user, 2L, 9.0);

        assertThat(saved.getRating()).isEqualTo(9.0);
        verify(ratingRepository).save(rating);
    }

    @Test
    void shouldExposeAggregateQueries() {
        when(ratingRepository.findAverageByGameId(5L)).thenReturn(7.4);
        when(ratingRepository.countByGameId(5L)).thenReturn(11L);

        assertThat(ratingService.getAverageRating(5L)).isEqualTo(7.4);
        assertThat(ratingService.getRatingsCount(5L)).isEqualTo(11L);
    }
}
