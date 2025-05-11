package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload for creating a new bank card.
 * Used by administrators to issue a new card to a user.
 */
@Data
@AllArgsConstructor
@Schema(description = "Payload for creating a new bank card")
public class CreateCardRequest {

    /**
     * Identifier of the card owner (user).
     */
    @Schema(description = "Unique identifier of the user",
            example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "ownerId must be provided")
    private UUID userId;

    /**
     * Unmasked card number (16 digits).
     */
    @Schema(description = "16-digit card number",
            example = "1234123412341234")
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String number;

    /**
     * Expiration date of the card. Must be a future date.
     */
    @Schema(description = "Expiry date of the card (must be in the future)",
            example = "2030-12-31")
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    /**
     * Initial balance to be assigned to the card.
     */
    @Schema(description = "Initial balance for the card",
            example = "100.00")
    @NotNull(message = "Initial balance must be provided")
    private BigDecimal initialBalance;
}
