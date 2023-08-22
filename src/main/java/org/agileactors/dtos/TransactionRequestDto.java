package org.agileactors.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.agileactors.enums.Currency;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a transaction request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {

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
     * The amount to be transferred in the transaction. Minimum value is 1.
     */
    @NotNull(message = "amount is required")
    @Min(value = 1, message = "amount to be transferred should be more than 0")
    private BigDecimal amount;

    /**
     * The {@link Currency} in which the transaction amount is specified.
     */
    @NotNull(message = "currency is required")
    private Currency currency;
}