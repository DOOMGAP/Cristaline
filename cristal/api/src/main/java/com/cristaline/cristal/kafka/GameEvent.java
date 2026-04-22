package com.cristaline.cristal.kafka;

import java.time.Instant;

public record GameEvent(
    String type,
    Long gameId,
    String title,
    String genre,
    Integer releaseYear,
    Instant occurredAt
) {
}
