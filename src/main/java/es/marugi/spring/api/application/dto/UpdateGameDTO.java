package es.marugi.spring.api.application.dto;

public record UpdateGameDTO(
    String title,
    String description,
    Integer developmentYear,
    Double score
) {}

