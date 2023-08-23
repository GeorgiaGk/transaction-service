package org.agileactors.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.agileactors.dtos.TransactionRequestDto;
import org.agileactors.dtos.TransactionResponseDto;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.services.TransactionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller class for managing transactions.
 */
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionServiceImpl transactionService;

    /**
     * Creates a new transaction based on the provided {@link TransactionRequestDto}.
     *
     * @param transactionRequest the transaction request containing the details of the transaction
     * @return a {@link ResponseEntity} with the URI of the created transaction and a success message
     */
    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<String> makeTransaction(@Valid @RequestBody TransactionRequestDto transactionRequest) {
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(transactionRequest.getSourceAccountId())
                .targetAccountId(transactionRequest.getTargetAccountId())
                .amount(transactionRequest.getAmount())
                .currency(transactionRequest.getCurrency())
                .build();

        transactionService.performTransaction(transaction);
        return ResponseEntity.created(URI.create("/transactions/" + transaction.getId())).body("Transaction successful");
    }

    /**
     * Retrieves all transactions.
     *
     * @return {@link List} of {@link TransactionResponseDto}
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions() {
        List<TransactionResponseDto> transactions = transactionService.getAllTransactions();

        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieves a transaction with the given transaction {@link UUID}.
     *
     * @param transactionId the {@link UUID} of the transaction
     * @return the {@link TransactionResponseDto} if found, otherwise a {@link ResponseEntity#notFound()} response
     */
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@PathVariable UUID transactionId) {
        Optional<TransactionResponseDto> response = transactionService.getTransaction(transactionId);
        return response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}