package com.annasozonova.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity representing a transaction between two bank cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    /**
     * Unique identifier of the transaction.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Source card from which the funds are transferred.
     */
    @NotNull(message = "Source card must be provided")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_card_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_tx_from_card"))
    private Card fromCard;

    /**
     * Target card to which the funds are transferred.
     */
    @NotNull(message = "Target card must be provided")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_card_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_tx_to_card"))
    private Card toCard;

    /**
     * Amount of money transferred. Must be a positive value.
     */
    @NotNull(message = "Transfer amount must be provided")
    @DecimalMin(value = "0.00", message = "Transfer amount must be positive")
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Timestamp when the transfer occurred.
     * Automatically set before insert.
     */
    @NotNull
    @Column(name = "transfer_date", nullable = false, updatable = false)
    private OffsetDateTime transferDate;

    /**
     * Status of the transaction (e.g., COMPLETED, FAILED).
     */
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /**
     * Optional description of the transaction.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Automatically sets the transfer timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        this.transferDate = OffsetDateTime.now();
    }
}