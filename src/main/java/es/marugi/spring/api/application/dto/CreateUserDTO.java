package es.marugi.spring.api.application.dto;

public record CreateUserDTO(String name, String login, String password, String email) {}