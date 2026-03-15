package com.cristaline.cristal.controller;

import com.cristaline.cristal.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/import")
public class AdminImportController {

    private final ImportService importService;

    public AdminImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/freetogame")
    public ResponseEntity<Void> importFromFreeToGame() {
        importService.refreshFromFreeToGame();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
