package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.GameRequest;
import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/games")
public class AdminGameController {

    private final GameService gameService;

    public AdminGameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse createGame(@Valid @RequestBody GameRequest request) {
        return gameService.createGame(request);
    }

    @PutMapping("/{id}")
    public GameResponse updateGame(@PathVariable Long id, @Valid @RequestBody GameRequest request) {
        return gameService.updateGame(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
    }
}
