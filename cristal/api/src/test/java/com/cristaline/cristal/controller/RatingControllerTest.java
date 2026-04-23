package com.cristaline.cristal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cristaline.cristal.dto.RatingRequest;
import com.cristaline.cristal.model.Rating;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.RatingRepository;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldReturnRatingSummary() throws Exception {
        when(ratingService.getAverageRating(7L)).thenReturn(8.2);
        when(ratingService.getRatingsCount(7L)).thenReturn(3L);

        mockMvc.perform(get("/games/7/ratings/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.averageRating").value(8.2))
            .andExpect(jsonPath("$.ratingsCount").value(3));
    }

    @Test
    void shouldReturnNoContentWhenUserHasNoRating() throws Exception {
        User user = new User("bob", "bob@test.dev", "encoded");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(ratingRepository.findByUserIdAndGameId(user.getId(), 5L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/games/5/ratings/user")
                .principal(new TestingAuthenticationToken("bob", null)))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectInvalidRating() throws Exception {
        mockMvc.perform(post("/games/2/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RatingRequest(11.0)))
                .principal(new TestingAuthenticationToken("bob", null)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSaveRatingForAuthenticatedUser() throws Exception {
        User user = new User("bob", "bob@test.dev", "encoded");
        Rating rating = new Rating(user, 2L, 9.0);

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(ratingService.saveRating(user, 2L, 9.0)).thenReturn(rating);

        mockMvc.perform(post("/games/2/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RatingRequest(9.0)))
                .principal(new TestingAuthenticationToken("bob", null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rating").value(9.0));

        verify(ratingService).saveRating(user, 2L, 9.0);
    }
}
