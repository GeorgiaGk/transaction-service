package org.agileactors.exceptions;

/**
 * Exception thrown when a transaction cannot be completed due to insufficient account balance.
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs an {@link InsufficientBalanceException} with the specified error message.
     *
     * @param errorMessage The error message describing the reason for the exception.
     */
    public InsufficientBalanceException(String errorMessage) {
        super(errorMessage);
    }
}