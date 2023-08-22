package org.agileactors.unit.services;

import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.repositories.TransactionEntityRepository;
import org.agileactors.services.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionEntityRepository transactionEntityRepository;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testGetTransactionFound() {
        UUID mockTransactionId = UUID.randomUUID();

        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(mockTransactionId);
        transaction.setTargetAccountId(1L);
        transaction.setSourceAccountId(2L);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setCurrency(Currency.EUR);

        when(transactionEntityRepository.findById(mockTransactionId)).thenReturn(Optional.of(transaction));

        Optional<TransactionResponseDto> result = transactionService.getTransaction(mockTransactionId);

        assertTrue(result.isPresent());
        assertEquals(mockTransactionId, result.get().getId());
        assertEquals(transaction.getSourceAccountId(), result.get().getSourceAccountId());
        assertEquals(transaction.getTargetAccountId(), result.get().getTargetAccountId());
        assertEquals(transaction.getAmount(), result.get().getAmount());
        assertEquals(transaction.getCurrency(), result.get().getCurrency());
    }

    @Test
    void testGetTransactionNotFound() {
        UUID mockTransactionId = UUID.randomUUID();

        when(transactionEntityRepository.findById(mockTransactionId)).thenReturn(Optional.empty());

        Optional<TransactionResponseDto> result = transactionService.getTransaction(mockTransactionId);

        assertTrue(result.isEmpty());
    }
}
