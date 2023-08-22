package org.agileactors.repositories;

import lombok.NonNull;
import org.agileactors.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for accessing transaction entities in the database.
 */
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    /**
     * Retrieves a transaction entity by its unique identifier.
     *
     * @param id The unique identifier of the transaction.
     * @return An {@link Optional} containing the {@link TransactionEntity}, or an empty Optional if not found.
     */
    @NonNull
    Optional<TransactionEntity> findById(@NonNull UUID id);
}