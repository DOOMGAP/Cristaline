package com.cristaline.cristal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FreeToGameApiResponse(
    Long id,
    String title,
    String genre,
    String thumbnail,
    @JsonProperty("short_description") String shortDescription,
    @JsonProperty("release_date") String releaseDate
) {
}
