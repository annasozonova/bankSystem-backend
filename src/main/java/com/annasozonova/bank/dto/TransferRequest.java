package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request payload for transferring funds between user's cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for transferring funds between cards")
public class TransferRequest {

    /**
     * Identifier of the source card.
     */
    @Schema(description = "UUID of the source card",
            example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Source card ID must be provided")
    private UUID fromCardId;

    /**
     * Identifier of the target card.
     */
    @Schema(description = "UUID of the target card",
            example = "660e8400-e29b-41d4-a716-446655440111")
    @NotNull(message = "Target card ID must be provided")
    private UUID toCardId;

    /**
     * Amount to transfer. Must be a positive value.
     */
    @Schema(description = "Amount to transfer (positive value)",
            example = "50.00")
    @NotNull(message = "Transfer amount must be provided")
    @Positive(message = "Transfer amount must be positive")
    private BigDecimal amount;
}
