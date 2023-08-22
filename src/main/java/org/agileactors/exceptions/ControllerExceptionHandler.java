package org.agileactors.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;

/**
 * Global exception handler for the controller layer.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Handles exceptions related to bad requests (e.g., SameAccountTransferException,
     * InsufficientBalanceException, WrongCurrencyException).
     *
     * @param ex The exception that occurred.
     * @return A {@link ResponseEntity} with a status of {@link HttpStatus#BAD_REQUEST} (400) and an error message.
     */
    @ExceptionHandler({SameAccountTransferException.class, InsufficientBalanceException.class, WrongCurrencyException.class})
    public ResponseEntity<String> handleBadRequestException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles exceptions related to account not found.
     *
     * @param ex The AccountNotFoundException that occurred.
     * @return A {@link ResponseEntity} with a status of {@link HttpStatus#NOT_FOUND} (404) and an error message.
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles general exceptions that are not explicitly caught.
     *
     * @param ex The general exception that occurred.
     * @return A {@link ResponseEntity} with a status of {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) and an error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}