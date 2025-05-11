package com.annasozonova.bank.exception;

/**
 * Exception indicating that an operation is not permitted
 * for the current authenticated user or role.
 * <p>
 * Typically thrown when access control or authorization fails.
 * </p>
 */
public class ForbiddenOperationException extends RuntimeException {

    /**
     * Constructs a new forbidden operation exception with the given message.
     *
     * @param message explanation of the forbidden action
     */
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
