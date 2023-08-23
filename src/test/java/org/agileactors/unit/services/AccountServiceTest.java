package org.agileactors.unit.services;

import org.agileactors.entities.AccountEntity;
import org.agileactors.enums.Currency;
import org.agileactors.repositories.AccountEntityRepository;
import org.agileactors.services.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountEntityRepository accountEntityRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testGetAccountById_Found() {
        AccountEntity mockAccount = new AccountEntity(1L, new BigDecimal("100.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        when(accountEntityRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

        Optional<AccountEntity> account = accountService.getAccountById(1L);

        assertTrue(account.isPresent());
        assertEquals(mockAccount, account.get());
        assertEquals(mockAccount.getCreatedAt(), account.get().getCreatedAt());
    }

    @Test
    void testGetAccountById_NotFound() {
        when(accountEntityRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AccountEntity> account = accountService.getAccountById(1L);

        assertTrue(account.isEmpty());
    }

    @Test
    void testUpdateAccountBalance() {
        AccountEntity mockAccount = new AccountEntity(1L, new BigDecimal("100.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        BigDecimal newBalance = BigDecimal.valueOf(500.0);

        accountService.updateAccountBalance(mockAccount, newBalance);

        assertEquals(newBalance, mockAccount.getBalance());
    }
}
