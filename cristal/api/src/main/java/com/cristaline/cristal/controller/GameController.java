package com.cristaline.cristal.controller;

import com.cristaline.cristal.dto.GameResponse;
import com.cristaline.cristal.service.GameService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponse> listGames(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String genre,
        @RequestParam(required = false) Integer year
    ) {
        return gameService.listGames(title, genre, year);
    }

    @GetMapping("/{id}")
    public GameResponse getGame(@PathVariable Long id) {
        return gameService.getGame(id);
    }
}
