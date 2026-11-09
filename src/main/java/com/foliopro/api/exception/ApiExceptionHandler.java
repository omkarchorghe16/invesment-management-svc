package com.foliopro.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    public record ApiError(Instant timestamp, int status, String error, String message,
                           Map<String, String> details) {
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<ApiError> notFound(NoSuchElementException exception, HttpServletRequest request) {
        logClientFailure(HttpStatus.NOT_FOUND.value(), "not found", request);
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException exception, HttpServletRequest request) {
        logClientFailure(HttpStatus.BAD_REQUEST.value(), "invalid argument", request);
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        logClientFailure(HttpStatus.BAD_REQUEST.value(), "validation", request);
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Request validation failed", details);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class})
    ResponseEntity<ApiError> malformed(Exception exception, HttpServletRequest request) {
        logClientFailure(HttpStatus.BAD_REQUEST.value(), "malformed request", request);
        return response(HttpStatus.BAD_REQUEST, "Invalid request parameter or JSON body", Map.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> statusException(ResponseStatusException exception, HttpServletRequest request) {
        logClientFailure(exception.getStatusCode().value(), "request rejected", request);
        return ResponseEntity.status(exception.getStatusCode()).body(new ApiError(
                Instant.now(), exception.getStatusCode().value(), exception.getStatusCode().toString(),
                exception.getReason(), Map.of()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled API exception: method={} path={}", request.getMethod(),
                sanitize(request.getRequestURI()), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", Map.of());
    }

    private void logClientFailure(int status, String category, HttpServletRequest request) {
        log.warn("API request failed: status={} category={} method={} path={}", status, category,
                request.getMethod(), sanitize(request.getRequestURI()));
    }

    private String sanitize(String value) {
        return value.replaceAll("[\\r\\n\\p{Cntrl}]", "?");
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(), message, details));
    }
}
