package org.agileactors.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.AccountEntity;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.exceptions.AccountNotFoundException;
import org.agileactors.exceptions.InsufficientBalanceException;
import org.agileactors.exceptions.SameAccountTransferException;
import org.agileactors.exceptions.WrongCurrencyException;
import org.agileactors.repositories.TransactionEntityRepository;
import org.agileactors.services.interfaces.AccountService;
import org.agileactors.services.interfaces.TransactionService;
import org.agileactors.utils.TransactionMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final AccountService accountService;
    private final TransactionEntityRepository transactionEntityRepository;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void performTransaction(TransactionEntity transaction) {
        if (transaction.getSourceAccountId().equals(transaction.getTargetAccountId())) {
            log.error("Source and target accounts are the same. Source={}, Target={}.", transaction.getSourceAccountId(), transaction.getTargetAccountId());
            throw new SameAccountTransferException("Source and target accounts are the same.");
        }

        Optional<AccountEntity> sourceAccountOptional = accountService.getAccountById(transaction.getSourceAccountId());
        Optional<AccountEntity> targetAccountOptional = accountService.getAccountById(transaction.getTargetAccountId());

        if (sourceAccountOptional.isEmpty() || targetAccountOptional.isEmpty()) {
            log.error("Source or target account does not exist. Source={}, Target={}.", transaction.getSourceAccountId(), transaction.getTargetAccountId());
            throw new AccountNotFoundException("Source or target account does not exist");
        }

        AccountEntity sourceAccount = sourceAccountOptional.get();
        AccountEntity targetAccount = targetAccountOptional.get();

        if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            log.error("Insufficient balance for the transaction. Balance={}, transactionAmount={}", sourceAccount.getBalance(), transaction.getAmount());
            throw new InsufficientBalanceException("Insufficient balance for the transaction");
        }

        if (!transaction.getCurrency().equals(sourceAccount.getCurrency()) || !transaction.getCurrency().equals(targetAccount.getCurrency())) {
            log.error("Source and target account currencies should be the same as the transaction currency. " +
                    "sourceCurrency={}, targetCurrency={}, transactionCurrency={}",
                    sourceAccount.getCurrency(), targetAccount.getCurrency(), transaction.getCurrency());
            throw new WrongCurrencyException("Source and target account currencies should be the same as the transaction currency");
        }

        BigDecimal newSourceAccountBalance = sourceAccount.getBalance().subtract(transaction.getAmount());
        BigDecimal newTargetAccountBalance = targetAccount.getBalance().add(transaction.getAmount());
        accountService.updateAccountBalance(sourceAccount, newSourceAccountBalance);
        accountService.updateAccountBalance(targetAccount, newTargetAccountBalance);
        log.info("Balance successfully updated. newSourceAccountBalance={}, newTargetAccountBalance={}", newSourceAccountBalance, newTargetAccountBalance);

        transactionEntityRepository.save(transaction);
        log.info("Transaction successful from Source={} to Target={} for Amount={}", sourceAccount.getId(), targetAccount.getId(), transaction.getAmount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<TransactionResponseDto> getTransaction(UUID id) {
        Optional<TransactionEntity> transactionEntity = transactionEntityRepository.findById(id);

        return transactionEntity.map(TransactionMapperUtils::mapTransactionEntityToTransactionResponseDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionResponseDto> getAllTransactions() {
        List<TransactionEntity> transactions = transactionEntityRepository.findAll();
        return transactions.stream()
                .map(TransactionMapperUtils::mapTransactionEntityToTransactionResponseDto)
                .collect(Collectors.toList());
    }
}

