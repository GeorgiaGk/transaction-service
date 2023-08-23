package org.agileactors.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountEntityRepository accountEntityRepository;

    /**
     * {@inheritDoc}
     * We cache the accounts, so we don't have to query the database every time
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
        log.info("Updated balance for account={} to {}", account.getId(), newBalance);
        accountEntityRepository.save(account);
    }
}
