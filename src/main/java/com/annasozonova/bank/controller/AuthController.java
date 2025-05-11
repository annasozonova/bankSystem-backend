package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.LoginRequest;
import com.annasozonova.bank.dto.LoginResponse;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.security.UserPrincipal;
import com.annasozonova.bank.service.UserService;
import com.annasozonova.bank.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controller for handling user registration and authentication.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user in the system.
     *
     * @param request request body containing user details
     * @return 201 Created with the created user data and Location header
     */
    @Operation(summary = "Register a new user",
            description = "Creates a new user account and returns the user data.")
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserDto created = userService.createUser(request);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.getId()))
                .body(created);
    }

    /**
     * Authenticates a user and returns a JWT access token.
     *
     * @param request request body containing email and password
     * @return JWT access token with token type
     */
    @Operation(summary = "Authenticate user",
            description = "Validates user credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(
            @Valid @RequestBody LoginRequest request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal.getId());

        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}