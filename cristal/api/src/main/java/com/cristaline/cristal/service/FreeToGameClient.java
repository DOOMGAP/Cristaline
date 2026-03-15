package com.cristaline.cristal.service;

import com.cristaline.cristal.dto.FreeToGameApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FreeToGameClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI gamesUri;

    public FreeToGameClient(
        ObjectMapper objectMapper,
        @Value("${app.freetogame.base-url:https://www.freetogame.com/api}") String baseUrl
    ) {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .build();
        this.objectMapper = objectMapper;
        this.gamesUri = URI.create(baseUrl.endsWith("/") ? baseUrl + "games" : baseUrl + "/games");
    }

    public List<FreeToGameApiResponse> fetchGames() {
        HttpRequest request = HttpRequest.newBuilder(gamesUri)
            .timeout(REQUEST_TIMEOUT)
            .header("Accept", "application/json")
            .GET()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("FreeToGame API returned status " + response.statusCode());
            }

            FreeToGameApiResponse[] games = objectMapper.readValue(response.body(), FreeToGameApiResponse[].class);
            return Arrays.asList(games);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read response from FreeToGame API", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("FreeToGame API call was interrupted", exception);
        }
    }
}
