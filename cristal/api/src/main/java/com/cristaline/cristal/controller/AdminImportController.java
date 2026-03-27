package com.cristaline.cristal.controller;

import com.cristaline.cristal.service.EventPublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/import")
public class AdminImportController {

    private final EventPublisherService eventPublisherService;

    public AdminImportController(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @PostMapping("/freetogame")
    public ResponseEntity<Void> importFromFreeToGame() {
        eventPublisherService.publishImportRequest("admin-api");
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
