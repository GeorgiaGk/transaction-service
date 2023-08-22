package org.agileactors.repositories;

import lombok.NonNull;
import org.agileactors.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing account entities in the database.
 */
public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * Retrieves an account entity by its unique identifier.
     *
     * @param id The unique identifier of the account.
     * @return An {@link Optional} containing the {@link AccountEntity}, or an empty Optional if not found.
     */
    @NonNull
    Optional<AccountEntity> findById(@NonNull Long id);
}