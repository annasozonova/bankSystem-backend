package com.annasozonova.bank.repository;

import com.annasozonova.bank.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email, eagerly fetching only their roles (not Role.users).
     *
     * @param email user's email
     * @return optional containing the user if found
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    /**
     * Find a user by ID, eagerly fetching only their roles (not Role.users)
     * @param id user's id
     * @return optional containing the user if found
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(UUID id);

    /**
     * Check if user with that email exists.
     *
     * @param email email to check, e.g., "USER"
     * @return true if user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
