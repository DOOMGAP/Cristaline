package com.cristaline.cristal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/import")
public class AdminImportController {

    @PostMapping("/freetogame")
    public ResponseEntity<Void> importFromFreeToGame() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
