package com.cristaline.cristal.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(Long id) {
        super("Game with id " + id + " was not found.");
    }
}
