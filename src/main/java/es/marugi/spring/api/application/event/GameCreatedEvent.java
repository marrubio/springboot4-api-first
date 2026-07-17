package es.marugi.spring.api.application.event;

import java.time.Instant;
import java.util.UUID;

public record GameCreatedEvent(
    UUID eventId,
    Long gameId,
    Instant occurredAt,
    GameSnapshot snapshot
) implements GameEvent {
}