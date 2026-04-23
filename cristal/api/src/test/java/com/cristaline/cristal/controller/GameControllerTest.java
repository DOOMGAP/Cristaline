package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.exception.GameNotFoundException;
import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.GameService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldReturnListOfGames() throws Exception {
        // Given
        List<GameResponse> games = List.of(
            new GameResponse(1L, "Outer Wilds", "Adventure", 2019, "Space exploration", "url")
        );
        when(gameService.listGames(null, null, null)).thenReturn(games);

        // When & Then
        mockMvc.perform(get("/games")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Outer Wilds"))
                .andExpect(jsonPath("$[0].genre").value("Adventure"));
    }

    @Test
    void shouldReturnGameById() throws Exception {
        // Given
        GameResponse game = new GameResponse(1L, "Celeste", "Platformer", 2018, "Mountain climbing", "url");
        when(gameService.getGame(1L)).thenReturn(game);

        // When & Then
        mockMvc.perform(get("/games/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Celeste"))
                .andExpect(jsonPath("$.releaseYear").value(2018));
    }

    @Test
    void shouldReturn404WhenGameNotFound() throws Exception {
        // Given - Correction ici : On passe un Long (99L) au lieu d'un String
        when(gameService.getGame(99L)).thenThrow(new GameNotFoundException(99L));

        // When & Then
        mockMvc.perform(get("/games/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPassFiltersToServiceWhenListingGames() throws Exception {
        // Given - Correction ici : L'ordre est title, genre, year
        when(gameService.listGames("Zelda", "RPG", 2020)).thenReturn(List.of());

        // When & Then - On adapte les paramètres d'URL pour correspondre
        mockMvc.perform(get("/games?title=Zelda&genre=RPG&year=2020")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // On vérifie que le contrôleur a bien extrait les paramètres
        Mockito.verify(gameService).listGames("Zelda", "RPG", 2020);
    }
}