package com.annasozonova.bank.exception;

/**
 * Exception indicating that a requested resource (e.g., user, card)
 * was not found in the database.
 * <p>
 * Used to return HTTP 404 Not Found responses in REST API.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new resource not found exception with a detailed message.
     *
     * @param message description of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
