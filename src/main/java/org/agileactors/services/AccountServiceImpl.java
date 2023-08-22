package org.agileactors.services;

import lombok.AllArgsConstructor;
import org.agileactors.entities.AccountEntity;
import org.agileactors.repositories.AccountEntityRepository;
import org.agileactors.services.interfaces.AccountService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service implementation for managing account-related operations.
 */
@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountEntityRepository accountEntityRepository;

    /**
     * {@inheritDoc}
     */
    @Cacheable("accounts")
    @Override
    public Optional<AccountEntity> getAccountById(Long id) {
        return accountEntityRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccountBalance(AccountEntity account, BigDecimal newBalance) {
        account.setBalance(newBalance);
        accountEntityRepository.save(account);
    }
}
