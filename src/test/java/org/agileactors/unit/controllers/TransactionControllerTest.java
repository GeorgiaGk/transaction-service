package org.agileactors.unit.controllers;

import org.agileactors.controllers.TransactionController;
import org.agileactors.dtos.TransactionRequestDto;
import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.exceptions.SameAccountTransferException;
import org.agileactors.services.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void testMakeTransaction() {
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("100.00"), Currency.EUR);

        UUID transactionId = UUID.randomUUID();

        doAnswer(invocation -> {
            TransactionEntity transaction = invocation.getArgument(0);
            transaction.setId(transactionId); // Simulate the behavior of assigning an ID
            return null; // performTransaction returns void
        }).when(transactionService).performTransaction(any(TransactionEntity.class));

        ResponseEntity<String> response = transactionController.makeTransaction(requestDto);

        assertEquals(ResponseEntity.created(URI.create("/transactions/" + transactionId)).body("Transaction successful"), response);
    }

    @Test
    void testMakeTransaction_BadRequestException() {
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 1L, new BigDecimal("100.00"), Currency.EUR);

        doThrow(new SameAccountTransferException("Same account transfer is not allowed.")).when(transactionService).performTransaction(any(TransactionEntity.class));

        assertThrows(SameAccountTransferException.class, () -> transactionController.makeTransaction(requestDto));
    }

    @Test
    void testMakeTransaction_InternalServerError() {
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("100.00"), Currency.EUR);

        doThrow(new RuntimeException("Something went wrong.")).when(transactionService).performTransaction(any(TransactionEntity.class));

        assertThrows(RuntimeException.class, () -> transactionController.makeTransaction(requestDto));
    }

    @Test
    void testGetAllTransactions() {
        TransactionResponseDto transaction1 = new TransactionResponseDto(
                UUID.randomUUID(),
                1L,
                2L,
                new BigDecimal("100.00"),
                Currency.EUR,
                new Timestamp(System.currentTimeMillis())
        );

        TransactionResponseDto transaction2 = new TransactionResponseDto(UUID.randomUUID(),
                3L,
                4L,
                new BigDecimal("50.00"),
                Currency.GBP,
                new Timestamp(System.currentTimeMillis())
        );

        List<TransactionResponseDto> transactions = List.of(transaction1, transaction2);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        ResponseEntity<List<TransactionResponseDto>> response = transactionController.getAllTransactions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void testGetTransactionFound() {
        UUID transactionId = UUID.randomUUID();

        TransactionResponseDto transaction = new TransactionResponseDto(
                transactionId,
                1L,
                2L,
                new BigDecimal("100.00"),
                Currency.EUR,
                new Timestamp(System.currentTimeMillis())
        );

        when(transactionService.getTransaction(transactionId)).thenReturn(Optional.of(transaction));

        ResponseEntity<TransactionResponseDto> response = transactionController.getTransaction(transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void testGetTransactionNotFound() {
        UUID mockTransactionId = UUID.randomUUID();

        when(transactionService.getTransaction(mockTransactionId)).thenReturn(Optional.empty());

        ResponseEntity<TransactionResponseDto> response = transactionController.getTransaction(mockTransactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
