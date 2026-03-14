package com.cristaline.cristal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GameRequest(
    @NotBlank @Size(max = 120) String title,
    @NotBlank @Size(max = 80) String genre,
    @NotNull @Min(1970) @Max(2100) Integer releaseYear,
    @NotBlank @Size(max = 2000) String description,
    @Size(max = 500) String coverUrl
) {
}
