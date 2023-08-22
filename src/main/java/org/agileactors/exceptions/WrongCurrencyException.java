package org.agileactors.exceptions;

/**
 * Exception thrown when a transaction involves incompatible currencies.
 */
public class WrongCurrencyException extends RuntimeException {

    /**
     * Constructs a {@link WrongCurrencyException} with the specified error message.
     *
     * @param errorMessage The error message describing the reason for the exception.
     */
    public WrongCurrencyException(String errorMessage) {
        super(errorMessage);
    }
}