package org.agileactors.exceptions;

/**
 * Exception thrown when attempting to perform a transfer between the same source and target accounts.
 */
public class SameAccountTransferException extends RuntimeException {

    /**
     * Constructs a {@link SameAccountTransferException} with the specified error message.
     *
     * @param errorMessage The error message describing the reason for the exception.
     */
    public SameAccountTransferException(String errorMessage) {
        super(errorMessage);
    }
}
