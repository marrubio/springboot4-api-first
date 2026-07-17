package es.marugi.spring.api.application.dto;

public record CreateGameDTO(
    String title,
    String description,
    Integer developmentYear,
    Double score
) {}

