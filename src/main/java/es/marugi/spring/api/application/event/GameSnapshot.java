package es.marugi.spring.api.application.event;

import java.time.LocalDateTime;

public record GameSnapshot(
    Long id,
    String title,
    String description,
    Double score,
    Integer developmentYear,
    LocalDateTime recordedAt
) {
}