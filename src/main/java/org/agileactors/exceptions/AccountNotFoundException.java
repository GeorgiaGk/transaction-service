package org.agileactors.exceptions;

/**
 * Exception thrown when an account is not found.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Constructs an {@link AccountNotFoundException} with the specified error message.
     *
     * @param errorMessage The error message describing the reason for the exception.
     */
    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
