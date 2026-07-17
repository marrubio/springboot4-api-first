package es.marugi.spring.api.application.event;

import java.time.Instant;
import java.util.UUID;

public record GameEventDeliveryRequest(
    UUID eventId,
    String eventType,
    int eventVersion,
    Long gameId,
    Instant occurredAt,
    int attempt,
    String payload
) {
}