package com.annasozonova.bank.service;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param req user creation data
     * @return created user
     */
    UserDto createUser(CreateUserRequest req);

    /**
     * Returns a user by ID.
     *
     * @param userId user ID
     * @return found user
     * @throws ResourceNotFoundException if user does not exist
     */
    UserDto getUserById(UUID userId);

    /**
     * Returns all users with pagination.
     *
     * @param pageable pagination settings
     * @return paginated list of users
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Updates user details.
     *
     * @param userId user ID
     * @param req    updated data
     * @return updated user
     * @throws ResourceNotFoundException if user does not exist
     */
    UserDto updateUser(UUID userId, CreateUserRequest req);

    /**
     * Deletes a user.
     *
     * @param userId user ID
     * @throws ResourceNotFoundException if user does not exist
     */
    void deleteUser(UUID userId);

    /**
     * Returns a user by email.
     *
     * @param email user email
     * @return found user
     * @throws ResourceNotFoundException if user does not exist
     */
    UserDto getUserByEmail(String email);

    /**
     * Assigns roles to a user.
     *
     * @param userId    user ID
     * @param roleNames set of role names (e.g. "USER", "ADMIN")
     * @return updated user with assigned roles
     * @throws ResourceNotFoundException if user or any role does not exist
     */
    UserDto assignRoles(UUID userId, Set<String> roleNames);
}
