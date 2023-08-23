package org.agileactors.integration.services;

import org.agileactors.entities.AccountEntity;
import org.agileactors.enums.Currency;
import org.agileactors.repositories.AccountEntityRepository;
import org.agileactors.services.interfaces.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceTestIT {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @BeforeEach
    void before() {
        accountEntityRepository.deleteAll();
    }

    @Test
    @DirtiesContext
    void testGetAccountById_Found() {
        AccountEntity account = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        accountEntityRepository.save(account);

        Optional<AccountEntity> retrievedAccount = accountService.getAccountById(account.getId());

        assertTrue(retrievedAccount.isPresent());
        assertEquals(account.getId(), retrievedAccount.get().getId());
    }

    @Test
    @DirtiesContext
    void testGetAccountById_NotFound() {
        Optional<AccountEntity> retrievedAccount = accountService.getAccountById(2L);

        assertFalse(retrievedAccount.isPresent());
    }

    @Test
    @DirtiesContext
    void testUpdateAccountBalance() {
        AccountEntity account = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        accountEntityRepository.save(account);

        BigDecimal newBalance = new BigDecimal("1500.00");
        accountService.updateAccountBalance(account, newBalance);

        Optional<AccountEntity> updatedAccount = accountEntityRepository.findById(account.getId());

        assertTrue(updatedAccount.isPresent());
        assertEquals(newBalance, updatedAccount.get().getBalance());
    }
}
