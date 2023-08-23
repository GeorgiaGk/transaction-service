package org.agileactors.unit.services;

import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.AccountEntity;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.exceptions.AccountNotFoundException;
import org.agileactors.exceptions.InsufficientBalanceException;
import org.agileactors.exceptions.SameAccountTransferException;
import org.agileactors.exceptions.CurrencyMismatchException;
import org.agileactors.repositories.TransactionEntityRepository;
import org.agileactors.services.AccountServiceImpl;
import org.agileactors.services.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private AccountServiceImpl accountService;
    @Mock
    private TransactionEntityRepository transactionEntityRepository;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testPerformTransaction_ValidTransaction() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setCurrency(Currency.EUR);

        AccountEntity mockSourceAccount = new AccountEntity(1L, new BigDecimal("200.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        AccountEntity mockTargetAccount = new AccountEntity(2L, new BigDecimal("300.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(mockTargetAccount));

        assertDoesNotThrow(() -> transactionService.performTransaction(mockTransaction));

        verify(accountService, times(1)).updateAccountBalance(mockSourceAccount, new BigDecimal("100.00"));
        verify(accountService, times(1)).updateAccountBalance(mockTargetAccount, new BigDecimal("400.00"));
        verify(transactionEntityRepository, times(1)).save(mockTransaction);
    }

    @Test
    void testPerformTransaction_SameAccounts() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(1L); // Same account
        mockTransaction.setAmount(new BigDecimal("200.00"));
        mockTransaction.setCurrency(Currency.GBP);

        assertThrows(SameAccountTransferException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_SourceAccountNotFound() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("200.00"));
        mockTransaction.setCurrency(Currency.GBP);

        when(accountService.getAccountById(1L)).thenReturn(Optional.empty()); // Simulate account not found

        assertThrows(AccountNotFoundException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_TargetAccountNotFound() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("200.00"));
        mockTransaction.setCurrency(Currency.GBP);

        AccountEntity mockSourceAccount = new AccountEntity();
        mockSourceAccount.setId(1L);
        mockSourceAccount.setBalance(new BigDecimal("200.00"));
        mockSourceAccount.setCurrency(Currency.EUR);
        mockSourceAccount.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.empty()); // Simulate account not found

        assertThrows(AccountNotFoundException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_InsufficientBalance() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("200.00")); // Amount exceeds source account balance
        mockTransaction.setCurrency(Currency.EUR);

        AccountEntity mockSourceAccount = new AccountEntity(1L,new BigDecimal("100.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        AccountEntity mockTargetAccount = new AccountEntity(2L, new BigDecimal("300.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(mockTargetAccount));

        assertThrows(InsufficientBalanceException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_WrongTransactionCurrency() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setCurrency(Currency.USD); // Transaction currency is USD

        AccountEntity mockSourceAccount = new AccountEntity(1L, new BigDecimal("200.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        AccountEntity mockTargetAccount = new AccountEntity(2L, new BigDecimal("300.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(mockTargetAccount));

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_WrongSourceCurrency() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setCurrency(Currency.EUR); // Transaction currency is EUR

        AccountEntity mockSourceAccount = new AccountEntity(1L, new BigDecimal("200.00"), Currency.GBP, new Timestamp(System.currentTimeMillis()));

        AccountEntity mockTargetAccount = new AccountEntity(2L, new BigDecimal("300.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(mockTargetAccount));

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testPerformTransaction_WrongTargetCurrency() {
        TransactionEntity mockTransaction = new TransactionEntity();
        mockTransaction.setSourceAccountId(1L);
        mockTransaction.setTargetAccountId(2L);
        mockTransaction.setAmount(new BigDecimal("100.00"));
        mockTransaction.setCurrency(Currency.EUR); // Transaction currency is EUR

        AccountEntity mockSourceAccount = new AccountEntity(1L, new BigDecimal("200.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));

        AccountEntity mockTargetAccount = new AccountEntity(2L, new BigDecimal("300.00"), Currency.USD, new Timestamp(System.currentTimeMillis()));

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(mockSourceAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(mockTargetAccount));

        assertThrows(CurrencyMismatchException.class, () -> transactionService.performTransaction(mockTransaction));
    }

    @Test
    void testGetTransactionFound() {
        UUID mockTransactionId = UUID.randomUUID();

        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(mockTransactionId);
        transaction.setTargetAccountId(1L);
        transaction.setSourceAccountId(2L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setCurrency(Currency.EUR);

        when(transactionEntityRepository.findById(mockTransactionId)).thenReturn(Optional.of(transaction));

        Optional<TransactionResponseDto> result = transactionService.getTransaction(mockTransactionId);

        assertTrue(result.isPresent());
        assertEquals(mockTransactionId, result.get().getId());
        assertEquals(transaction.getSourceAccountId(), result.get().getSourceAccountId());
        assertEquals(transaction.getTargetAccountId(), result.get().getTargetAccountId());
        assertEquals(transaction.getAmount(), result.get().getAmount());
        assertEquals(transaction.getCurrency(), result.get().getCurrency());
    }

    @Test
    void testGetTransactionNotFound() {
        UUID mockTransactionId = UUID.randomUUID();

        when(transactionEntityRepository.findById(mockTransactionId)).thenReturn(Optional.empty());

        Optional<TransactionResponseDto> result = transactionService.getTransaction(mockTransactionId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllTransactions() {
        List<TransactionEntity> mockTransactions = new ArrayList<>();

        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setId(UUID.randomUUID());
        transaction1.setTargetAccountId(1L);
        transaction1.setSourceAccountId(2L);
        transaction1.setAmount(new BigDecimal("100.00"));
        transaction1.setCurrency(Currency.EUR);
        mockTransactions.add(transaction1);

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setId(UUID.randomUUID());
        transaction2.setTargetAccountId(3L);
        transaction2.setSourceAccountId(4L);
        transaction2.setAmount(new BigDecimal("50.00"));
        transaction2.setCurrency(Currency.GBP);
        mockTransactions.add(transaction2);

        when(transactionEntityRepository.findAll()).thenReturn(mockTransactions);

        List<TransactionResponseDto> result = transactionService.getAllTransactions();

        assertEquals(mockTransactions.size(), result.size());

        TransactionResponseDto resultTransaction1 = result.get(0);
        assertEquals(transaction1.getId(), resultTransaction1.getId());
        assertEquals(transaction1.getSourceAccountId(), resultTransaction1.getSourceAccountId());
        assertEquals(transaction1.getTargetAccountId(), resultTransaction1.getTargetAccountId());
        assertEquals(transaction1.getAmount(), resultTransaction1.getAmount());
        assertEquals(transaction1.getCurrency(), resultTransaction1.getCurrency());

        TransactionResponseDto resultTransaction2 = result.get(1);
        assertEquals(transaction2.getId(), resultTransaction2.getId());
        assertEquals(transaction2.getSourceAccountId(), resultTransaction2.getSourceAccountId());
        assertEquals(transaction2.getTargetAccountId(), resultTransaction2.getTargetAccountId());
        assertEquals(transaction2.getAmount(), resultTransaction2.getAmount());
        assertEquals(transaction2.getCurrency(), resultTransaction2.getCurrency());
    }
}
