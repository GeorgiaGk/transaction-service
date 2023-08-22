package org.agileactors.unit.utils;

import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.utils.TransactionMapperUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionMapperUtilsTest {

    @Test
    void testMapTransactionEntityToTransactionResponseDto() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(UUID.randomUUID());
        transaction.setSourceAccountId(1L);
        transaction.setTargetAccountId(2L);
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setCurrency(Currency.USD);
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));

        TransactionResponseDto result = TransactionMapperUtils.mapTransactionEntityToTransactionResponseDto(transaction);

        assertEquals(transaction.getId(), result.getId());
        assertEquals(transaction.getSourceAccountId(), result.getSourceAccountId());
        assertEquals(transaction.getTargetAccountId(), result.getTargetAccountId());
        assertEquals(transaction.getAmount(), result.getAmount());
        assertEquals(transaction.getCurrency(), result.getCurrency());
        assertEquals(transaction.getTransactionDate(), result.getTransactionDate());
    }
}
