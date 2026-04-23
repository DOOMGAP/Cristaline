package com.cristaline.cristal.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cristaline.cristal.model.Favorite;
import com.cristaline.cristal.model.Game;
import com.cristaline.cristal.model.User;
import com.cristaline.cristal.repository.GameRepository;
import com.cristaline.cristal.repository.UserRepository;
import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.FavoriteService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldRequireAuthenticationForAddFavorite() throws Exception {
        mockMvc.perform(post("/games/1/favorites"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAddFavoriteWhenAuthenticated() throws Exception {
        User user = new User("alice", "alice@test.dev", "encoded");
        Game game = new Game(1L, null, "Celeste", "Platformer", 2018, "desc", null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        mockMvc.perform(post("/games/1/favorites")
                .principal(new TestingAuthenticationToken("alice", null)))
            .andExpect(status().isOk());

        verify(favoriteService).addFavorite(user, 1L);
    }

    @Test
    void shouldExposeFavoriteStatus() throws Exception {
        User user = new User("alice", "alice@test.dev", "encoded");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(favoriteService.isFavorited(user, 4L)).thenReturn(true);

        mockMvc.perform(get("/games/4/favorites/user")
                .principal(new TestingAuthenticationToken("alice", null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.favorited").value(true));
    }

    @Test
    void shouldReturnResolvedFavoriteGames() throws Exception {
        User user = new User("alice", "alice@test.dev", "encoded");
        Favorite favorite = new Favorite(user, 9L);
        Game game = new Game(9L, null, "Hollow Knight", "Metroidvania", 2017, "desc", null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(favoriteService.getUserFavorites(user.getId())).thenReturn(List.of(favorite));
        when(gameRepository.findById(9L)).thenReturn(Optional.of(game));

        mockMvc.perform(get("/me/favorites")
                .principal(new TestingAuthenticationToken("alice", null)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(9))
            .andExpect(jsonPath("$[0].title").value("Hollow Knight"));
    }

    @Test
    void shouldRemoveFavoriteWhenAuthenticated() throws Exception {
        User user = new User("alice", "alice@test.dev", "encoded");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/games/1/favorites")
                .principal(new TestingAuthenticationToken("alice", null)))
            .andExpect(status().isOk());

        verify(favoriteService).removeFavorite(user, 1L);
    }
}
