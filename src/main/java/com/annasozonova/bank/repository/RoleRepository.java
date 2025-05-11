package com.annasozonova.bank.repository;

import com.annasozonova.bank.model.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link Role} entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Find a role by its name.
     *
     * @param name name of the role, e.g., "ROLE_USER"
     * @return optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if a role exists by its name.
     *
     * @param name name of the role to check, e.g., "USER"
     * @return true if a role with the given name exists, false otherwise
     */
    boolean existsByName(String name);
}