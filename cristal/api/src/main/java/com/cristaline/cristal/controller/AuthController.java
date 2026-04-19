package com.cristaline.cristal.controller;

import com.cristaline.cristal.service.AuthService;
import com.cristaline.cristal.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {this.authService = authService; }


    @PostMapping("/register")
    public ResponseEntity<Void> register() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
