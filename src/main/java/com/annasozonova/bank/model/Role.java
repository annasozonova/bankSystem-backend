package com.annasozonova.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

/**
 * Entity representing a security role (e.g., ROLE_USER, ROLE_ADMIN).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Unique identifier of the role.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Name of the role (e.g., ROLE_USER).
     */
    @NotNull(message = "Role name must be provided")
    @Size(min = 3, max = 50,
            message = "Role name must be between 3 and 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Convenience constructor to create a role with a generated ID.
     *
     * @param name the name of the role
     */
    public Role(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }
}
