package com.cristaline.cristal.kafka;

import java.time.Instant;

public record ImportRequestEvent(
    String source,
    String requestedBy,
    Instant requestedAt
) {
}
