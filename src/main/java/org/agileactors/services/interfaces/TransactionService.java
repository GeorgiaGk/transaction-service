package org.agileactors.services.interfaces;

import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing transaction-related operations.
 */
public interface TransactionService {

    /**
     * Performs a transaction using the provided {@link TransactionEntity}.
     *
     * @param transaction The {@link TransactionEntity} containing transaction details.
     */
    void performTransaction(TransactionEntity transaction);

    /**
     * Retrieves a {@link org.agileactors.dtos.TransactionResponseDto} by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return An {@link Optional} containing the {@link TransactionResponseDto}, or an empty Optional if not found.
     */
    Optional<TransactionResponseDto> getTransaction(UUID id);

    /**
     * Retrieves a list of all {@link TransactionResponseDto}.
     *
     * @return A {@link List} of {@link TransactionResponseDto}.
     */
    List<TransactionResponseDto> getAllTransactions();
}
