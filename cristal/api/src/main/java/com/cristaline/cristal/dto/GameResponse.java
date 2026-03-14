package com.cristaline.cristal.dto;

public record GameResponse(
    Long id,
    String title,
    String genre,
    Integer releaseYear,
    String description,
    String coverUrl
) {
}
