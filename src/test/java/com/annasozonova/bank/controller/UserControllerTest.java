package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.CreateUserRequest;
import com.annasozonova.bank.dto.UserDto;
import com.annasozonova.bank.security.CustomUserDetailsService;
import com.annasozonova.bank.service.UserService;
import com.annasozonova.bank.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "john@example.com", "password123", "John", "Doe"
        );
        UUID id = UUID.randomUUID();
        UserDto dto = new UserDto(id, request.getEmail(), request.getFirstName(), request.getLastName());

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingUserWithInvalidData() throws Exception {
        String invalidJson = """
            {
              "password": "password123",
              "firstName": "John"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnPagedUsers() throws Exception {
        UUID id = UUID.randomUUID();
        UserDto dto = new UserDto(id, "john@example.com", "John", "Doe");
        PageRequest pageable = PageRequest.of(0, 10);

        when(userService.getAllUsers(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(dto), pageable, 1));

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        UUID id = UUID.randomUUID();
        UserDto dto = new UserDto(id, "john@example.com", "John", "Doe");

        when(userService.getUserById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest(
                "jane@example.com", "newpass", "Jane", "Smith"
        );
        UserDto updated = new UserDto(id, request.getEmail(), request.getFirstName(), request.getLastName());

        when(userService.updateUser(eq(id), any(CreateUserRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }
}