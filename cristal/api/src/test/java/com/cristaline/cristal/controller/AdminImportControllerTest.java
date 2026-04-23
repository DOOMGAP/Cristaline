package com.cristaline.cristal.controller;

import com.cristaline.cristal.security.CustomUserDetailsService;
import com.cristaline.cristal.security.JwtService;
import com.cristaline.cristal.service.EventPublisherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminImportController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventPublisherService eventPublisherService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldTriggerImportAndReturnAccepted() throws Exception {
        // When & Then
        mockMvc.perform(post("/admin/import/freetogame"))
                .andExpect(status().isAccepted());

        verify(eventPublisherService).publishImportRequest("admin-api");
    }

    @Test
    void shouldNotPublishEventIfProviderIsUnknown() throws Exception {
        // When & Then
        mockMvc.perform(post("/admin/import/steam"))
                .andExpect(status().isNotFound()); 
                
        // On vérifie que le service n'a JAMAIS été appelé
        verify(eventPublisherService, never()).publishImportRequest(anyString());
    }
}