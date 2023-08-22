package org.agileactors.services.interfaces;

import org.agileactors.entities.AccountEntity;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for managing account-related operations.
 */
public interface AccountService {

    /**
     * Retrieves an account entity by its unique identifier.
     *
     * @param id The unique identifier of the account.
     * @return An {@link Optional} containing the {@link AccountEntity}, or an empty Optional if not found.
     */
    Optional<AccountEntity> getAccountById(Long id);

    /**
     * Updates the balance of an {@link AccountEntity}.
     *
     * @param account    The account entity to update.
     * @param newBalance The new balance value to set.
     */
    void updateAccountBalance(AccountEntity account, BigDecimal newBalance);
}
