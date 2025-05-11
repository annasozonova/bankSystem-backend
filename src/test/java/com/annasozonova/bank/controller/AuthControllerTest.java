package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.LoginRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.security.CustomUserDetailsService;
import com.annasozonova.bank.security.UserPrincipal;
import com.annasozonova.bank.service.UserService;
import com.annasozonova.bank.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "john@example.com", "password123", "John", "Doe"
        );
        UUID newUserId = UUID.randomUUID();
        UserDto createdDto = new UserDto(
                newUserId, request.getEmail(), request.getFirstName(), request.getLastName()
        );
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/" + newUserId))
                .andExpect(jsonPath("$.id").value(newUserId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturnBadRequestForInvalidRegisterRequest() throws Exception {
        String invalidJson = """
            {
              "password": "password123",
              "firstName": "John",
              "lastName": "Doe"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123");
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getId()).thenReturn(userId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        String token = "jwt-token";
        when(jwtUtil.generateToken(userId)).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "wrongpass");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
