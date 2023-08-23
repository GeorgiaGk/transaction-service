package org.agileactors.unit.exceptions;

import org.agileactors.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ControllerExceptionHandlerTest {

    @InjectMocks
    private ControllerExceptionHandler exceptionHandler;

    @Mock
    private SameAccountTransferException sameAccountTransferException;

    @Mock
    private InsufficientBalanceException insufficientBalanceException;

    @Mock
    private CurrencyMismatchException currencyMismatchException;

    @Mock
    private AccountNotFoundException accountNotFoundException;

    @Mock
    private Exception generalException;

    @Test
    void testHandleBadRequestException_SameAccountException() {
        ResponseEntity<String> response = exceptionHandler.handleBadRequestException(sameAccountTransferException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleBadRequestException_InsufficientBalanceException() {
        ResponseEntity<String> response = exceptionHandler.handleBadRequestException(insufficientBalanceException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleBadRequestException_WrongCurrencyException() {
        ResponseEntity<String> response = exceptionHandler.handleBadRequestException(currencyMismatchException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleNotFoundException() {
        ResponseEntity<String> response = exceptionHandler.handleNotFoundException(accountNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testHandleInternalServerError() {
        ResponseEntity<String> response = exceptionHandler.handleInternalServerError(generalException);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
