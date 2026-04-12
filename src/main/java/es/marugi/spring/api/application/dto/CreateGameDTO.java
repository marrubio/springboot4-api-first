package es.marugi.spring.api.application.dto;

import java.time.LocalDateTime;

public record CreateGameDTO(
    String title,
    String description,
    Integer developmentYear,
    Double score
) {}

