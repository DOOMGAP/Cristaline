package com.cristaline.cristal.dto;

public record RatingSummaryResponse(
        Double averageRating,
        Long ratingsCount
) {
}