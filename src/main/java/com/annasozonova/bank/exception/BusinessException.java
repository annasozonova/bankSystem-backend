package com.annasozonova.bank.exception;

/**
 * Exception indicating a business rule violation.
 * <p>
 * Used to signal conditions such as invalid transfer,
 * insufficient balance, or unauthorized logical operations.
 * This is a controlled, expected exception, not a system failure.
 * </p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new business exception with the specified detail message.
     *
     * @param message human-readable message describing the violation
     */
    public BusinessException(String message) {
        super(message);
    }
}
