package org.agileactors.exceptions;

/**
 * Exception thrown when a transaction involves incompatible currencies.
 */
public class CurrencyMismatchException extends RuntimeException {

    /**
     * Constructs a {@link CurrencyMismatchException} with the specified error message.
     *
     * @param errorMessage The error message describing the reason for the exception.
     */
    public CurrencyMismatchException(String errorMessage) {
        super(errorMessage);
    }
}