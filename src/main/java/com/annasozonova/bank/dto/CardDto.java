package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object representing bank card details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for bank card")
public class CardDto {

    /**
     * Unique identifier of the card.
     */
    @Schema(description = "Unique identifier of the card",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    /**
     * Masked card number for display purposes (e.g., **** **** **** 1234).
     */
    @Schema(description = "Masked number for display (e.g., **** **** **** 1234)",
            example = "**** **** **** 1234")
    @NotNull
    @Size(min = 19, max = 19)
    @Pattern(regexp = "\\*{4} \\*{4} \\*{4} \\*\\d{4}")
    private String maskedNumber;

    /**
     * Expiration date of the card.
     */
    @Schema(description = "Expiration date of the card", example = "2025-12-31")
    @NotNull
    private LocalDate expiryDate;

    /**
     * Current status of the card: ACTIVE, BLOCKED, or EXPIRED.
     */
    @Schema(description = "Status of the card: ACTIVE, BLOCKED, or EXPIRED",
            example = "ACTIVE")
    @NotNull
    @Pattern(regexp = "ACTIVE|BLOCKED|EXPIRED")
    private String status;

    /**
     * Current card balance (must be non-negative).
     */
    @Schema(description = "Current balance of the card (must be ≥ 0)",
            example = "1000.00")
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal balance;
}
