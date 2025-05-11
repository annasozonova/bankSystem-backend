package com.annasozonova.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Payload for creating a new user (registration).
 * Includes email, password, and personal name information.
 */
@Data
@AllArgsConstructor
@Schema(description = "Payload for creating a new user (registration)")
public class CreateUserRequest {

    /**
     * User's email address.
     */
    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * User's password.
     * Must be between 6 and 100 characters.
     */
    @Schema(description = "User's password (6–100 characters)",
            example = "P@ssw0rd")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100,
            message = "Password must be between 6 and 100 characters")
    private String password;

    /**
     * User's first name.
     */
    @Schema(description = "User's first name", example = "Anna")
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * User's last name.
     */
    @Schema(description = "User's last name", example = "Sozonova")
    @NotBlank(message = "Last name is required")
    private String lastName;
}
