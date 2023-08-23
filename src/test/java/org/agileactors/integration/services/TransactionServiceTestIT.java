package org.agileactors.integration.services;

import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.AccountEntity;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.exceptions.CurrencyMismatchException;
import org.agileactors.exceptions.InsufficientBalanceException;
import org.agileactors.exceptions.SameAccountTransferException;
import org.agileactors.repositories.AccountEntityRepository;
import org.agileactors.repositories.TransactionEntityRepository;
import org.agileactors.services.interfaces.AccountService;
import org.agileactors.services.interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTestIT {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionEntityRepository transactionEntityRepository;
    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @BeforeEach
    void setup() {
        accountEntityRepository.deleteAll();
        transactionEntityRepository.deleteAll();
    }

    @Test
    @DirtiesContext
    void testPerformTransaction_Successful() {
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        assertDoesNotThrow(() -> transactionService.performTransaction(transaction));

        // Check account balances
        sourceAccount = accountService.getAccountById(sourceAccount.getId()).orElseThrow();
        targetAccount = accountService.getAccountById(targetAccount.getId()).orElseThrow();

        assertEquals(new BigDecimal("800.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), targetAccount.getBalance());

        // Check transaction entity
        Optional<TransactionEntity> savedTransaction = transactionEntityRepository.findById(transaction.getId());
        assertTrue(savedTransaction.isPresent());
        assertEquals(transaction.getAmount(), savedTransaction.get().getAmount());
        assertEquals(transaction.getSourceAccountId(), savedTransaction.get().getSourceAccountId());
        assertEquals(transaction.getTargetAccountId(), savedTransaction.get().getTargetAccountId());
        assertEquals(transaction.getCurrency(), savedTransaction.get().getCurrency());
    }

    @Test
    void testPerformTransaction_SameAccountException() {
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(1L)
                .targetAccountId(1L)  // Same account
                .amount(new BigDecimal("100.00"))
                .currency(Currency.EUR)
                .build();

        assertThrows(SameAccountTransferException.class, () -> transactionService.performTransaction(transaction));
    }

    @Test
    @DirtiesContext
    void testPerformTransaction_InsufficientBalanceException() {
        // Create accounts
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("100.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        // Create transaction with higher amount than source account balance
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        assertThrows(InsufficientBalanceException.class, () -> transactionService.performTransaction(transaction));
    }

    @Test
    @DirtiesContext
    void testPerformTransaction_WrongTargetCurrencyException() {
        // Create accounts with different currencies
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.GBP, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        // Create transaction with EUR currency
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(transaction));
    }

    @Test
    @DirtiesContext
    void testPerformTransaction_WrongSourceCurrencyException() {
        // Create accounts with different currencies
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.GBP, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        // Create transaction with EUR currency
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(transaction));
    }

    @Test
    @DirtiesContext
    void testPerformTransaction_WrongTransactionCurrencyException() {
        // Create accounts with different currencies
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        // Create transaction with EUR currency
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(sourceAccount.getId())
                .targetAccountId(targetAccount.getId())
                .amount(new BigDecimal("200.00"))
                .currency(Currency.GBP)
                .build();

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(transaction));
    }

    @Test
    @DirtiesContext
    void testGetTransaction_Found() {
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.EUR)
                .build();

        transactionEntityRepository.save(transaction);

        Optional<TransactionResponseDto> retrievedTransaction = transactionService.getTransaction(transaction.getId());

        assertTrue(retrievedTransaction.isPresent());
        assertEquals(transaction.getId(), retrievedTransaction.get().getId());
        assertEquals(transaction.getSourceAccountId(), retrievedTransaction.get().getSourceAccountId());
        assertEquals(transaction.getTargetAccountId(), retrievedTransaction.get().getTargetAccountId());
        assertEquals(transaction.getAmount(), retrievedTransaction.get().getAmount());
        assertEquals(transaction.getCurrency(), retrievedTransaction.get().getCurrency());
    }

    @Test
    void testGetTransaction_NotFound() {
        Optional<TransactionResponseDto> retrievedTransaction = transactionService.getTransaction(UUID.randomUUID());

        assertTrue(retrievedTransaction.isEmpty());
    }

    @Test
    @DirtiesContext
    void testGetAllTransactions_ReturnsTwoTransactions() {
        TransactionEntity transaction1 = TransactionEntity.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency(Currency.EUR)
                .build();

        TransactionEntity transaction2 = TransactionEntity.builder()
                .sourceAccountId(3L)
                .targetAccountId(4L)
                .amount(new BigDecimal("50.00"))
                .currency(Currency.GBP)
                .build();

        transactionEntityRepository.saveAll(List.of(transaction1, transaction2));

        List<TransactionResponseDto> retrievedTransactions = transactionService.getAllTransactions();

        assertEquals(2, retrievedTransactions.size());
        assertEquals(transaction1.getId(), retrievedTransactions.get(0).getId());
        assertEquals(transaction1.getSourceAccountId(), retrievedTransactions.get(0).getSourceAccountId());
        assertEquals(transaction1.getTargetAccountId(), retrievedTransactions.get(0).getTargetAccountId());
        assertEquals(transaction1.getAmount(), retrievedTransactions.get(0).getAmount());
        assertEquals(transaction1.getCurrency(), retrievedTransactions.get(0).getCurrency());

        assertEquals(transaction2.getId(), retrievedTransactions.get(1).getId());
        assertEquals(transaction2.getSourceAccountId(), retrievedTransactions.get(1).getSourceAccountId());
        assertEquals(transaction2.getTargetAccountId(), retrievedTransactions.get(1).getTargetAccountId());
        assertEquals(transaction2.getAmount(), retrievedTransactions.get(1).getAmount());
        assertEquals(transaction2.getCurrency(), retrievedTransactions.get(1).getCurrency());
    }

    @Test
    void testGetAllTransactions_ReturnsNoTransactions() {
        List<TransactionResponseDto> retrievedTransactions = transactionService.getAllTransactions();

        assertEquals(0, retrievedTransactions.size());
    }
}
