package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Controller for user management operations.
 * Accessible to administrators only.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations (ADMIN only)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param request the user creation request payload
     * @return 201 Created with the created user data
     */
    @Operation(summary = "Create new user",
            description = "Registers a new user (ADMIN only)")
    @ApiResponse(responseCode = "201",
            description = "User created successfully")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto created = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable pagination parameters
     * @return paginated list of users
     */
    @Operation(summary = "Get users",
            description = "Retrieve a paginated list of users (ADMIN only)")
    @ApiResponse(responseCode = "200",
            description = "List of users retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> page = userService.getAllUsers(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id the UUID of the user
     * @return user details
     */
    @Operation(summary = "Get user by ID",
            description = "Retrieve user details by user ID (ADMIN only)")
    @ApiResponse(responseCode = "200",
            description = "User retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        UserDto dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Updates an existing user.
     *
     * @param id      the UUID of the user
     * @param request updated user details
     * @return updated user information
     */
    @Operation(summary = "Update user",
            description = "Update user details by ID (ADMIN only)")
    @ApiResponse(responseCode = "200",
            description = "User updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request) {
        UserDto dto = userService.updateUser(id, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id UUID of the user to be deleted
     * @return 204 No Content if deletion is successful
     */
    @Operation(summary = "Delete user",
            description = "Delete user by ID (ADMIN only)")
    @ApiResponse(responseCode = "204",
            description = "User deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
