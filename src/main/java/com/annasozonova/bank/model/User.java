package com.annasozonova.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing an application user with login credentials and assigned roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * User's email address (used as login).
     */
    @NotNull(message = "Email must be provided")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * User's password stored as a bcrypt hash.
     * Always exactly 60 characters long.
     */
    @NotNull(message = "Password hash must be provided")
    @Size(min = 60, max = 60, message = "Password hash must be 60 characters")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's first name (optional).
     */
    @Size(max = 100, message = "First name must be up to 100 characters")
    @Column(name = "first_name", length = 100)
    private String firstName;

    /**
     * User's last name (optional).
     */
    @Size(max = 100, message = "Last name must be up to 100 characters")
    @Column(name = "last_name", length = 100)
    private String lastName;

    /**
     * Indicates whether the user account is active and enabled.
     */
    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    /**
     * Timestamp when the user account was created.
     * Automatically set on insert.
     */
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     * Automatically updated on every change.
     */
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Set of roles assigned to the user (e.g., ROLE_USER, ROLE_ADMIN).
     */
    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Automatically sets creation and update timestamps before insert.
     * Also ensures the account is enabled by default if not explicitly set.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = OffsetDateTime.now();
        if (this.enabled == null) {
            this.enabled = true;
        }
    }

    /**
     * Updates the timestamp before any update to the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}