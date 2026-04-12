package es.marugi.spring.api.application.dto;

import java.time.LocalDateTime;

public record GameDTO(
    Double score,
    Integer developmentYear,
    String description,
    String title,
    Long id,
    LocalDateTime recordedAt
    ) {}


