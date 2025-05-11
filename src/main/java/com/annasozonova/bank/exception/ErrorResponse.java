package com.annasozonova.bank.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Standard error response format for failed API requests.
 * <p>
 * Used by {@link GlobalExceptionHandler} to return structured JSON
 * in case of validation errors, business exceptions, or other failures.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * UTC timestamp of when the error occurred.
     */
    private OffsetDateTime timestamp;

    /**
     * HTTP status code (e.g., 400, 404, 500).
     */
    private int status;

    /**
     * Reason phrase of the HTTP status (e.g., "Bad Request").
     */
    private String error;

    /**
     * Human-readable error message.
     */
    private String message;

    /**
     * Field validation errors (field name → message).
     * Only present for {@code 400 Bad Request}.
     */
    private Map<String, String> errors;

    /**
     * Request path that caused the error (e.g., "/api/users").
     */
    private String path;
}
