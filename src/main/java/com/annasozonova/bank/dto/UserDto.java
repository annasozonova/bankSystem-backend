package com.annasozonova.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object representing user profile data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for User entity")
public class UserDto {

    /**
     * Unique identifier of the user.
     */
    @Schema(description = "Unique identifier of the user",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    /**
     * User's email address.
     */
    @Schema(description = "User's email address",
            example = "user@example.com")
    private String email;

    /**
     * User's first name.
     */
    @Schema(description = "User's first name",
            example = "Anna")
    private String firstName;

    /**
     * User's last name.
     */
    @Schema(description = "User's last name",
            example = "Sozonova")
    private String lastName;

    /**
     * Set of roles assigned to the user (e.g., ROLE_USER, ROLE_ADMIN).
     */
    @Schema(description = "User's roles",
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    /**
     * Indicates whether the user account is enabled.
     */
    @Schema(description = "Is user account enabled",
            example = "true")
    private boolean enabled;

    /**
     * Convenience constructor for newly registered users.
     * Roles are set to an empty set, and enabled is true by default.
     *
     * @param id        user ID
     * @param email     user email
     * @param firstName user first name
     * @param lastName  user last name
     */
    public UserDto(UUID id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = Collections.emptySet();
        this.enabled = true;
    }
}
