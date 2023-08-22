package org.agileactors.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agileactors.enums.Currency;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a transaction response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {

    /**
     * The {@link UUID} of the transaction.
     */
    private UUID id;

    /**
     * The ID of the source account for the transaction.
     */
    @NotNull(message = "sourceAccountId is required")
    private Long sourceAccountId;

    /**
     * The ID of the target account for the transaction.
     */
    @NotNull(message = "targetAccountId is required")
    private Long targetAccountId;

    /**
     * The amount transferred in the transaction. Minimum is 1.
     */
    @NotNull(message = "amount is required")
    @Min(value = 1, message = "amount to be transferred should be more than 0")
    private BigDecimal amount;

    /**
     * The {@link Currency} in which the transaction amount is specified.
     */
    @NotNull(message = "currency is required")
    private Currency currency;

    /**
     * The date and time when the transaction occurred in the format of "dd/MM/yyyy HH:mm:ss".
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp transactionDate;
}
