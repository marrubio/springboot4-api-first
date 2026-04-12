package es.marugi.spring.api.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record GameResponseDTO(
    Long id,
    String title,
    String description,
    int score,
    int developmentYear,
    LocalDateTime recordedAt
) {}

