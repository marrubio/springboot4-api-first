package es.marugi.spring.api.application.event;

import java.time.Instant;
import java.util.UUID;

public sealed interface GameEvent permits GameCreatedEvent, GameUpdatedEvent, GameDeletedEvent {

    UUID eventId();

    Long gameId();

    Instant occurredAt();

    GameSnapshot snapshot();

    default int eventVersion() {
        return 1;
    }

    default String eventType() {
        return getClass().getSimpleName();
    }
}