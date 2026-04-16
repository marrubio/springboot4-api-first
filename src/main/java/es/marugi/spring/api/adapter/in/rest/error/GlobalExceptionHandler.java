package es.marugi.spring.api.adapter.in.rest.error;

import es.marugi.spring.api.application.exception.GameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .orElse("Request validation failed");

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGameNotFoundException(
        GameNotFoundException exception,
        HttpServletRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
        Exception exception,
        HttpServletRequest request
    ) {
        logger.error("Unhandled exception on {} {}: {}", request.getMethod(), request.getRequestURI(), exception.toString(), exception);
        return buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            request.getRequestURI()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
        HttpStatus status,
        String message,
        String path
    ) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}

