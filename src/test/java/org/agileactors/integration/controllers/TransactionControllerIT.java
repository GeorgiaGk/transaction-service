package org.agileactors.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.agileactors.dtos.TransactionRequestDto;
import org.agileactors.entities.AccountEntity;
import org.agileactors.entities.TransactionEntity;
import org.agileactors.enums.Currency;
import org.agileactors.repositories.AccountEntityRepository;
import org.agileactors.repositories.TransactionEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class TransactionControllerIT {
    private MockMvc mockMvc;

    @Autowired
    private TransactionEntityRepository transactionEntityRepository;

    @Autowired
    private AccountEntityRepository accountEntityRepository;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        transactionEntityRepository.deleteAll();
        accountEntityRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    void testMakeTransaction_returnsSuccessful() {
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("100.00"), Currency.EUR);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Transaction successful"));
    }

    @Test
    @SneakyThrows
    void testMakeTransaction_returnsBadRequest_SameAccountTransfer() {
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 1L, new BigDecimal("100.00"), Currency.GBP);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Source and target accounts are the same."));
    }

    @Test
    @SneakyThrows
    void testMakeTransaction_returnsBadRequest_AccountNotFound() {
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("100.00"), Currency.GBP);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Source or target account does not exist"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    void testMakeTransaction_returnsBadRequest_InsufficientBalance() {
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("100.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);
        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("200.00"), Currency.EUR);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance for the transaction"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    void testMakeTransaction_returnsBadRequest_CurrencyMismatch() {
        AccountEntity sourceAccount = new AccountEntity(1L, new BigDecimal("1000.00"), Currency.EUR, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(sourceAccount);

        AccountEntity targetAccount = new AccountEntity(2L, new BigDecimal("500.00"), Currency.GBP, new Timestamp(System.currentTimeMillis()));
        accountEntityRepository.save(targetAccount);

        TransactionRequestDto requestDto = new TransactionRequestDto(1L, 2L, new BigDecimal("100.00"), Currency.EUR);

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Source and target account currencies should be the same as the transaction currency"));
    }

    @Test
    @SneakyThrows
    void testGetAllTransactions_ReturnsEmptyList() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    void testGetAllTransactions_ReturnsTransactions() {
        TransactionEntity transaction1 = TransactionEntity.builder()
                .sourceAccountId(2L)
                .targetAccountId(4L)
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        TransactionEntity transaction2 = TransactionEntity.builder()
                .sourceAccountId(1L)
                .targetAccountId(3L)
                .amount(new BigDecimal("400.00"))
                .currency(Currency.GBP)
                .build();

        transactionEntityRepository.save(transaction1);
        transactionEntityRepository.save(transaction2);

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(transaction1.getId().toString())))
                .andExpect(jsonPath("$[0].sourceAccountId", is(transaction1.getSourceAccountId().intValue())))
                .andExpect(jsonPath("$[0].targetAccountId", is(transaction1.getTargetAccountId().intValue())))
                .andExpect(jsonPath("$[0].amount", is(transaction1.getAmount().doubleValue())))
                .andExpect(jsonPath("$[0].currency", is(transaction1.getCurrency().toString())))
                .andExpect(jsonPath("$[1].id", is(transaction2.getId().toString())))
                .andExpect(jsonPath("$[1].sourceAccountId", is(transaction2.getSourceAccountId().intValue())))
                .andExpect(jsonPath("$[1].targetAccountId", is(transaction2.getTargetAccountId().intValue())))
                .andExpect(jsonPath("$[1].amount", is(transaction2.getAmount().doubleValue())))
                .andExpect(jsonPath("$[1].currency", is(transaction2.getCurrency().toString())));
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    void testGetTransaction_Found() {
        TransactionEntity transaction = TransactionEntity.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();
        transactionEntityRepository.save(transaction);

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/" + transaction.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(transaction)));
    }

    @Test
    @SneakyThrows
    void testGetTransaction_NotFound() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
