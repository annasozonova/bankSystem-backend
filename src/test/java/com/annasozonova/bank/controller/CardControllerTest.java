package com.annasozonova.bank.controller;

import com.annasozonova.bank.dto.*;
import com.annasozonova.bank.model.CardStatus;
import com.annasozonova.bank.model.Role;
import com.annasozonova.bank.model.User;
import com.annasozonova.bank.repository.UserRepository;
import com.annasozonova.bank.security.CustomUserDetailsService;
import com.annasozonova.bank.security.UserPrincipal;
import com.annasozonova.bank.service.CardService;
import com.annasozonova.bank.service.UserService;
import com.annasozonova.bank.util.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean UserRepository userRepository;
    @MockitoBean CardService cardService;
    @MockitoBean UserService userService;

    @MockitoBean JwtUtil jwtUtil;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateCard() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateCardRequest req = new CreateCardRequest(
                userId,
                "1234123412345678",
                LocalDate.of(2030, 1, 1),
                new BigDecimal("100.00")
        );
        UUID cardId = UUID.randomUUID();
        CardDto dto = new CardDto(
                cardId,
                "**** **** **** 5678",
                req.getExpiryDate(),
                CardStatus.ACTIVE.name(),
                req.getInitialBalance()
        );
        when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 5678"))
                .andExpect(jsonPath("$.expiryDate").value("2030-01-01"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    void shouldGetCardById() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardDto dto = new CardDto(
                cardId,
                "**** **** **** 9999",
                LocalDate.of(2029, 12, 31),
                CardStatus.BLOCKED.name(),
                new BigDecimal("0.00")
        );
        when(cardService.getCardById(cardId)).thenReturn(dto);

        mockMvc.perform(get("/api/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void shouldUpdateCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        CreateCardRequest req = new CreateCardRequest(
                UUID.randomUUID(),
                "4321432143214321",
                LocalDate.of(2032, 3, 15),
                new BigDecimal("200.00")
        );
        CardDto updated = new CardDto(
                cardId,
                "**** **** **** 4321",
                req.getExpiryDate(),
                CardStatus.ACTIVE.name(),
                req.getInitialBalance()
        );
        when(cardService.updateCard(any(UUID.class), any(CreateCardRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/cards/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 4321"))
                .andExpect(jsonPath("$.balance").value(200.00));
    }

    @Test
    void shouldDeleteCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldListAllCardsForAdmin() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest(
                "admin@example.com", "SecurePass123", "Anna", "Sozonova"
        );

        UserDto mockUserDto = new UserDto(
                userId,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                Set.of("ROLE_ADMIN"),
                true
        );

        User mockEntity = User.builder()
                .id(userId)
                .email(request.getEmail())
                .passwordHash("bcrypt")
                .firstName("Anna")
                .lastName("Sozonova")
                .enabled(true)
                .roles(Set.of(new Role("ADMIN")))
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUserDto);
        when(userService.assignRoles(eq(userId), anySet())).thenReturn(mockUserDto);
        when(userService.getUserById(userId)).thenReturn(mockUserDto);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockEntity));

        UserPrincipal principal = new UserPrincipal(mockEntity);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        UUID cardId = UUID.randomUUID();
        CardDto dto = new CardDto(
                cardId,
                "**** **** **** 0001",
                LocalDate.of(2031, 6, 30),
                CardStatus.EXPIRED.name(),
                new BigDecimal("50.00")
        );
        Page<CardDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);
        when(cardService.getAllCards(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardId.toString()))
                .andExpect(jsonPath("$.content[0].status").value("EXPIRED"));
    }

    @Test
    void shouldListCardsForUser() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest(
                "user@example.com", "UserPass123", "Ivan", "Ivanov"
        );

        UserDto mockUserDto = new UserDto(
                userId,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                Set.of("ROLE_USER"),
                true
        );

        User mockEntity = User.builder()
                .id(userId)
                .email(request.getEmail())
                .passwordHash("bcrypt")
                .firstName("Ivan")
                .lastName("Ivanov")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockUserDto);
        when(userService.assignRoles(eq(userId), anySet())).thenReturn(mockUserDto);
        when(userService.getUserById(userId)).thenReturn(mockUserDto);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockEntity));

        UserPrincipal principal = new UserPrincipal(mockEntity);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        UUID cardId = UUID.randomUUID();
        CardDto dto = new CardDto(
                cardId,
                "**** **** **** 2222",
                LocalDate.of(2032, 1, 1),
                CardStatus.ACTIVE.name(),
                new BigDecimal("300.00")
        );
        Page<CardDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);
        when(cardService.getUserCards(eq(userId), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardId.toString()))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    void transferFunds_shouldReturnOkForUser() throws Exception {
        UUID userId = UUID.randomUUID();

        User mockUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .passwordHash("bcrypt")
                .firstName("Ivan")
                .lastName("Ivanov")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        UserPrincipal principal = new UserPrincipal(mockUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cardService).transferFunds(any(TransferRequest.class));
    }

    @Test
    void transferFunds_shouldReturnForbiddenForAdmin() throws Exception {
        UUID adminId = UUID.randomUUID();

        User mockAdmin = User.builder()
                .id(adminId)
                .email("admin@example.com")
                .passwordHash("bcrypt")
                .firstName("Admin")
                .lastName("admin")
                .enabled(true)
                .roles(Set.of(new Role("ADMIN")))
                .build();

        UserPrincipal principal = new UserPrincipal(mockAdmin);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void transferFunds_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        TransferRequest request = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transferFunds_shouldReturnBadRequest_whenAmountIsNegative() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        TransferRequest invalidRequest = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("-50.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferFunds_shouldReturnBadRequest_whenFromCardIdIsNull() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        TransferRequest invalidRequest = new TransferRequest(
                null,
                UUID.randomUUID(),
                new BigDecimal("10.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transferFunds_shouldReturnBadRequest_whenAmountIsNull() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        TransferRequest invalidRequest = new TransferRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestBlockCard_shouldSucceedForUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("Ivan")
                .lastName("Ivanov")
                .enabled(true)
                .roles(Set.of(new Role("USER")))
                .build();

        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );

        mockMvc.perform(post("/api/cards/{id}/block", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).requestBlockCard(eq(userId), eq(cardId));
    }

}