package com.cristaline.cristal.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.cristaline.cristal.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userDetails = new CustomUserDetails(new User("samus", "samus@bounty.io", "encoded"));
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("samus");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }
}
