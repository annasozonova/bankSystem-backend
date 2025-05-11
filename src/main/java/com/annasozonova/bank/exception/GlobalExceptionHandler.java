package com.annasozonova.bank.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for REST API.
 * Converts exceptions into structured {@link ErrorResponse} objects.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation failures for incoming request bodies.
     *
     * @param ex      exception containing field errors
     * @param request current HTTP request
     * @return 400 Bad Request with field error map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage()
                ));

        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation failed")
                .errors(errors)
                .path(request.getRequestURI())
                .build();
    }

    /**
     * Handles not-found exceptions (404).
     *
     * @param ex      resource-not-found exception
     * @param request current HTTP request
     * @return 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    /**
     * Handles access denial for forbidden operations (403).
     *
     * @param ex      forbidden operation exception
     * @param request current HTTP request
     * @return 403 Forbidden
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(
            ForbiddenOperationException ex,
            HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    /**
     * Handles domain-specific business logic errors (400).
     *
     * @param ex      business exception
     * @param request current HTTP request
     * @return 400 Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
    }

    /**
     * Handles authentication-related errors (401).
     *
     * @param ex      any Spring Security authentication exception
     * @param request current HTTP request
     * @return 401 Unauthorized
     */
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            AuthenticationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthError(
            Exception ex,
            HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .build();
    }

    /**
     * Handles uncaught or unexpected errors (500).
     *
     * @param ex      unexpected exception
     * @param request current HTTP request
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(
            Exception ex,
            HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Unexpected error")
                .path(request.getRequestURI())
                .build();
    }
}
