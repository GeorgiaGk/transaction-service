package org.agileactors.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.agileactors.enums.Currency;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;


/**
 * Entity representing a transaction.
 */
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TRANSACTION")
public class TransactionEntity {

    /**
     * The unique identifier of the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The ID of the source account for the transaction.
     */
    @Column(nullable = false)
    private Long sourceAccountId;

    /**
     * The ID of the target account for the transaction.
     */
    @Column(nullable = false)
    private Long targetAccountId;

    /**
     * The amount involved in the transaction.
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * The {@link Currency} in which the transaction amount is specified.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * The timestamp when the transaction occurred.
     */
    @CreationTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Timestamp transactionDate;
}
