package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.GameRequest;
import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.exception.GameNotFoundException;
import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminGameController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateGame() throws Exception {
        // Given
        GameRequest request = new GameRequest("Hades", "Rogue-like", 2020, "Escape the underworld", "url");
        GameResponse response = new GameResponse(1L, "Hades", "Rogue-like", 2020, "Escape the underworld", "url");
        
        when(gameService.createGame(any(GameRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/admin/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Hades"));
    }

    @Test
    void shouldUpdateGame() throws Exception {
        // Given
        GameRequest request = new GameRequest("Hades II", "Rogue-like", 2024, "Beyond the underworld", "url");
        GameResponse response = new GameResponse(1L, "Hades II", "Rogue-like", 2024, "Beyond the underworld", "url");

        when(gameService.updateGame(eq(1L), any(GameRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/admin/games/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hades II"));
    }

    @Test
    void shouldDeleteGame() throws Exception {
        // When & Then
        mockMvc.perform(delete("/admin/games/1"))
                .andExpect(status().isNoContent());

        verify(gameService).deleteGame(1L);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentGame() throws Exception {
        // Given
        GameRequest request = new GameRequest("Fantôme", "Action", 2024, "Desc", "url");
        // Correction ici : 99L au lieu du string
        when(gameService.updateGame(eq(99L), any(GameRequest.class)))
                .thenThrow(new GameNotFoundException(99L));

        // When & Then
        mockMvc.perform(put("/admin/games/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentGame() throws Exception {
        // Given - Correction ici : 99L au lieu du string
        Mockito.doThrow(new GameNotFoundException(99L))
                .when(gameService).deleteGame(99L);

        // When & Then
        mockMvc.perform(delete("/admin/games/99"))
                .andExpect(status().isNotFound());
    }
}