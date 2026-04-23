package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.AuthRequest;
import com.cristaline.cristal.dto.RegisterRequest;
import com.cristaline.cristal.exception.UserNotFoundException;
import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldRegisterUserAndReturnToken() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest("testuser", "test@test.com", "password");
        when(authService.register(any(RegisterRequest.class))).thenReturn("fake-jwt-token");

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void shouldLoginUserAndReturnToken() throws Exception {
        // Given
        AuthRequest request = new AuthRequest(null, "testuser", "password"); 
        when(authService.login(any(AuthRequest.class))).thenReturn("fake-jwt-token");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void shouldReturn404WhenLoginWithUnknownUser() throws Exception {
        // Given
        AuthRequest request = new AuthRequest(null, "inconnu", "mauvais_mot_de_passe");
        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new UserNotFoundException("Utilisateur introuvable"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}