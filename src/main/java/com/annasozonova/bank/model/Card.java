package com.annasozonova.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity representing a bank card.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cards")
public class Card {

    /**
     * Unique identifier of the card.
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID id;

    /**
     * Encrypted card number stored in binary format.
     */
    @NotNull
    @Column(name = "card_number_enc", nullable = false)
    private byte[] cardNumberEnc;

    /**
     * Masked version of the card number shown to the user (e.g., **** **** **** 1234).
     */
    @NotNull
    @Size(min = 19, max = 19)
    @Pattern(regexp = "\\*{4} \\*{4} \\*{4} \\*\\d{4}")
    @Column(name = "card_mask", nullable = false, unique = true, length = 19)
    private String cardMask;

    /**
     * Owner of the card.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_cards_users"))
    private User owner;

    /**
     * Expiration date of the card.
     */
    @NotNull
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    /**
     * Current status of the card (e.g., ACTIVE, BLOCKED, EXPIRED).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    /**
     * Current balance of the card. Must be a non-negative amount.
     */
    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    /**
     * Timestamp indicating when the card was created.
     */
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp indicating when the card was last updated.
     */
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Sets createdAt and updatedAt before inserting the record.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    /**
     * Updates the updatedAt timestamp before updating the record.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
