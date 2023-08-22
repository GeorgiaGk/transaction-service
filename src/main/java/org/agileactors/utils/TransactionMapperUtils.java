package org.agileactors.utils;

import lombok.experimental.UtilityClass;
import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;

/**
 * Utility class for mapping transaction entities to transaction response DTOs.
 */
@UtilityClass
public class TransactionMapperUtils {

    /**
     * Maps a {@link TransactionEntity} to a {@link TransactionResponseDto}.
     *
     * @param transaction The {@link TransactionEntity} to be mapped.
     * @return The corresponding {@link TransactionResponseDto}.
     */
    public static TransactionResponseDto mapTransactionEntityToTransactionResponseDto(TransactionEntity transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getTargetAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionDate()
        );
    }
}
