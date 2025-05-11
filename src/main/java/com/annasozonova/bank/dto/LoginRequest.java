package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for user login (authentication request).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for user login (authentication request)")
public class LoginRequest {

    /**
     * User's email address.
     */
    @Schema(description = "User's email address",
            example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * User's password.
     */
    @Schema(description = "User's password",
            example = "P@ssw0rd")
    @NotBlank(message = "Password is required")
    private String password;
}
